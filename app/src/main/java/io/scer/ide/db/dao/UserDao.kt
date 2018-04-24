package io.scer.ide.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.scer.ide.db.entity.UserEntity

@Dao
interface UserDao: BaseDao<UserEntity> {
    /**
     *  Get user
     */
    @Query("SELECT * FROM User")
    fun get(): LiveData<UserEntity>
}