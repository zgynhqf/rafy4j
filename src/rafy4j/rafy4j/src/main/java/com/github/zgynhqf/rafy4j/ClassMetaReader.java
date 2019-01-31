package com.github.zgynhqf.rafy4j;

import com.github.zgynhqf.rafy4j.annotation.Reference;
import com.github.zgynhqf.rafy4j.annotation.ReferenceType;
import com.github.zgynhqf.rafy4j.data.DbSetting;
import com.github.zgynhqf.rafy4j.dbmigration.model.*;
import com.github.zgynhqf.rafy4j.dbmigration.providers.DbMigrationProviderFactory;
import com.github.zgynhqf.rafy4j.dbmigration.providers.DbTypeConverter;
import com.github.zgynhqf.rafy4j.env.EntityConvention;
import com.github.zgynhqf.rafy4j.metadata.EntityFieldMeta;
import com.github.zgynhqf.rafy4j.metadata.EntityMeta;
import com.github.zgynhqf.rafy4j.metadata.EntityMetaStore;
import com.github.zgynhqf.rafy4j.utils.AnnotationHelper;
import com.github.zgynhqf.rafy4j.utils.PrimitiveType;
import com.github.zgynhqf.rafy4j.utils.TypeHelper;
import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * 从 Rafy 元数据中读取整个数据库的元数据。
 */
public class ClassMetaReader implements DestinationDatabaseReader {
    private static Logger logger = LoggerFactory.getLogger(ClassMetaReader.class);

    private DbSetting dbSetting;
    /**
     * 需要忽略的表的表名的集合。
     */
    private List<String> ignoreTables;
    /**
     * 是否生成外键，默认true
     */
    private boolean generatingForeignKey = true;
    private EntityMetaStore entityMetaStore;

    public ClassMetaReader(DbSetting dbSetting, EntityMetaStore entityMetaStore) {
        this.dbSetting = dbSetting;
        ignoreTables = new ArrayList<>();
        this.entityMetaStore = entityMetaStore;
    }

    //region gs
    public final List<String> getIgnoreTables() {
        return ignoreTables;
    }

    public final boolean isGeneratingForeignKey() {
        return generatingForeignKey;
    }

    public void setGeneratingForeignKey(boolean generatingForeignKey) {
        this.generatingForeignKey = generatingForeignKey;
    }

    //    /**
//     * 是否需要同时读取出相应的注释。
//     */
//    private boolean privateReadComment;
//
//    public final boolean getReadComment() {
//        return privateReadComment;
//    }
//
//    public final void setReadComment(boolean value) {
//        privateReadComment = value;
//    }

//    /**
//     * 额外的一些属性注释的字典。
//     * Key:属性名。
//     * Value:注释值。
//     */
//    private HashMap<String, String> additionalPropertiesComments;
//
//    public final HashMap<String, String> getAdditionalPropertiesComments() {
//        return additionalPropertiesComments;
//    }
//
//    public final void setAdditionalPropertiesComments(HashMap<String, String> value) {
//        additionalPropertiesComments = value;
//    }
    //endregion

    /**
     * 读取整个类型对应的数据库的元数据。
     *
     * @return
     */
    public final DestinationDatabase read() {
        DestinationDatabase result = new DestinationDatabase(dbSetting.getDatabase());

        result.getIgnoreTables().addAll(ignoreTables);

        if (entityMetaStore.isEmpty()) {
            result.setRemoved(true);
        } else {
            TypesMetaReader reader = this.createTypesMetaReader();
            reader.dbTypeConverter = DbMigrationProviderFactory.getDbTypeConverter(dbSetting.getDriverName());
            reader.database = result;
            reader.entityMetaStore = entityMetaStore;
            reader.setGeneratingForeignKey(generatingForeignKey);
//            reader.setReadComment(this.getReadComment());
//            reader.setAdditionalPropertiesComments(this.getAdditionalPropertiesComments());
            reader.Read();
        }

        return result;
    }

    protected TypesMetaReader createTypesMetaReader() {
        return new TypesMetaReader();
    }

