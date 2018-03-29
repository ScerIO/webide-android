package io.scer.ide.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import io.scer.ide.db.entity.ProjectEntity

@Dao
interface ProjectDao: BaseDao<ProjectEntity> {
    /**
     *  Get all projects
     */
    @Query("SELECT * FROM Projects")
    fun getAll(): LiveData<List<ProjectEntity>>
    /**
     * Get a project by path.
     * *
     * @param configPath - Project path
     * @return the project from the table with a specific id.
     */
    @Query("SELECT * FROM Projects WHERE configPath = :configPath")
    fun getByConfigPath(configPath: String): LiveData<ProjectEntity>

    /**
     * Delete all projects.
     */
    @Query("DELETE FROM Projects")
    fun deleteAll()
}