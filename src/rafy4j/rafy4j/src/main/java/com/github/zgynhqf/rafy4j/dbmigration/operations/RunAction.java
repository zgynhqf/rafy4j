package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.data.DbAccesser;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;

import java.util.function.Consumer;

//******************************************************
// * 
// * 作者：胡庆访
// * 创建时间：20110109
// * 说明：此文件只包含一个类，具体内容见类型注释。
// * 运行环境：.NET 4.0
// * 版本号：1.0.0
// * 
// * 历史记录：
// * 创建文件 胡庆访 20110109
// * 
//******************************************************


/**
 * 操作执行某个具体的代码段
 */
public class RunAction extends MigrationOperation {
    private Consumer<DbAccesser> privateAction;

    public final Consumer<DbAccesser> getAction() {
        return privateAction;
    }

    public final void setAction(Consumer<DbAccesser> value) {
        privateAction = value;
    }

    @Override
    protected void Down() {
    }
}