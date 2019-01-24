package com.github.zgynhqf.rafy4j.metadata;

import com.github.zgynhqf.rafy4j.annotation.RafyApplicationAnnotationHolder;
import com.github.zgynhqf.rafy4j.utils.TypesSearcher;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体元数据仓库。
 *
 * @author: huqingfang
 * @date: 2019-01-21 13:48
 **/
public class EntityMetaStore extends ConcurrentHashMap<Class<?>, EntityMeta> implements InitializingBean {
    private EntityMetaParser metaParser;

    public EntityMetaStore() {
        super(20);
        metaParser = new EntityMetaParser();
    }

    /**
     * 初始化
     * 将 {@link com.github.zgynhqf.rafy4j.annotation.RafyApplication} 中标记的所有实体包中的所有实体，
     * 都加载其相应的元数据。
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Class<?>[] entityPackageClasses = RafyApplicationAnnotationHolder.getEntityPackageClasses();

        String[] packages = new String[entityPackageClasses.length];
        for (int i = 0; i < entityPackageClasses.length; i++) {
            Class<?> deleteEntity = entityPackageClasses[i];
            packages[i] = deleteEntity.getPackage().getName();
        }

        this.addEntityPackages(packages);
    }

    /**
     * 将指定的包中的所有类型，都扫描并创建实体元数据。
     *
     * @param entityPackages
     */
    public void addEntityPackages(Package... entityPackages) {
        String[] packages = new String[entityPackages.length];
        for (int i = 0; i < entityPackages.length; i++) {
            Package deleteEntity = entityPackages[i];
            packages[i] = deleteEntity.getName();
        }
        this.addEntityPackages(packages);
    }

    /**
     * 将指定包中的所有类型，都扫描并创建实体元数据。
     *
     * @param entityPackages
     */
    public void addEntityPackages(String... entityPackages) {
        //程序集列表，生成数据库会反射找到程序集内的实体类型进行数据库映射
        Set<Class<?>> classes = TypesSearcher.getClasses(entityPackages);
        for (Class<?> type : classes) {
            if (isEntityType(type)) {
                getEntityMeta(type);
            }
        }
    }

    public EntityMeta getEntityMeta(Class<?> type) {
        EntityMeta entityMeta = this.get(type);

        //也支持临时加载。
        if (entityMeta == null) {
            synchronized (this) {
                entityMeta = this.get(type);
                if (entityMeta == null && isEntityType(type)) {
                    entityMeta = metaParser.parse(type);

                    this.put(type, entityMeta);
                }
            }
        }

        return entityMeta;
    }

    protected boolean isEntityType(Class<?> type) {
        int modifiers = type.getModifiers();
        return !Modifier.isAbstract(modifiers) && !type.isAnnotation() && !type.isEnum()
                && !type.isInterface() && !type.isAnonymousClass();
    }
}