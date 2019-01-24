package com.github.zgynhqf.rafy4j.mybatisplus.plugins.stamp;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.github.zgynhqf.rafy4j.env.RafyEnvironment;
import com.github.zgynhqf.rafy4j.metadata.EntityFieldMeta;
import com.github.zgynhqf.rafy4j.metadata.EntityMeta;
import com.github.zgynhqf.rafy4j.metadata.EntityMetaStore;
import com.github.zgynhqf.rafy4j.utils.LambdaUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author: huqingfang
 * @date: 2019-01-23 11:05
 **/
public class StampSqlParser implements ISqlParser {
    private String fieldNameCreateTime, fieldNameCreator, fieldNameUpdateTime, fieldNameUpdater;
    /**
     * 需要创建时间戳的数据表。
     * Key：表名。
     * Value：对应实体的元数据。
     */
    private Map<String, EntityMeta> stampAwareEntities;

    private void lazyInit() {
        if (stampAwareEntities == null) {
            synchronized (this) {
                if (stampAwareEntities == null) {
                    //init cache
                    Map<String, EntityMeta> cache = new HashMap<>(20);

                    EntityMetaStore metaStore = RafyEnvironment.getBean(EntityMetaStore.class);
                    Collection<EntityMeta> entityMetas = metaStore.values();
                    for (EntityMeta entityMeta : entityMetas) {
                        if (CreateStampAware.class.isAssignableFrom(entityMeta.getType()) ||
                                UpdateStampAware.class.isAssignableFrom(entityMeta.getType())) {
                            cache.put(entityMeta.getTableName(), entityMeta);
                        }
                    }

                    //init 4 field names;
                    fieldNameCreateTime = LambdaUtils.resolveFieldName(CreateStampAware::getCreateTime);
                    fieldNameCreator = LambdaUtils.resolveFieldName(CreateStampAware::getCreator);
                    fieldNameUpdateTime = LambdaUtils.resolveFieldName(UpdateStampAware::getUpdateTime);
                    fieldNameUpdater = LambdaUtils.resolveFieldName(UpdateStampAware::getUpdater);

                    stampAwareEntities = cache;
                }
            }
        }
    }

    @Override
    public SqlInfo parser(MetaObject metaObject, String sql) {
        if (!this.allowProcess(metaObject)) return null;

        lazyInit();

        try {
            List<Statement> statements = CCJSqlParserUtil.parseStatements(sql).getStatements();

            StringBuilder sqlBuff = new StringBuilder();
            for (int i = 0; i < statements.size(); i++) {
                Statement statement = statements.get(i);
                if (statement != null) {
                    if (i++ > 0) {
                        sqlBuff.append(';');
                    }
                    this.process(statement, metaObject);
                    sqlBuff.append(statement.toString());
                }
            }
            if (sqlBuff.length() > 0) {
                return SqlInfo.newInstance().setSql(sqlBuff.toString());
            }
        } catch (JSQLParserException e) {
            throw ExceptionUtils.mpe("Failed to process, please exclude the tableName or statementId.\n Error SQL: %s", e, sql);
        }

        return null;
    }

    protected boolean allowProcess(MetaObject metaObject) {
        MappedStatement ms = SqlParserHelper.getMappedStatement(metaObject);
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (sqlCommandType == SqlCommandType.INSERT ||
                sqlCommandType == SqlCommandType.UPDATE) {
            return true;
        }

        return false;
    }

    protected void process(Statement statement, MetaObject metaObject) {
        if (statement instanceof Insert) {
            this.processInsert((Insert) statement);
        } else if (statement instanceof Update) {
            this.processUpdate((Update) statement, metaObject);
        }
    }

