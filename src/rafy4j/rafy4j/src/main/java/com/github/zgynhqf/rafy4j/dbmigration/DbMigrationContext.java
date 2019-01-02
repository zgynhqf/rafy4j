package com.github.zgynhqf.rafy4j.dbmigration;


import com.github.zgynhqf.rafy4j.data.SimpleDbAccessor;
import com.github.zgynhqf.rafy4j.data.DbSetting;
import com.github.zgynhqf.rafy4j.data.DbAccesser;
import com.github.zgynhqf.rafy4j.dbmigration.model.*;
import com.github.zgynhqf.rafy4j.dbmigration.model.differ.DatabaseChanges;
import com.github.zgynhqf.rafy4j.dbmigration.model.differ.ModelDiffer;
import com.github.zgynhqf.rafy4j.dbmigration.operations.DropDatabase;
import com.github.zgynhqf.rafy4j.dbmigration.operations.UpdateComment;
import com.github.zgynhqf.rafy4j.dbmigration.providers.DbMigrationProvider;
import com.github.zgynhqf.rafy4j.dbmigration.providers.DbMigrationProviderFactory;
import com.github.zgynhqf.rafy4j.dbmigration.providers.SqlRunGenerator;
import com.github.zgynhqf.rafy4j.dbmigration.run.SqlMigrationRun;
import com.github.zgynhqf.rafy4j.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 支持功能：
 * 根据目标 Schema 自动升级
 * 此时可配置是否考虑数据丢失。
 * 手工更新
 * 升级历史日志功能
 * 根据历史记录回滚、再次升级
 * 客户端根据开发人员的历史记录升级自己的自己的数据库。
 * 防止数据丢失
 * 配置是否执行丢失操作。
 * 配置是否忽略数据丢失。
 * 数据库删除、备份、还原
 */
public class DbMigrationContext implements Closeable {
    //region fields
//    private ManualMigrationsContainer _ManualMigrations;
    //    private IDbBackuper _DbBackuper;
//    private DbVersionProvider _DbVersionProvider;
    private DbMigrationProvider _dbProvider;
    private DbAccesser _dba;
    private SqlRunGenerator _runGenerator;
    //endregion

    //region database settings

    /**
     * 数据库的名称
     */
    protected final String getDbName() {
        return this.getDbSetting().getDatabase();
    }

    /**
     * 对应的数据库配置。
     */
    private DbSetting dbSetting;

    public final DbSetting getDbSetting() {
        return dbSetting;
    }

    private void setDbSetting(DbSetting value) {
        dbSetting = value;
    }
    //endregion

    /**
     * Initializes a new instance of the <see cref="DbMigrationContext"/> class.
     *
     * @param dbSetting The database setting.
     */
    public DbMigrationContext(DbSetting dbSetting) {
        Objects.requireNonNull(dbSetting);
        Validate.notNull(dbSetting);

        this.setDbSetting(dbSetting);

        this.setRunDataLossOperation(DataLossOperation.None);

        this._dbProvider = DbMigrationProviderFactory.GetProvider(dbSetting);

        this._runGenerator = (SqlRunGenerator) this._dbProvider.CreateRunGenerator();
        this.setDatabaseMetaReader(this._dbProvider.CreateSchemaReader());
    }

    //region Components

//        ********************** 代码块解释 *********************************
//         * Builder 模式
//         * 以下组件属性基本全是公开的，应用层可以设置这些组件属性以实现不同程序的功能多样化。
//        *********************************************************************

//    /**
//     * 存储所有可用的手工更新
//     */
//    public final ManualMigrationsContainer getManualMigrations() {
//        if (this._ManualMigrations == null) {
//            this.setManualMigrations(new ManualMigrationsContainer());
//        }
//
//        return this._ManualMigrations;
//    }
//
//    public final void setManualMigrations(ManualMigrationsContainer value) {
//        value.TryInitalize(this.getDbSetting());
//        this._ManualMigrations = value;
//    }

    /**
     * 数据库元数据读取器。
     */
    private IMetadataReader metadataReader;

    public final IMetadataReader getDatabaseMetaReader() {
        return metadataReader;
    }

