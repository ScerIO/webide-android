package io.scer.ide.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.ColumnInfo
import io.scer.ide.model.NewsModel as INews

@Entity(tableName = "news")
data class NewsEntity(@PrimaryKey
                      val id: String,
                      @ColumnInfo(name = "image")
                      override val image: String,
                      @ColumnInfo(name = "title")
                      override val title: String,
                      @ColumnInfo(name = "description")
                      override val description: String,
                      @ColumnInfo(name = "content")
                      override val content: String,
                      @ColumnInfo(name = "link")
                      override val link: String) : INews