    public static class TypesMetaReader {
        private DbTypeConverter dbTypeConverter;
        private DestinationDatabase database;
        private EntityMetaStore entityMetaStore;
        /**
         * 是否生成外键，默认true
         */
        private boolean generatingForeignKey = true;
        /**
         * 临时存储在这个列表中，最后再整合到 database 中。
         */
        private List<ForeignConstraintInfo> foreigns = new ArrayList<>();

        //region ignore
        //        private boolean _readComment;
//        /**
//         * 额外的一些属性注释的字典。
//         * Key:属性名。
//         * Value:注释值。
//         */
//        private HashMap<String, String> additionalPropertiesComments;
//
//        private CommentFinder _commentFinder = new CommentFinder();
//
//        public final boolean getReadComment() {
//            return _readComment;
//        }
//
//        public final void setReadComment(boolean value) {
//            _readComment = value;
//        }

//        public final Map<String, String> getAdditionalPropertiesComments() {
//            return additionalPropertiesComments;
//        }
//
//        public final void setAdditionalPropertiesComments(HashMap<String, String> value) {
//            additionalPropertiesComments = value;
//        }
        //endregion

        //region gs
        public final boolean isGeneratingForeignKey() {
            return generatingForeignKey;
        }

        public final void setGeneratingForeignKey(boolean value) {
            generatingForeignKey = value;
        }
        //endregion

        public final void Read() {
//            _commentFinder.setAdditionalPropertiesComments(this.getAdditionalPropertiesComments());

            for (EntityMeta meta : entityMetaStore.values()) {
                this.BuildTable(meta);
            }

            //在所有关系完成之后在创建外键的元数据
            this.buildFKRelations();
        }

        /**
         * 根据实体类型创建表的描述信息，并添加到数据库中
         */
        private void BuildTable(EntityMeta em) {
            if (!em.isMappingTable()) return;

            Table table = new Table(em.getTableName(), this.database);

//            //读取实体的注释
//            if (_readComment) {
//                table.Comment = _commentFinder.TryFindComment(em.EntityType);
//            }

            for (EntityFieldMeta fieldMeta : em.getFields()) {
                if (!fieldMeta.isMappingColumn()) continue;

                //列名
                String columnName = fieldMeta.getColumnName();

                //类型
                Class<?> fieldType = fieldMeta.getField().getType();

                Class dataType = TypeHelper.ignoreOptional(fieldType);
                //对于支持多数据类型的 Id、TreePId 属性进行特殊处理。
//                if (mp == Entity.IdProperty || mp == Entity.TreePIdProperty) {
//                    dataType = em.IdType;
//                }
//                var dbType = columnMeta.DbType.GetValueOrDefault(dbTypeConverter.fromJreType(dataType));
                JDBCType dbType = dbTypeConverter.fromJreType(dataType);
                if (dbType == null) {
//                    logger.info("{} 不支持映射类型为 {} 的字段，名为 {} 的字段将会被忽略。", dbTypeConverter.getClass(), dataType ,fieldMeta.getName());
                    continue;
                }

                //是否为约定的 Id 列的列名。
                boolean isIdCovention = columnName.equalsIgnoreCase(EntityConvention.IdColumnName);

                Column column = new Column(columnName, dbType, fieldMeta.getColumnLength(), table);
                if (fieldMeta.getIsNullable() != OptionalBoolean.ANY) {
                    column.setRequired(fieldMeta.getIsNullable() == OptionalBoolean.FALSE);
                } else {
                    //如果不是可空引用、可选类型、原生类型的类类型，则设置数据库字段为不可空。
//                    if (!isNullableRef && !TypeHelper.isOptional(fieldType)) {
                    PrimitiveType primitiveType = TypeHelper.getPrimitiveType(fieldType);
                    if (primitiveType != null && TypeHelper.isPrimitiveType(fieldType, primitiveType)) {
                        column.setRequired(true);
                    }
//                    }
                }
                //IsPrimaryKey 的设置放在 IsRequired 之后，可以防止在设置可空的同时把列调整为非主键。
                if (fieldMeta.getIsPrimaryKey() != OptionalBoolean.ANY) {
                    column.setPrimaryKey(fieldMeta.getIsPrimaryKey() == OptionalBoolean.TRUE);
                } else {
                    column.setPrimaryKey(isIdCovention);
                }
                if (fieldMeta.getIsAutoIncrement() != OptionalBoolean.ANY) {
                    column.setAutoIncrement(fieldMeta.getIsAutoIncrement() == OptionalBoolean.TRUE);
                } else {
                    column.setAutoIncrement(isIdCovention);
                }
                column.setDefaultValue(fieldMeta.getDefaultValue());

                table.getColumns().add(column);

                //region 引用关系
                if (generatingForeignKey) {
                    Reference annotation = AnnotationHelper.findAnnotation(fieldMeta.getField(), Reference.class);
                    if (annotation != null) {
                        ForeignConstraintInfo fcInfo = new ForeignConstraintInfo();
                        fcInfo.FKColumn = column;
                        fcInfo.Annotation = annotation;
                        this.foreigns.add(fcInfo);
                    }
                }
                //endregion

                this.onColumnCreated(column, fieldMeta);

//                //读取属性的注释。
//                if (_readComment) {
//                    var commentProperty = mp;
//                    var refProperty = (IRefProperty) ((commentProperty instanceof IRefProperty) ? commentProperty : null);
//                    if (refProperty != null) {
//                        commentProperty = refProperty.RefEntityProperty;
//                    }
//
//                    column.Comment = _commentFinder.TryFindComment(commentProperty);
//                }
            }

            table.sortColumns();

            this.addTable(table);
        }

