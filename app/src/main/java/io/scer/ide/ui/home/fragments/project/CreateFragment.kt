package io.scer.ide.ui.home.fragments.project

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
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
    private lateinit var description: EditText
    private lateinit var selectedDirPath: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_project, container, false)
        title = view.findViewById(R.id.title)
        description = view.findViewById(R.id.description)
        selectedDirPath = view.findViewById(R.id.selected_dir_path)
        val selectDir = view.findViewById<Button>(R.id.select_dir)
        selectDir.setOnClickListener{ selectDir() }
        val create = view.findViewById<Button>(R.id.create)
        create.setOnClickListener { create(view) }
        return view
    }

    private fun selectDir() {
        if (fragmentManager!!.findFragmentByTag("CREATE_PROJECT_FILE_EXPLORER") == null)
            FileExplorerDialogFragment()
                    .setMode(FileExplorer.MODE_DIR)
                    .showAddDirButton(true)
                    .showCloseButton(true)
                    .setResultListener { patch, dialog ->
                        projectPath = patch
                        selectedDirPath.text = patch.replace(Environment.getExternalStorageDirectory().path, "storage", true)
                        (dialog as FileExplorerDialogFragment).dismiss()
                    }
                    .show(fragmentManager, "CREATE_PROJECT_FILE_EXPLORER")
    }

    private fun create (view: View) {
        val title: String = title.text.toString()
        val description: String = description.text.toString()

        if (title.isBlank())
            return Snackbar.make(view, R.string.project_add_title_empty, Snackbar.LENGTH_LONG).show()

        if (projectPath == null)
            return Snackbar.make(view, R.string.project_add_dir_not_selected, Snackbar.LENGTH_LONG).show()

        try {
            val projectRootDir = File(projectPath)
            val project = ProjectEntity.makeProject(projectRootDir, title, description)
            viewModel.add(project)
        } catch (e: Exception) {
            when(e) {
                is JSONException -> {
                    Snackbar.make(view, R.string.project_add_create_config_file_error, Snackbar.LENGTH_LONG).show()
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
