package com.github.zgynhqf.rafy4j;

import com.github.zgynhqf.rafy4j.data.DbSetting;
import com.github.zgynhqf.rafy4j.dbmigration.model.*;
import com.github.zgynhqf.rafy4j.dbmigration.providers.DbMigrationProviderFactory;
import com.github.zgynhqf.rafy4j.dbmigration.providers.DbTypeConverter;
import com.github.zgynhqf.rafy4j.env.EntityConvention;
import com.github.zgynhqf.rafy4j.metadata.EntityFieldMeta;
import com.github.zgynhqf.rafy4j.metadata.EntityMeta;
import com.github.zgynhqf.rafy4j.metadata.EntityMetaParser;
import com.github.zgynhqf.rafy4j.utils.PrimitiveType;
import com.github.zgynhqf.rafy4j.utils.TypeHelper;
import com.github.zgynhqf.rafy4j.utils.TypesSearcher;
import com.sun.javafx.scene.control.behavior.OptionalBoolean;

import java.lang.reflect.Modifier;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 从 Rafy 元数据中读取整个数据库的元数据。
 */
public class ClassMetaReader implements DestinationDatabaseReader {
    private DbSetting dbSetting;
    /**
     * 需要忽略的表的表名的集合。
     */
    private List<String> ignoreTables;
    private boolean isGeneratingForeignKey = true;
    private String[] entityPackages;
    private EntityMetaParser metaParser = new EntityMetaParser();

    public ClassMetaReader(DbSetting dbSetting, String... entityPackage) {
        this.dbSetting = dbSetting;
        ignoreTables = new ArrayList<>();

        this.entityPackages = entityPackage;
    }

    //region gs
    public final List<String> getIgnoreTables() {
        return ignoreTables;
    }

    /**
     * 是否生成外键，默认true
     */
    public final boolean isGeneratingForeignKey() {
        return isGeneratingForeignKey;
    }

    public boolean isMapCamelToUnderline() {
        return metaParser.isMapCamelToUnderline();
    }

    public void setMapCamelToUnderline(boolean mapCamelToUnderline) {
        metaParser.setMapCamelToUnderline(mapCamelToUnderline);
    }

    public boolean isMapAllEntitiesToTable() {
        return metaParser.isMapAllEntitiesToTable();
    }

    public void setMapAllEntitiesToTable(boolean mapAllEntitiesToTable) {
        metaParser.setMapAllEntitiesToTable(mapAllEntitiesToTable);
    }
    //
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
        List<EntityMeta> tableEntityTypes = this.getMappingEntityTypes();

        DestinationDatabase result = new DestinationDatabase(dbSetting.getDatabase());

        result.getIgnoreTables().addAll(ignoreTables);

        if (tableEntityTypes.isEmpty()) {
            result.setRemoved(true);
        } else {
            TypesMetaReader reader = this.createTypesMetaReader();
            reader.dbTypeConverter = DbMigrationProviderFactory.getDbTypeConverter(dbSetting.getDriverName());
            reader.database = result;
            reader.entities = tableEntityTypes;
            reader.setGeneratingForeignKey(this.isGeneratingForeignKey());
//            reader.setReadComment(this.getReadComment());
//            reader.setAdditionalPropertiesComments(this.getAdditionalPropertiesComments());
            reader.Read();
        }

