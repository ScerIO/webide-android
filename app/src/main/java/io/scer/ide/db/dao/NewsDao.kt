package io.scer.ide.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.scer.ide.db.entity.NewsEntity

@Dao
interface NewsDao: BaseDao<NewsEntity> {
    /**
     *  Get all news
     */
    @Query("SELECT * FROM News")
    fun getAll(): LiveData<List<NewsEntity>>

    /**
     * Get a news by id.
     * *
     * @param id - News id
     * @return the news from the table with a specific id.
     */
    @Query("SELECT * FROM News WHERE id = :id")
    fun getById(id: String): LiveData<NewsEntity>

    /**
     * Delete all news.
     */
    @Query("DELETE FROM News")
    fun deleteAll()

    /**
     * Insert all a entity in the database.
     * *
     * @param entity - The entity to be inserted.
     */
    @Ignore
    @Insert( onConflict = OnConflictStrategy.FAIL )
    fun insertAll(entity: List<NewsEntity>)
}