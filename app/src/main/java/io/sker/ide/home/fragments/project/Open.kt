package io.sker.ide.home.fragments.project

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import io.sker.fileexplorer.FileExplorer
import io.sker.fileexplorer.FilesExplorerView
import io.sker.ide.util.JSONConfig
import io.sker.ui.TabDialog
import java.io.File
import io.sker.ide.project.controllers.Project as ProjectController

/**
 * Добавление проета
 */
class Open : TabDialog.TabDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = RelativeLayout(context)
        val projectController = ProjectController(context!!)
        val explorer = FilesExplorerView(context!!)
                .setMode(FileExplorer.MODE_FILE)
                .setVisibleExtensions(arrayOf("phpide"))
                .setResultListener { patch, _ ->
                    val configFile = File(patch)
                    try {
                        JSONConfig.makeJSONObjectWrapper(configFile).getString("title")
                    } catch (e: Throwable) {
                        return@setResultListener Snackbar.make(view, "Error read config", Snackbar.LENGTH_LONG).show()
                    }
                    projectController .allProjects
                            .filter { it.configFile.path == configFile.path }
                            .forEach { return@setResultListener Snackbar.make(view, "Project already exist", Snackbar.LENGTH_LONG).show() }
                    projectController.addProjectToList(configFile.absolutePath)
                    dialogInstance.dismiss()
                }
                .setBackButtonPressedInRootDirectory {
                    dialogInstance.dismiss()
                }
                .init()
        view.addView(explorer)
        return view
    }

}
