package io.scer.ide.ui.home.fragments.project

import android.arch.lifecycle.ViewModelProviders
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import io.scer.fileexplorer.FileExplorer
import io.scer.fileexplorer.FilesExplorerView
import io.scer.ide.R
import io.scer.ide.db.entity.ProjectEntity
import io.scer.ide.util.JSONConfig
import io.scer.ide.util.px
import io.scer.ide.viewmodel.ProjectsViewModel
import io.scer.ui.TabDialog
import io.scer.ui.TabDialogFullscreen
import org.json.JSONException
import java.io.File

/**
 * Добавление проета
 */
class OpenFragment : TabDialog.TabDialogFragment() {
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(ProjectsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = RelativeLayout(context)
        val explorer = FilesExplorerView(context!!)
                .setMode(FileExplorer.MODE_FILE)
                .setVisibleExtensions(arrayOf("json"))
                .setResultListener { patch, _ ->
                    val configFile = File(patch)
                    try {
                        JSONConfig.makeJSONObjectWrapper(configFile).getString("title")
                        viewModel.add(ProjectEntity(configFile.absolutePath))
                    } catch (e: Exception) {
                        return@setResultListener when (e) {
                            is JSONException ->   Snackbar.make(view, R.string.project_config_error_read, Snackbar.LENGTH_LONG).show()
                            is SQLiteConstraintException -> Snackbar.make(view, R.string.project_config_already_exist, Snackbar.LENGTH_LONG).show()
                            else -> Snackbar.make(view, R.string.error, Snackbar.LENGTH_LONG).show()
                        }
                    }
                    dialogInstance.dismiss()
                }
                .setBackButtonPressedInRootDirectory {
                    dialogInstance.dismiss()
                }
                .init()
        view.addView(explorer)
        return view
    }

    companion object {
        fun newInstance() = OpenFragment()
    }

}
