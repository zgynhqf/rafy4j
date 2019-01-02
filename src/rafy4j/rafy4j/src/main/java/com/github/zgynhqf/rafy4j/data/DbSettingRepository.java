package com.github.zgynhqf.rafy4j.data;

import com.github.zgynhqf.rafy4j.env.RafyEnvironment;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 管理 DbSetting 的仓库
 * @author: huqingfang
 * @date: 2018-12-29 22:34
 **/
public class DbSettingRepository {
    /**
     * 默认使用的 MySql Driver
     */
    public static final String DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";

    private Map<String, DbSetting> store = new HashMap<>();

    /**
     * 向仓库中添加一项
     * @param dbSetting
     */
    public void add(DbSetting dbSetting) {
        Objects.requireNonNull(dbSetting);
        if (StringUtils.isBlank(dbSetting.getName())) throw new NullPointerException();

        //如果是 SimpleDbSetting，可以设置一些默认值。
        if (dbSetting instanceof SimpleDbSetting) {
            SimpleDbSetting setting = (SimpleDbSetting) dbSetting;
            if (StringUtils.isBlank(setting.getDriverName())) {
                setting.setDriverName(DRIVER_MYSQL);
            }
            if (StringUtils.isBlank(setting.getDatabase())) {
                setting.setDatabase(setting.getName());
            }
            if (setting.getDataSource() == null) {
                DataSource dataSource = RafyEnvironment.getBeanFactory().getBean(DataSource.class);
                setting.setDataSource(dataSource);
            }
        }

        store.put(dbSetting.getName(), dbSetting);
    }

    /**
     * 通过配置名查找指定的配置项。
     * @param dbSettingName
     * @return
     */
    public DbSetting findOrCreate(String dbSettingName) {
        DbSetting res = store.get(dbSettingName);
        if (res != null) return res;

        //create a default db setting.
        SimpleDbSetting setting = new SimpleDbSetting() {{
            setName(dbSettingName);
            setDatabase(dbSettingName);
        }};

        this.add(setting);

        return setting;
    }
}