    private void setDatabaseMetaReader(IMetadataReader value) {
        metadataReader = value;
    }

//    /**
//     * 数据库备份工具
//     */
//    public final IDbBackuper getDbBackuper() {
//        if (this._DbBackuper == null) {
//            this._DbBackuper = this._dbProvider.CreateDbBackuper();
//        }
//
//        return this._DbBackuper;
//    }

//    /**
//     * 此属性如果为 null，表示不需要记录更新日志。
//     * 也就是说每次都是根据数据库当前版本号来进行完整对比升级。
//     * 默认值为 null。
//     */
//    private HistoryRepository privateHistoryRepository;
//
//    public final HistoryRepository getHistoryRepository() {
//        return privateHistoryRepository;
//    }
//
//    public final void setHistoryRepository(HistoryRepository value) {
//        privateHistoryRepository = value;
//    }
//
//    /**
//     * 数据库版本号管理提供程序
//     * 当纯粹使用手工更新时，可以只重写此属性而不重写 HistoryRepository 属性。
//     */
//    public final DbVersionProvider getDbVersionProvider() {
//        if (this._DbVersionProvider == null) {
//            EmbadedDbVersionProvider tempVar = new EmbadedDbVersionProvider();
//            tempVar.DBA = this.getDBA();
//            tempVar.setDbSetting(this.getDbSetting());
//            this._DbVersionProvider = tempVar;
//        }
//
//        return this._DbVersionProvider;
//    }
//
//    public final void setDbVersionProvider(DbVersionProvider value) {
//        this._DbVersionProvider = value;
//        if (value != null) {
//            value.setDbSetting(this.getDbSetting());
//        }
//    }

    //endregion

    //region Configuration

    /**
     * 是否在自动迁移过程中执行 删除表、删除列 的操作。
     * <p>
     * 默认为 None，表示不执行任何数据丢失的操作。
     */
    private DataLossOperation dataLossOperation = DataLossOperation.None;

    public final DataLossOperation getRunDataLossOperation() {
        return dataLossOperation;
    }

    public final void setRunDataLossOperation(DataLossOperation value) {
        dataLossOperation = value;
    }

    public final void NotifyDataLoss(String actionName) {
        //此函数中可以记录所有数据丢失操作的日志。
        //暂不实现。
    }

    //endregion

    //region AutoMigrate

    /**
     * 自动移植到目标结构
     * 注意，自动迁移时，同样执行相应时间段的手工迁移。
     *
     * @param destination 目标结构
     */
    public final void MigrateTo(DestinationDatabase destination) {
//            ********************** 代码块解释 *********************************
//             *
//             * 主要更新逻辑如下：
//             * 1 根据当前版本号，同步最新的历史记录。
//             *      客户端被动升级：当给客户版本做更新时，一般使用开发人员的历史记录替换客户版本的历史记录，此时发生此逻辑：
//             *      发现历史记录中有比当前数据库版本还要新的记录时，说明已经使用了最新版本的历史记录库，这时需要根据这些历史记录来升级数据库。
//             * 2 如果是第一次创建库，则：先执行自动升级、然后再执行手工结构升级。
//             * 2 如果是升级库，则：先执行手工结构升级、然后再执行自动升级。
//             * 3 执行手工数据升级。
//             *
//            *********************************************************************
//
//        if (this.getSupportHistory()) {
//            Must(this.MigrateToHistory(LocalDateTime.MAX));
//        }
//
//        java.util.ArrayList<ManualDbMigration> manualPendings = this.GetManualPendings();
//        var schemaPending = manualPendings.Where(m = > m.Type == ManualMigrationType.Schema).ToList();
//        var dataPending = manualPendings.Where(m = > m.Type == ManualMigrationType.data).ToList();

        Database dbMeta = this.getDatabaseMetaReader().Read();

        ModelDiffer differ = new ModelDiffer(_runGenerator.getDbTypeCoverter());
        differ.IDbIdentifierProvider = _runGenerator.getIdentifierQuoter();
        DatabaseChanges changeSet = differ.Distinguish(dbMeta, destination);

        //判断是否正处于升级阶段。（或者是处于创建阶段。）
        //不能直接用 dbMeta.Tables.Count > 0 来判断，这是因为里面可能有 IgnoreTables 中指定表。
//        boolean updating = changeSet.getChangeType() == ChangeType.Removed ||
//                changeSet.getChangeType() == ChangeType.Modified &&
//                        destination.getTables().size() > changeSet.getTablesChanged()
//                                .stream().filter(t -> t.getChangeType() == ChangeType.Added).count();
//        if (updating) {
//            Must(this.MigrateUpBatch(schemaPending));

        Must(this.AutoMigrate(changeSet));
//        } else {
//            if (manualPendings.size() > 0) {
//                //此时，自动升级的时间都应该小于所有手工升级
//                Must(this.AutoMigrate(changeSet, manualPendings[0].getTimeId()));
//            } else {
//            Must(this.AutoMigrate(changeSet));
//            }
//
//            Must(this.MigrateUpBatch(schemaPending));
//        }

//        if (dataPending.size() > 0) {
////                ********************** 代码块解释 *********************************
////                 *
////                 * 由于之前把 结构升级 和 数据升级 分离开了，
////                 * 所以有可能出现 数据升级 中的最后时间其实没有之前的 结构升级 或者 自动升级 的时间大，
////                 * 这样就会导致 数据升级 后，版本号变得更小了。
////                 * 所以这里需要判断如果这种情况发生，则忽略数据升级中的版本号。
////                 *
////                *********************************************************************
//
//            java.util.Date dbVersion = this.GetDbVersion();
//            var maxDataPending = dataPending.Max(m = > m.TimeId);
//
//            Must(this.MigrateUpBatch(dataPending));
//
//            if (dbVersion.compareTo(maxDataPending) > 0) {
//                this.getDbVersionProvider().SetDbVersion(dbVersion);
//            }
//        }
    }

