package io.scer.ide.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import io.scer.ide.model.Role
import io.scer.ide.model.UserModel

@Entity(tableName = "User")
data class UserEntity(@PrimaryKey
                      override val id: String,
                      @ColumnInfo(name = "email")
                      override val email: String,
                      @ColumnInfo(name = "role")
                      override val role: Int,
                      @ColumnInfo(name = "token")
                      override val token: String,
                      @ColumnInfo(name = "lastName")
                      override val lastName: String,
                      @ColumnInfo(name = "firstName")
                      override val firstName: String,
                      @ColumnInfo(name = "picture")
                      override val picture: String) : UserModel