package io.scer.ide.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import io.scer.ide.db.dao.NewsDao
import io.scer.ide.db.dao.ProjectDao
import io.scer.ide.db.dao.UserDao
import io.scer.ide.db.entity.NewsEntity
import io.scer.ide.db.entity.ProjectEntity
import io.scer.ide.db.entity.UserEntity

@Database(entities = [
    (NewsEntity::class),
    (ProjectEntity::class),
    (UserEntity::class)
], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun projectDao(): ProjectDao
    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "app.db")
                        .allowMainThreadQueries()
                        .build()
    }
}