    /**
     * 保证 TimeId 之间的间隔在 10ms 以上
     */
    private static final long TimeIdSpan = 10;

    private Result AutoMigrate(DatabaseChanges changeSet) {
        return this.AutoMigrate(changeSet, null);
    }

    private Result AutoMigrate(DatabaseChanges changeSet, Instant maxTime) {
        //生成所有自动迁移操作
        AutomationMigration auto = new AutomationMigration();
        auto.Context = this;
        auto.GenerateOpertions(changeSet);
        List<MigrationOperation> autoMigrations = auto.getOperations();

        if (autoMigrations.size() > 0) {
            this.GenerateTimeId(autoMigrations, maxTime);

            return this.MigrateUpBatch(autoMigrations);
        }

        return Result.ok();
    }

    private void GenerateTimeId(List<MigrationOperation> autoMigrations, Instant maxTime) {
//            ********************** 代码块解释 *********************************
//             *
//             * 如果提供了最大时间限制，则所有自动升级的时间都由该时间向前反推得出。
//             * 否则，直接由当前时间向后推出。
//             *
//             * 保证 TimeId 之间的间隔在 MinTimeIdSpan 以上。
//             *
//            *********************************************************************

        if (maxTime != null) {
            Instant timeId = maxTime;
            for (int i = autoMigrations.size() - 1; i >= 0; i--) {
                timeId = timeId.plusMillis(-TimeIdSpan);
                autoMigrations.get(i).setRuntimeTimeId(timeId);
            }
        } else {
            Instant timeId = Instant.now();
            for (MigrationOperation m : autoMigrations) {
                m.setRuntimeTimeId(timeId);
                timeId = timeId.plusMillis(TimeIdSpan);
            }
        }
    }

    //endregion

    //region MigrateManually

//    /**
//     * 使用场景：如果只是使用手工更新，可以调用此方法完成。
//     */
//    public final void MigrateManually() {
//        java.util.ArrayList<ManualDbMigration> pendings = this.GetManualPendings();
//
//        Must(this.MigrateUpBatch(pendings));
//    }
//
//    private java.util.ArrayList<ManualDbMigration> GetManualPendings() {
//        java.util.Date version = this.GetDbVersion();
//
//        var pendings = this.getManualMigrations().Where(m = > m.TimeId > version).OrderBy(m = > m.TimeId).ToList();
//
//        return pendings;
//    }

    //endregion

    //region RefreshComments

    /**
     * 使用指定的注释来更新数据库中的相关注释内容。
     * 更新注释前，请保证真实数据库中的包含了指定的库中的所有表和字段。
     */
    public final void RefreshComments(Database database) {
        List<MigrationOperation> operations = new ArrayList<MigrationOperation>(1000);

        for (Table table : database.getTables()) {
            if (!StringUtils.isBlank(table.getComment())) {
                UpdateComment updateComment = new UpdateComment();
                updateComment.setTableName(table.getName());
                updateComment.setComment(table.getComment());
                operations.add(updateComment);
            }

            for (Column column : table.getColumns()) {
                if (!StringUtils.isBlank(column.getComment())) {
                    UpdateComment updateComment = new UpdateComment();
                    updateComment.setTableName(table.getName());
                    updateComment.setColumnName(column.getName());
                    updateComment.setColumnDbType(column.getDbType());
                    updateComment.setComment(column.getComment());
                    operations.add(updateComment);
                }
            }
        }

        Must(this.MigrateUpBatch(operations));
    }

