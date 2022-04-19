package com.example.plugin_datasource

interface DataSource : DataReader {

    interface Factory {
        //提供实现子类的创建工厂
        fun createDataSource(): DataSource?
    }

    fun open(dataSpec: DataSpec): Long

    fun close()


}