package io.scer.ide.ui.home.fragments.project

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import io.scer.fileexplorer.FileExplorer
import io.scer.fileexplorer.FileExplorerDialogFragment
import io.scer.ide.R
import io.scer.ide.db.entity.ProjectEntity
import io.scer.ide.viewmodel.ProjectsViewModel
import io.scer.ui.TabDialog
import org.json.JSONException
import java.io.File

/**
 * Создание нового проекта
 */
class CreateFragment : TabDialog.TabDialogFragment() {
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(ProjectsViewModel::class.java)
    }

    private var projectPath: String? = null

    private lateinit var title: EditText
    private lateinit var version: EditText
    private lateinit var description: EditText
    private lateinit var selectedDirPath: TextView

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
        if (fragmentManager!!.findFragmentByTag("CREATE_PROJECT_FILE_EXPLORER") == null)
            FileExplorerDialogFragment()
                    .setMode(FileExplorer.MODE_DIR)
                    .showAddDirButton(true)
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

        //TODO Добавить строковый ресурс
        if (title.isEmpty())
            return Snackbar.make(view, "Title empty", Snackbar.LENGTH_LONG).show()
        //TODO Добавить строковый ресурс
        if (version.isEmpty())
            return Snackbar.make(view, "Version empty", Snackbar.LENGTH_LONG).show()
        //TODO Добавить строковый ресурс
        if (projectPath == null)
            return Snackbar.make(view, "Dir not select", Snackbar.LENGTH_LONG).show()

        try {
            val projectRootDir = File(projectPath)
            val project = ProjectEntity.makeProject(projectRootDir, title, description, version)
            viewModel.add(project)
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

    companion object {
        fun newInstance() = CreateFragment()
    }

}
