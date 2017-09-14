package io.sker.phpide.home.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.sker.phpide.Editor
import io.sker.phpide.R
import io.sker.phpide.home.fragments.project.Create
import io.sker.phpide.home.fragments.project.Open
import io.sker.phpide.project.PrimitiveEvents
import io.sker.phpide.ui.ProjectCard
import io.sker.phpide.util.checkPermissions
import io.sker.ui.TabDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import io.sker.phpide.project.controllers.Project as ProjectController

/**
 *
 */
class Projects : TagFragment() {

    /**
     * Диалог добавления проекта и список вкадок
     */
    private val projectDialog = TabDialog()
    private val tabs = ArrayList<TabDialog.Tab>()
    private val DIALOG_PROJECT = "DIALOG_PROJECT"

    /**
     * Тэги фрагментов
     */
    private val FRAGMENT_TAG_CREATE = "FRAGMENT_TAG_CREATE"
    private val FRAGMENT_TAG_ADD = "FRAGMENT_TAG_ADD"

    private val REQUEST_PERMISSIONS_CREATE_PROJECT = 0

    /**
     * Контролер проектов
     */
    private lateinit var projectController: ProjectController

    private lateinit var projects: LinearLayout

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_projects, container, false)
        projects = view.findViewById(R.id.layout)
        projectController = ProjectController(context)

        if (tabs.count() < 2 ) {
            val create = childFragmentManager.findFragmentByTag(FRAGMENT_TAG_CREATE) as TabDialog.TabDialogFragment? ?: Create()
            val open = childFragmentManager.findFragmentByTag(FRAGMENT_TAG_ADD) as TabDialog.TabDialogFragment? ?: Open()
            tabs.add(TabDialog.Tab(R.drawable.ic_create_new_folder_black, create))
            tabs.add(TabDialog.Tab(R.drawable.ic_folder_open_black, open))
            projectDialog.setTabs(tabs)
        }

        val fab = view!!.findViewById<FloatingActionButton>(R.id.add_project)
        fab.setOnClickListener {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val checkPermissionResponses = checkPermissions(context, permissions)
            if (checkPermissionResponses[0] or checkPermissionResponses[1] != PackageManager.PERMISSION_GRANTED)
                requestPermissions(permissions, REQUEST_PERMISSIONS_CREATE_PROJECT)
            else
                projectDialog.show(fragmentManager, DIALOG_PROJECT)
        }

        updateProjects()

        return view
    }

    companion object {
        const val TAG: String = "FRAGMENT_PROJECTS"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CREATE_PROJECT -> {
                if (grantResults.count() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    projectDialog.show(fragmentManager, DIALOG_PROJECT)
                else
                    Snackbar.make(view!!, "Permissions denied", Snackbar.LENGTH_LONG).show()// TODO Текстовый ресурс
            }
        }
    }

    private fun updateProjects () {
        for (project in projectController.allProjects) {
            val projectView = ProjectCard(context)
            projectView
                    .setTitle(project.config!!.get("title") as String)
                    .setVersion(project.config!!.get("version") as String)
                    .setDescription(project.config!!.get("description") as String)
                    .setProjectInfo(project)
                    .onRemoveClickListener(View.OnClickListener {
                        projects.removeView(projectView)
                        projectController.removeProjectFromList(path = project.configFile.path)
                    })
                    .onOpenClickListener(View.OnClickListener {
                        val intent = Intent(context, Editor::class.java)
                        intent.putExtra("CONFIG_PATH", project.configFile.path)
                        startActivity(intent)
                    })
                    .build()
            projects.addView(projectView)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PrimitiveEvents.UpdateProjectList) = updateProjects()

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

}
