package com.shimao.mybuglylib.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * @author : jian
 * @date   : 2020/7/21 18:06
 * @version: 1.0
 */
@Database(entities = [CrashVO::class],version = 1)
abstract class CrashDatabase:RoomDatabase() {
    abstract fun crashDao(): CrashDao

    companion object {
        private var instance: CrashDatabase? = null

        fun init(context: Context) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                CrashDatabase::class.java,
                "crashdatabase"
            )
                .allowMainThreadQueries()
                .build()
        }

        fun get(): CrashDatabase {
            return instance!!
        }

    }
}