    //endregion

    //region Migrate/Rollback by history

//        ********************** 代码块解释 *********************************
//         * 直接根据时间点来迁移数据库。
//         *
//         * 根据历史记录功能来进行数据库的迁移
//        *********************************************************************
//
//    /**
//     * 直接跳转到某个时间点的数据库
//     *
//     * @param time
//     * @return
//     */
//    public final Result JumpToHistory(java.util.Date time) {
//        if (!this.getSupportHistory()) {
//            throw new InvalidOperationException("当前迁移操作不支持历史记录功能。");
//        }
//
//        java.util.Date version = this.GetDbVersion();
//        if (version.compareTo(time) < 0) {
//            return this.MigrateToHistory(time);
//        } else {
//            this.RollbackToHistory(time);
//            return true;
//        }
//    }

//    /**
//     * 只使用历史记录来升级到指定的时间点
//     *
//     * @param time
//     */
//    public final Result MigrateToHistory(LocalDateTime time) {
//        java.util.Date version = this.GetDbVersion();
//
//        java.util.List<HistoryItem> histories = this.EnsureHistoryRepository().GetHistoryItems(this.getDbName(), version, time);
//        if (histories.size() > 0) {
//            var migrations = histories.Reverse().Select(h = > this.getHistoryRepository().TryRestore(h)).
//            Where(h = > h != null).ToList();
//
//            return this.MigrateUpBatch(migrations, true);
//        }
//
//        return true;
//    }
//
//    /**
//     * 回滚到指定时间
//     *
//     * @param time           The time.
//     * @param rollbackAction The rollback action.
//     */
////ORIGINAL LINE: public void RollbackToHistory(DateTime time, RollbackAction rollbackAction = RollbackAction.None)
//    public final void RollbackToHistory(java.util.Date time, RollbackAction rollbackAction) {
//        java.util.Date version = this.GetDbVersion();
//
//        java.util.List<HistoryItem> histories = this.EnsureHistoryRepository().GetHistoryItems(this.getDbName(), time, version);
//
//        this.Rollback(histories, rollbackAction, time);
//    }

//    /**
//     * 全部回滚历史记录
//     */
////ORIGINAL LINE: public void RollbackAll(RollbackAction rollbackAction = RollbackAction.None)
//    public final void RollbackAll(RollbackAction rollbackAction) {
//        java.util.Date version = this.GetDbVersion();
//
//        java.util.List<HistoryItem> histories = this.EnsureHistoryRepository().GetHistoryItems(this.getDbName(), java.util.Date.getMinValue(), version);
//
//        this.Rollback(histories, rollbackAction);
//    }
//
////ORIGINAL LINE: private void Rollback(IList<HistoryItem> histories, RollbackAction rollbackAction, Nullable<DateTime> time = null)
//    private void Rollback(java.util.List<HistoryItem> histories, RollbackAction rollbackAction, java.util.Date time) {
//        HistoryRepository historyRepo = this.getHistoryRepository();
//        if (histories.size() > 0) {
//            for (var history : histories) {
//                dbmigration migration = historyRepo.TryRestore(history);
//                if (migration != null) {
//                    //CreateDatabase 不能回滚，需要使用 DeleteDatabase 方法。
//                    if (!(migration instanceof CreateDatabase)) {
//                        this.MigrateDown(migration);
//
//                        var version = migration.getTimeId().AddMilliseconds(-TimeIdSpan / 2);
//                        Must(this.getDbVersionProvider().SetDbVersion(version));
//
//                        if (rollbackAction == RollbackAction.DeleteHistory) {
//                            historyRepo.Remove(this.getDbName(), history);
//                        }
//                    }
//                }
//            }
//        }
//
//        if (time != null) {
//            this.getDbVersionProvider().SetDbVersion(time.getValue());
//        }
//    }

