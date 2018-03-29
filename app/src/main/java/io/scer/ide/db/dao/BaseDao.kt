package io.scer.ide.db.dao

import android.arch.persistence.room.Delete
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy

interface BaseDao<in T> {
    /**
     * Insert a entity in the database.
     * *
     * @param entity - The entity to be inserted.
     */
    @Insert( onConflict = OnConflictStrategy.FAIL )
    fun insert(vararg entity: T)

    /**
     * Delete
     */
    @Delete
    fun delete(vararg entity: T)
}