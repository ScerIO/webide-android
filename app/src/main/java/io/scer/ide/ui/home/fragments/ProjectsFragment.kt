package io.scer.ide.ui.home.fragments

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import io.scer.ide.R
import io.scer.ide.db.entity.ProjectEntity
import io.scer.ide.ui.editor.EditorActivity
import io.scer.ide.ui.home.fragments.project.CreateFragment
import io.scer.ide.ui.home.fragments.project.OpenFragment
import io.scer.ide.ui.home.views.ProjectCardView
import io.scer.ide.util.checkPermissions
import io.scer.ide.viewmodel.ProjectsViewModel
import io.scer.ui.TabDialog
import io.scer.ui.TabDialogFullscreen

const val REQUEST_PERMISSIONS_CREATE_PROJECT = 0
const val DIALOG_PROJECT = "DIALOG_PROJECT"
/**
 *
 */
class ProjectsFragment : Fragment() {
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(ProjectsViewModel::class.java)
    }

    /**
     * Диалог добавления проекта и список вкадок
     */
    private val projectDialog = TabDialogFullscreen()
    private val tabs = ArrayList<TabDialog.Tab>()

    private lateinit var projectsView: LinearLayout

    private lateinit var animation: Animation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_projects, container, false)
        projectsView = view.findViewById(R.id.layout)

        animation = AnimationUtils.loadAnimation(context, R.anim.fade_and_translate)

        if (tabs.count() < 2 ) {
            val create = CreateFragment.newInstance()
            val open = OpenFragment.newInstance()
            tabs.add(TabDialog.Tab(R.drawable.ic_create_new_folder_black, create))
            tabs.add(TabDialog.Tab(R.drawable.ic_folder_open_black, open))
            projectDialog.setTabs(tabs)
        }

        val fab = view!!.findViewById<FloatingActionButton>(R.id.add_project)
        fab.setOnClickListener {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val checkPermissionResponses = checkPermissions(context!!, permissions)
            if (checkPermissionResponses[0] or checkPermissionResponses[1] != PackageManager.PERMISSION_GRANTED)
                requestPermissions(permissions, REQUEST_PERMISSIONS_CREATE_PROJECT)
            else
                projectDialog.show(fragmentManager, DIALOG_PROJECT)
        }

        viewModel.getAll.observe(this, projectsObserver)

        return view
    }

    companion object {
        fun newInstance() = ProjectsFragment()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CREATE_PROJECT -> {
                if (grantResults.count() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    projectDialog.show(fragmentManager, DIALOG_PROJECT)
                else
                    Snackbar.make(view!!, R.string.permission_denied, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private val projectsObserver = Observer<List<ProjectEntity>> { list ->
        if (projectsView.childCount > 0) projectsView.removeAllViews()
        if (list === null) return@Observer

        list.forEach({ project ->
            try {
                val projectView = ProjectCardView(context!!)
                projectView
                        .setTitle(project.title)
                        .setDescription(project.description)
                        .onRemoveClickListener(View.OnClickListener {
                            Snackbar.make(view!!, R.string.project_removed, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.undo, {})
                                    .addCallback(object:Snackbar.Callback() {
                                        override fun onDismissed(snackbar: Snackbar, event: Int) {
                                            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT)
                                                viewModel.delete(project)
                                        }
                                    })
                                    .show()
                        })
                        .onOpenClickListener(View.OnClickListener {
                            val intent = Intent(context, EditorActivity::class.java)
                            intent.putExtra("CONFIG_PATH", project.configFile.parent)
                            startActivity(intent)
                        })
                        .build()
                projectsView.addView(projectView)
            } catch (e: Exception) {
                viewModel.delete(project)
            }
        })
        projectsView.startAnimation(animation)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.viewModel.getAll.removeObserver(projectsObserver)
    }

}