        /**
         * 子类可在此方法中重写 column 默认值的一些逻辑。
         *
         * @param column
         * @param fieldMeta
         */
        protected void onColumnCreated(Column column, EntityFieldMeta fieldMeta) {
            //for sub classes
        }

        /**
         * 将表添加到数据库中，对于已经存在的表进行全并
         *
         * @param table
         */
        private void addTable(Table table) {
            Table existingTable = this.database.findTable(table.getName());
            if (existingTable != null) {
                //由于有类的继承关系存在，合并两个表的所有字段。
                for (Column column : table.getColumns()) {
                    if (existingTable.findColumn(column.getName()) == null) {
                        Column newColumn = new Column(column.getName(), column.getDbType(), column.getLength(), existingTable);
                        newColumn.setRequired(column.isRequired());
                        newColumn.setPrimaryKey(column.isPrimaryKey());
                        newColumn.setAutoIncrement(column.isAutoIncrement());
                        existingTable.getColumns().add(newColumn);
                    }
                }
            } else {
                this.database.getTables().add(table);
            }
        }

        /**
         * 构造外键的描述，并创建好与数据库、表、列的相关依赖关系
         */
        private void buildFKRelations() {
            for (ForeignConstraintInfo foreign : this.foreigns) {
                //引用实体的类型。
                EntityMeta refTypeMeta = entityMetaStore.getEntityMeta(foreign.Annotation.entity());
                if (refTypeMeta == null || !refTypeMeta.isMappingTable()) continue;

                //如果主键表已经被忽略，那么到这个表上的外键也不能建立了。
                //这是因为被忽略的表的结构是未知的，不一定是以这个字段为主键。
                String pkTableName = refTypeMeta.getTableName();
                if (this.database.isIgnored(pkTableName)) continue;

                //有可能这个引用的表并不在这个数据库中，此时不需要创建外键。
                Table pkTable = this.database.findTable(pkTableName);
                if (pkTable == null) continue;

                //找到主键列，创建引用关系
                Column pkColumn = pkTable.findPrimaryColumn();
                if (pkColumn == null) continue;

                boolean needDeleteCascade = foreign.Annotation.referenceType() == ReferenceType.AGGREGATION_PARENT;

                ForeignConstraint foreignConstraint = new ForeignConstraint(pkColumn);
                foreignConstraint.setNeedDeleteCascade(needDeleteCascade);
                foreign.FKColumn.setForeignConstraint(foreignConstraint);
            }
        }

        /**
         * 简单描述外键约束的信息，在表构建完成后，用些信息构造外键约束
         */
        private static class ForeignConstraintInfo {
            Column FKColumn;
            Reference Annotation;
        }
    }
}