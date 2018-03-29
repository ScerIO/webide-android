package io.scer.ide.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.scer.ide.db.AppDatabase
import io.scer.ide.db.entity.ProjectEntity

class ProjectsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app.applicationContext).projectDao()

    val getAll = dao.getAll()

    fun add(news: ProjectEntity) = dao.insert(news)

    fun delete(news: ProjectEntity) = dao.delete(news)
}