package io.scer.ide.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.schedulers.Schedulers
import io.scer.ide.NewsQuery
import io.scer.ide.api.ApiApolloClient
import io.scer.ide.db.AppDatabase
import io.scer.ide.db.entity.NewsEntity

class NewsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app.applicationContext).newsDao()

    fun getById(id: String) = dao.getById(id)

    fun getAll(): LiveData<List<NewsEntity>> {
        val query = ApiApolloClient.newInstance().query(
            NewsQuery
                .builder()
                .offset(0)
                .limit(5)
                .build()
        )

        Rx2Apollo.from(query)
                .subscribeOn(Schedulers.io())
                .subscribe({ allNews: Response<NewsQuery.Data>? ->
                    dao.deleteAll()
                    val data = allNews!!.data()!!.news()!!.all()!!.nodes()!!.map { newsData ->
                        val news = newsData.fragments().news()
                        NewsEntity(id = news.id(),
                                image = news.image(),
                                title = news.title(),
                                description = news.description(),
                                content = news.content(),
                                link = "")
                    }
                    dao.insertAll(data)
                }, { e: Throwable? ->
                    Log.e("NVM", e!!.message)
                })
        return dao.getAll()
    }

    fun add(news: NewsEntity) = dao.insert(news)

    fun delete(news: NewsEntity) = dao.delete(news)
}
