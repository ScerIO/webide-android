package io.sker.phpide.home.fragments.project

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import io.sker.fileexplorer.FileExplorer
import io.sker.fileexplorer.FileExplorerDialogFragment
import io.sker.phpide.R
import io.sker.phpide.util.JSONConfig
import io.sker.phpide.project.controllers.Project as  ProjectController
import io.sker.ui.TabDialog
import org.json.JSONException
import java.io.File

/**
 * Создание нового проекта
 */
class Create : TabDialog.TabDialogFragment() {

    private var projectPath: String? = null

    private lateinit var title: EditText
    private lateinit var version: EditText
    private lateinit var description: EditText
    private lateinit var selectedDirPath: AppCompatTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_project, container, false)
        title = view.findViewById(R.id.title)
        version = view.findViewById(R.id.version)
        description = view.findViewById(R.id.description)
        selectedDirPath = view.findViewById(R.id.selected_dir_path)
        val selectDir = view.findViewById<Button>(R.id.select_dir)
        selectDir.setOnClickListener{ selectDir() }
        val create = view.findViewById<Button>(R.id.create)
        create.setOnClickListener { buttonView -> create(buttonView) }
        return view
    }

    private fun selectDir() {
        if (fragmentManager.findFragmentByTag("CREATE_PROJECT_FILE_EXPLORER") == null)
            FileExplorerDialogFragment()
                    .setMode(FileExplorer.MODE_DIR)
                    .setResultListener { patch, dialog ->
                        projectPath = patch
                        selectedDirPath.text = patch
                        (dialog as FileExplorerDialogFragment).dismiss()
                    }
                    .show(fragmentManager, "CREATE_PROJECT_FILE_EXPLORER")
    }

    private fun create (view: View) {
        val title: String = title.text.toString()
        val version: String = version.text.toString()
        val description: String = description.text.toString()
        if (title.isEmpty()) {
            //TODO Добавить строковый ресурс
            Snackbar.make(view, "Title empty", Snackbar.LENGTH_LONG).show()
            return
        }
        if (version.isEmpty()) {
            //TODO Добавить строковый ресурс
            Snackbar.make(view, "Version empty", Snackbar.LENGTH_LONG).show()
            return
        }
        if (projectPath == null) {
            //TODO Добавить строковый ресурс
            Snackbar.make(view, "Dir not select", Snackbar.LENGTH_LONG).show()
            return
        }
        try {
            val projectRootDir = File(projectPath)
            if (!projectRootDir.isDirectory || !projectRootDir.canWrite()) {
                //TODO Добавить строковый ресурс
                Snackbar.make(view, "Write error", Snackbar.LENGTH_LONG).show()
                return
            }
            val configFile = File("$projectRootDir/${title.toLowerCase().replace(" ", "_")}.phpide")
            if (configFile.exists()) configFile.delete()
            if (!configFile.createNewFile()) {
                Toast.makeText(context, configFile.path, Toast.LENGTH_LONG).show()
                //TODO Добавить строковый ресурс
                Snackbar.make(view, "Config write error", Snackbar.LENGTH_LONG).show()
                return
            }
            val config = JSONConfig.makeJSONObjectWrapper(configFile)
            config
                    .put("title", title)
                    .put("version", version)
                    .put("description", description)
                    .commit()
            ProjectController(context).addProjectToList(configFile.absolutePath)
        } catch (e: Exception) {
            when(e) {
                is JSONException -> {
                    //TODO Добавить строковый ресурс
                    Snackbar.make(view, "Error make config", Snackbar.LENGTH_LONG).show()
                    return
                }
                else -> e.printStackTrace()
            }
        } finally {
            dialogInstance.dismiss()
        }
    }

}
