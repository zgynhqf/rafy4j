package com.github.zgynhqf.rafy4j.metadata;

import com.github.zgynhqf.rafy4j.annotation.IgnoreMapping;
import com.github.zgynhqf.rafy4j.annotation.MappingColumn;
import com.github.zgynhqf.rafy4j.annotation.MappingTable;
import com.github.zgynhqf.rafy4j.utils.AnnotationHelper;
import com.github.zgynhqf.rafy4j.utils.NameUtils;
import com.github.zgynhqf.rafy4j.utils.TypeHelper;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * 实体元数据的解析器。
 *
 * @author: huqingfang
 * @date: 2018-12-30 22:09
 **/
public class EntityMetaParser {
    /**
     * 是否映射驼峰到下划线。
     */
    private boolean mapCamelToUnderline = true;
    private boolean mapAllEntitiesToTable = true;

    //region gs
    public boolean isMapCamelToUnderline() {
        return mapCamelToUnderline;
    }

    public void setMapCamelToUnderline(boolean mapCamelToUnderline) {
        this.mapCamelToUnderline = mapCamelToUnderline;
    }

    public boolean isMapAllEntitiesToTable() {
        return mapAllEntitiesToTable;
    }

    public void setMapAllEntitiesToTable(boolean mapAllEntitiesToTable) {
        this.mapAllEntitiesToTable = mapAllEntitiesToTable;
    }
    //endregion

    /**
     * 通过反射，来解析指定的类型所对应的关键元数据。
     *
     * @param type
     * @return
     */
    public EntityMeta parse(Class<?> type) {
        EntityMeta meta = new EntityMeta(type);

        boolean ignoreMapping = AnnotationHelper.findAnnotation(type, IgnoreMapping.class) != null;
        if (!ignoreMapping) {
            boolean mappingTable = mapAllEntitiesToTable;

            MappingTable annotation = AnnotationHelper.findAnnotation(type, MappingTable.class);
            if (annotation != null) {
                meta.setTableName(annotation.name());

                meta.setMapAllFieldsToColumn(annotation.mapAllFieldsToColumn());

                mappingTable = true;
            }

            //默认映射的表名。
            if (mappingTable) {
                if (StringUtils.isBlank(meta.getTableName())) {
                    String name = type.getSimpleName();
                    if (mapCamelToUnderline) {
                        name = NameUtils.camelToUnderline(name);
                    }
                    meta.setTableName(name);
                }
            }
        }

        parseProperties(meta, type);

        return meta;
    }

    private void parseProperties(EntityMeta meta, Class<?> type) {
        List<Field> fields = TypeHelper.getMembers(type, t -> t.getDeclaredFields());
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            //忽略静态字段。
            int modifiers = field.getModifiers();
            if(Modifier.isStatic(modifiers)) continue;

            EntityFieldMeta fieldMeta = new EntityFieldMeta();
            fieldMeta.setField(field);

            MappingColumn columnAnnotation = AnnotationHelper.findAnnotation(field, MappingColumn.class);
            boolean isMappingColumn = (columnAnnotation != null || meta.isMapAllFieldsToColumn())
                    && AnnotationHelper.findAnnotation(field, IgnoreMapping.class) == null;
            if (isMappingColumn) {
                if (columnAnnotation != null) {
                    fieldMeta.setColumnName(columnAnnotation.name());
                    fieldMeta.setColumnLength(columnAnnotation.length());
//                    fieldMeta.setColumnType(columnAnnotation.type());
                    fieldMeta.setIsNullable(columnAnnotation.isNullable());
                    fieldMeta.setIsPrimaryKey(columnAnnotation.isKey());
                    fieldMeta.setIsAutoIncrement(columnAnnotation.isAutoIncrement());
                    fieldMeta.setDefaultValue(columnAnnotation.defaultValue());
                }

                if (StringUtils.isBlank(fieldMeta.getColumnName())) {
                    String name = field.getName();
                    if (mapCamelToUnderline) {
                        name = NameUtils.camelToUnderline(name);
                    }
                    fieldMeta.setColumnName(name);
                }
            }

            meta.getFields().add(fieldMeta);
        }
    }
}