        return result;
    }

    protected TypesMetaReader createTypesMetaReader() {
        return new TypesMetaReader();
    }

    private List<EntityMeta> getMappingEntityTypes() {
        List<EntityMeta> tableEntityTypes = new ArrayList<>();

        //程序集列表，生成数据库会反射找到程序集内的实体类型进行数据库映射
        Set<Class<?>> classes = TypesSearcher.getClasses(entityPackages);
        for (Class<?> type : classes) {
            int modifiers = type.getModifiers();
            if (!Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers)) {
                //判断实体类型是否映射了某一个数据库
                EntityMeta meta = metaParser.parse(type);

                tableEntityTypes.add(meta);
            }
        }

        return tableEntityTypes;
    }

    public static class TypesMetaReader {
        private DbTypeConverter dbTypeConverter;
        private DestinationDatabase database;
        private List<EntityMeta> entities;
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

            for (EntityMeta meta : entities) {
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

                //region 引用关系
                boolean isNullableRef = false;
//                if (columnMeta.HasFKConstraint) {
//                    var refProperty = (IRefProperty) ((mp instanceof IRefProperty) ? mp : null);
//                    if (refProperty != null) {
//                        isNullableRef = refProperty.Nullable;
//
//                        //是否生成外键
//                        // 默认 IsGeneratingForeignKey 为 true
//                        if (generatingForeignKey()) {
//                            var refMeta = em.Property(refProperty.RefEntityProperty);
//                            if (refMeta.ReferenceInfo == null) {
//                                throw new InvalidOperationException("refMeta.ReferenceInfo == null");
//                            }
//
//                            //引用实体的类型。
//                            var refTypeMeta = refMeta.ReferenceInfo.RefTypeMeta;
//                            if (refTypeMeta != null) {
//                                var refTableMeta = refTypeMeta.TableMeta;
//                                if (refTableMeta != null) {
//                                    //如果主键表已经被忽略，那么到这个表上的外键也不能建立了。
//                                    //这是因为被忽略的表的结构是未知的，不一定是以这个字段为主键。
//                                    if (!this.database.isIgnored(refTableMeta.TableName)) {
//                                        var id = refTypeMeta.Property(Entity.IdProperty);
//                                        //有时一些表的 Id 只是自增长，但并不是主键，不能创建外键。
//                                        if (id.ColumnMeta.IsPrimaryKey) {
//                                            ForeignConstraintInfo tempVar = new ForeignConstraintInfo();
//                                            tempVar.FkTableName = tableMeta.TableName;
//                                            tempVar.PkTableName = refTableMeta.TableName;
//                                            tempVar.FkColumn = columnName;
//                                            tempVar.PkColumn = id.ColumnMeta.ColumnName;
//                                            tempVar.NeedDeleteCascade = refProperty.ReferenceType == ReferenceType.Parent;
//                                            this.foreigns.add(tempVar);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    } else if (generatingForeignKey() && mp == Entity.TreePIdProperty) {
//                        var id = em.Property(Entity.IdProperty);
//                        //有时一些表的 Id 只是自增长，但并不是主键，不能创建外键。
//                        if (id.ColumnMeta.IsPrimaryKey) {
//                            ForeignConstraintInfo tempVar2 = new ForeignConstraintInfo();
//                            tempVar2.FkTableName = tableMeta.TableName;
//                            tempVar2.PkTableName = tableMeta.TableName;
//                            tempVar2.FkColumn = columnName;
//                            tempVar2.PkColumn = id.ColumnMeta.ColumnName;
//                            tempVar2.NeedDeleteCascade = false;
//                            this.foreigns.add(tempVar2);
//                        }
//                    }
//                }
                //endregion

                Class dataType = TypeHelper.ignoreOptional(fieldType);
                //对于支持多数据类型的 Id、TreePId 属性进行特殊处理。
//                if (mp == Entity.IdProperty || mp == Entity.TreePIdProperty) {
//                    dataType = em.IdType;
//                }
//                var dbType = columnMeta.DbType.GetValueOrDefault(dbTypeConverter.fromJreType(dataType));
                JDBCType dbType = dbTypeConverter.fromJreType(dataType);
                Column column = new Column(columnName, dbType, fieldMeta.getColumnLength(), table);
                if (fieldMeta.getIsNullable() != OptionalBoolean.ANY) {
                    column.setRequired(fieldMeta.getIsNullable() == OptionalBoolean.FALSE);
                } else {
                    //如果不是可空引用、可选类型、原生类型的类类型，则设置数据库字段为不可空。
                    if (!isNullableRef &&!TypeHelper.isOptional(fieldType)) {
                        PrimitiveType primitiveType = TypeHelper.getPrimitiveType(fieldType);
                        if (primitiveType == null || TypeHelper.isPrimitiveType(fieldType, primitiveType)) {
                            column.setRequired(true);
                        }
                    }
                }
                //IsPrimaryKey 的设置放在 IsRequired 之后，可以防止在设置可空的同时把列调整为非主键。
                if (fieldMeta.getIsPrimaryKey() != OptionalBoolean.ANY) {
                    column.setPrimaryKey(fieldMeta.getIsPrimaryKey() == OptionalBoolean.TRUE);
                } else {
                    column.setPrimaryKey(column.getName().equalsIgnoreCase(EntityConvention.IdColumnName));
                }
                if (fieldMeta.getIsAutoIncrement() != OptionalBoolean.ANY) {
                    column.setAutoIncrement(fieldMeta.getIsAutoIncrement() == OptionalBoolean.TRUE);
                } else {
                    column.setAutoIncrement(column.getName().equalsIgnoreCase(EntityConvention.IdColumnName));
                }
                column.setDefaultValue(fieldMeta.getDefaultValue());

                table.getColumns().add(column);

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
                //外键表必须找到，否则这个外键不会加入到集合中。
                Table fkTable = this.database.findTable(foreign.FkTableName);
                Column fkColumn = fkTable.findColumn(foreign.FkColumn);

                Table pkTable = this.database.findTable(foreign.PkTableName);
                //有可能这个引用的表并不在这个数据库中，此时不需要创建外键。
                if (pkTable != null) {
                    //找到主键列，创建引用关系
                    Column pkColumn = pkTable.findColumn(foreign.PkColumn);
                    ForeignConstraint foreignConstraint = new ForeignConstraint(pkColumn);
                    foreignConstraint.setNeedDeleteCascade(foreign.NeedDeleteCascade);
                    fkColumn.setForeignConstraint(foreignConstraint);
                }
            }
        }

        /**
         * 简单描述外键约束的信息，在表构建完成后，用些信息构造外键约束
         */
        private static class ForeignConstraintInfo {
            public String FkTableName, PkTableName, FkColumn, PkColumn;
            public boolean NeedDeleteCascade;
        }
    }
}