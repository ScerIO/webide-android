package io.sker.phpide.project.controllers

import android.content.Context
import io.sker.phpide.project.PrimitiveEvents
import io.sker.phpide.util.JSONConfig
import io.sker.phpide.project.models.Project as ProjectModel
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * Список проектов
 */
class Project(context: Context) {

    private val configFile: File = File("${context.applicationInfo.dataDir}/projects.json")

    fun addProjectToList(path: String) {
        JSONConfig.makeJSONArrayWrapper(configFile).put(path).commit()
        EventBus.getDefault().post(PrimitiveEvents.UpdateProjectList())
    }

    fun removeProjectFromList(path: String) {
        val config = JSONConfig.makeJSONArrayWrapper(configFile)
        val index = config.indexOf(path)
        if (index != -1)
            config.remove(index).commit()
    }

    val allProjects: ArrayList<ProjectModel> get() {
        val config = JSONConfig.makeJSONArrayWrapper(configFile)
        val projects: ArrayList<ProjectModel> = ArrayList()
        val length = config.length()
        (0 until length).mapTo(projects) { ProjectModel(config.get(it) as String) }
        return projects
    }

}