    //region 方便的 API

//    /**
//     * 直接跳转到某个时间点的数据库
//     *
//     * @param time
//     * @return
//     */
//    public final Result JumpToHistory(String time) {
//        return this.JumpToHistory(new java.util.Date(java.util.Date.parse(time)));
//    }
//
//    /**
//     * 回滚到指定时间
//     *
//     * @param time           The time.
//     * @param rollbackAction The rollback action.
//     */
////ORIGINAL LINE: public void RollbackToHistory(string time, RollbackAction rollbackAction = RollbackAction.None)
//    public final void RollbackToHistory(String time, RollbackAction rollbackAction) {
//        this.RollbackToHistory(new java.util.Date(java.util.Date.parse(time)), rollbackAction);
//    }

    //endregion

    //endregion

    //region DbVersion & History
//
//    /**
//     * 获取数据库当前的版本号
//     *
//     * @return
//     */
//    public final java.util.Date GetDbVersion() {
//        return this.getDbVersionProvider().GetDbVersion();
//    }
//
//    /**
//     * 把当前数据库的版本号设置为初始状态。
//     * <p>
//     * 调用此方法后，会导致：所有手动迁移再次运行。
//     */
//    public final void ResetDbVersion() {
//        this.getDbVersionProvider().SetDbVersion(getDbVersionProvider().DefaultMinTime);
//    }
//
//    /**
//     * 当前是否支持历史操作
//     */
//    public final boolean getSupportHistory() {
//        return this.getHistoryRepository() != null;
//    }
//
//    /**
//     * 获取当前所有的历史项
//     *
//     * @return
//     */
//    public final java.util.List<dbmigration> GetHistories() {
//        return this.EnsureHistoryRepository().GetHistories(this.getDbName());
//    }
//
//    /**
//     * 判断当前库的版本号是否处于最开始的状态。
//     * 暂时把这个判断封装在方法内，以应对未来可能的 DefaultMinTime 变化
//     *
//     * @return
//     */
//    public final boolean HasNoHistory() {
//        return this.EnsureHistoryRepository().HasNoHistory(this.getDbName());
//    }
//
//    /**
//     * 删除所有历史记录
//     */
//    public final void ResetHistory() {
//        HistoryRepository repo = this.EnsureHistoryRepository();
//
//        java.util.List<HistoryItem> items = repo.GetHistoryItems(this.getDbName());
//
//        for (var item : items) {
//            repo.Remove(this.getDbName(), item);
//        }
//    }
//
//    private HistoryRepository EnsureHistoryRepository() {
//        if (!this.getSupportHistory()) {
//            throw new InvalidOperationException("当前环境不支持历史记录功能。（请正确重写本类的 HistoryRepository 属性。）");
//        }
//
//        return this.getHistoryRepository();
//    }

    //endregion

    //region Database

    /**
     * 是否存在
     *
     * @return
     */
    public final boolean DatabaseExists() {
        DbAccesser dba = this.getDBA();
        try {
            Connection connection = dba.startConnection();

//            DataSource dataSource = this.getDBA().getDataSource();
//        try (Connection connection = dataSource.getConnection()) {
//
//            if (!connection.isClosed()) {
//                return true;
//            }

            return true;
        } catch (Exception e) {
        } finally {
            dba.endConnection();
        }

        return false;
    }

    /**
     * 删除数据库
     * 以及它的历史信息、版本号信息。
     * <p>
     * 注意，如果需要保留整个历史库的升级信息，请使用 MigrateTo(RemovedDatabase) 方法。
     */
    public final void DeleteDatabase() {
        DropDatabase dropDatabase = new DropDatabase();
        dropDatabase.setDatabase(this.getDbName());
        this.MigrateUp(dropDatabase);

        //        boolean embaded = this.getDbVersionProvider() instanceof EmbadedDbVersionProvider;
//
//        if (!embaded) {
//            this.ResetDbVersion();
//        }
//
//        this.ResetHistory();
    }

    //endregion

    //region MigrateCore

    private DbAccesser getDBA() {
        if (this._dba == null) {
            this._dba = new SimpleDbAccessor(this.getDbSetting());
        }

        return this._dba;
    }

