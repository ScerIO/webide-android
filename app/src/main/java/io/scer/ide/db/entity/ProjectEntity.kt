package io.scer.ide.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.support.design.widget.Snackbar
import android.widget.Toast
import io.scer.ide.model.ProjectModel
import io.scer.ide.util.JSONConfig
import java.io.File

@Entity(tableName = "projects")
class ProjectEntity(@PrimaryKey
                    @ColumnInfo(name = "configPath")
                    override val configPath: String) : ProjectModel {

    @Ignore
    val configFile: File = File(configPath)

    private val config: JSONConfig.JSONObjectWrapper? get() = JSONConfig.makeJSONObjectWrapper(configFile)

    override val title: String get() = config!!.get("title") as String

    override val description: String get() = config!!.get("description") as String

    override val version: String get() = config!!.get("version") as String

    companion object {
        fun makeProject(projectRootDir: File, title: String, description: String, version: String): ProjectEntity {
            if (!projectRootDir.isDirectory || !projectRootDir.canWrite())
                throw Exception("Can't write in parent directory")

            val configFile = File("$projectRootDir/phpide.json")
            if (configFile.exists()) configFile.delete()
            if (!configFile.createNewFile())
                throw Exception("Can't create config")

            JSONConfig.makeJSONObjectWrapper(configFile)
                    .put("title", title)
                    .put("version", version)
                    .put("description", description)
                    .commit()
            return ProjectEntity(configFile.absolutePath)
        }
    }
}