    protected void processInsert(Insert statement) {
        String tableName = statement.getTable().getName();
        EntityMeta entityMeta = stampAwareEntities.get(tableName);
        if (entityMeta == null) return;

        boolean needCreateStamp = CreateStampAware.class.isAssignableFrom(entityMeta.getType());
        boolean needUpdateStamp = UpdateStampAware.class.isAssignableFrom(entityMeta.getType());
        if (!needCreateStamp && !needUpdateStamp) return;

        Expression dateValue = getDateValue();
        Expression principleValue = getPrincipleValue();

        if (needCreateStamp) {
            setInsertColumn(statement, entityMeta.getField(fieldNameCreateTime), dateValue);
            setInsertColumn(statement, entityMeta.getField(fieldNameCreator), principleValue);
        }
        if (needUpdateStamp) {
            setInsertColumn(statement, entityMeta.getField(fieldNameUpdateTime), dateValue);
            setInsertColumn(statement, entityMeta.getField(fieldNameUpdater), principleValue);
        }
    }

    protected void processUpdate(Update statement, MetaObject metaObject) {
        String tableName = statement.getTables().get(0).getName();
        EntityMeta entityMeta = stampAwareEntities.get(tableName);
        if (entityMeta == null) return;

        boolean needUpdateStamp = UpdateStampAware.class.isAssignableFrom(entityMeta.getType());
        if (!needUpdateStamp) return;

        Expression dateValue = getDateValue();
        Expression principleValue = getPrincipleValue();
//        StatementHandler statementHandler = (StatementHandler) metaObject.getValue("delegate");
//        List<ParameterMapping> parameterMappings = statementHandler.getBoundSql().getParameterMappings();

        setUpdateColumn(statement, entityMeta.getField(fieldNameUpdateTime), dateValue, null);
        setUpdateColumn(statement, entityMeta.getField(fieldNameUpdater), principleValue, null);
//
//        //setUpdateColumn 中如果替换了参数，只会设置为 null，这里需要真正的清空它们。
//        for (int i = parameterMappings.size() - 1; i >= 0; i--) {
//            ParameterMapping parameterMapping = parameterMappings.get(i);
//            if (parameterMapping == null) {
//                parameterMappings.remove(i);
//            }
//        }
    }

    private static void setInsertColumn(Insert statement, EntityFieldMeta fieldMeta, Expression value) {
        if (!fieldMeta.isMappingColumn()) return;

        List<Expression> itemsList = ((ExpressionList) statement.getItemsList()).getExpressions();
        List<Column> columns = statement.getColumns();

        String columnName = fieldMeta.getColumnName();

        //由于这两值都是引用类型，那么如果已经存在值的设置，那么就不再赋值。
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if (column.getColumnName().equalsIgnoreCase(columnName)) {
                return;
            }
        }

        columns.add(new Column(columnName));
        itemsList.add(value);
    }

    private static void setUpdateColumn(Update statement, EntityFieldMeta fieldMeta, Expression value, List<ParameterMapping> parameterMappings) {
        if (!fieldMeta.isMappingColumn()) return;

        String columnName = fieldMeta.getColumnName();
        List<Column> columns = statement.getColumns();
        List<Expression> expressions = statement.getExpressions();

        //如果已经存在此列，则直接更新为新的列。
//        for (int i = 0; i < columns.size(); i++) {
//            Column column = columns.get(i);
//            if (column.getColumnName().equalsIgnoreCase(columnName)) {
//                //set new value.
//                expressions.set(i, value);
//                //remove parameterMapping
//                //由于索引有可能位置会出错，所以先设置为 null，当两个属性都清除完后，再从集合中删除。
//                parameterMappings.set(i, null);
//
//                // 通过名称去匹配，不好处理。
//                // parameterMapping.getProperty() 的值是 et.updateTime，而 columnName 是 update_time。
////                for (int j = 0; j < parameterMappings.size(); j++) {
////                    ParameterMapping parameterMapping = parameterMappings.get(j);
////                    if(parameterMapping.getProperty().equalsIgnoreCase(columnName)){
////                        parameterMappings.remove(j);
////                        break;
////                    }
////                }
//                return;
//            }
//        }

        //不用再去重。因为 update 语句中允许重复，且后者会生效。
        columns.add(new Column(columnName));
        expressions.add(value);
    }

    private static Expression getDateValue() {
        String value = new Timestamp(new Date().getTime()).toString();
        return new TimestampValue(value);
    }

    private static Expression getPrincipleValue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principle = authentication != null ? authentication.getName() : "";
        return new StringValue(principle);
    }
}