    //ORIGINAL LINE: private Result MigrateUpBatch(IEnumerable<dbmigration> migrations, bool addByDeveloperHistory = false)
    private Result MigrateUpBatch(List<MigrationOperation> migrations) {//, boolean addByDeveloperHistory) {
        Result res = Result.ok();

        //对于每一个数据库操作，进行升级操作
//        int i = 0, count = migrations.size();
        for (MigrationOperation migration : migrations) {
            this.MigrateUp(migration);

//            //如果不是客户端被动升级，需要创建历史记录
//            if (!addByDeveloperHistory && this.getSupportHistory()) {
//                res = this.getHistoryRepository().AddAsExecuted(this.getDbName(), migration);
//            }

//            //如果如果前面执行成功，那么刷新版本号
//            if (res.isSuccess()) {
//                //以下三种情况需要记录版本号：手工更新、自动更新且同时支持历史记录、客户端被动升级
//                if (migration.getMigrationType() == MigrationType.ManualMigration
//                        || this.getSupportHistory() || addByDeveloperHistory
//                ) {
//                    //EmbadedDbVersionProvider 在删除数据库时就不能再存储版本号信息了。
//                    if (!(migration instanceof DropDatabase) || !this.getDbVersionProvider().IsEmbaded()) {
//                        res = this.getDbVersionProvider().SetDbVersion(migration.TimeId);
//                    }
//                }
//            }

//            if (!res.isSuccess()) {
//                //升级日志添加出错，回滚，并终止之后的升级列表。
//                this.MigrateDown(migration);
//                break;
//            }
//
//            i++;
//            this.OnItemMigrated(new MigratedEventArgs(i, count));
        }

        return res;
    }

    private void MigrateUp(DbMigration migration) {
        this.RunMigration(migration, true);
    }

    private void MigrateDown(DbMigration migration) {
        this.RunMigration(migration, false);
    }

    private void RunMigration(DbMigration migration, boolean up) {
        List<MigrationRun> runList = this.GenerateRunList(migration, up);

        if (runList.isEmpty()) return;

        this.ExecuteWithoutDebug(() -> {
            DbAccesser dba = this.getDBA();
            if (runList.size() > 1) {
                /*********************** 代码块解释 *********************************
                 *
                 * 如果执行多句 SQL，则需要主动打开连接，
                 * 否则 DBA 可能会打开之后再把连接关闭，造成多次打开连接，引起分布式事务。
                 * 而分布式事务在一些数据库中并不支持，例如 SQLCE。
                 *
                 **********************************************************************/
                try {
                    dba.startTransaction();
                    for (MigrationRun item : runList) {
                        this._currentRun = (SqlMigrationRun) ((item instanceof SqlMigrationRun) ? item : null);
                        item.Run(dba);
                    }
                    dba.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    dba.endConnection();
                }
            } else {
                MigrationRun singleRun = runList.get(0);
                this._currentRun = (SqlMigrationRun) (singleRun instanceof SqlMigrationRun ? singleRun : null);
                singleRun.Run(dba);
            }
            return null;
        });
    }

    private SqlMigrationRun _currentRun;

    private void ExecuteWithoutDebug(Supplier<Void> action) {
        try {
            action.get();
        } catch (Exception ex) {
            String error = ex.getMessage();
            if (ex instanceof SQLException) {
                error = DbMigrationExceptionMessageFormatter.FormatMessage((SQLException) ex, _currentRun);
            }
            throw new DbMigrationException(error, ex);
        }
    }

    private List<MigrationRun> GenerateRunList(DbMigration migration, boolean up) {
        migration.setDatabaseMetaReader(this.getDatabaseMetaReader());

        if (up) {
            migration.GenerateUpOperations();
        } else {
            migration.GenerateDownOperations();
        }

        List<MigrationRun> runList = _runGenerator.Generate(migration.getOperations());

        return runList;
    }

    /**
     * 释放资源。
     */
    public void close() throws IOException {
        if (this._dba != null) {
            this._dba.close();
        }
    }

//    /**
//     * 每一个项成功升级后的通知事件。
//     */
//	public event EventHandler<MigratedEventArgs> ItemMigrated;
//    private void OnItemMigrated(MigratedEventArgs e) {
//        EventHandler<MigratedEventArgs> handler = this.ItemMigrated;
//        if (handler != null) {
//            handler(this, e);
//        }
//    }

    //endregion

    //region Helper

    private static Result Must(Result res) {
        if (!res.isSuccess()) {
            throw new DbMigrationException(res.getMessage());
        }
        return res;
    }

    //endregion
}