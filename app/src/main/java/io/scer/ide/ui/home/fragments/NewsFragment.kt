package io.scer.ide.ui.home.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import io.scer.ide.R
import io.scer.ide.db.entity.NewsEntity
import io.scer.ide.ui.news.NewsActivity
import io.scer.ide.viewmodel.NewsViewModel
import io.scer.ui.NewsCard

class NewsFragment : Fragment() {
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(NewsViewModel::class.java)
    }

    private lateinit var newsView: LinearLayout
    private lateinit var refreshView: SwipeRefreshLayout

    private lateinit var animation: Animation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        newsView = view.findViewById(R.id.layout)
        refreshView = view.findViewById(R.id.refresh)

        animation = AnimationUtils.loadAnimation(context, R.anim.fade_and_translate)

        this.viewModel.getAll()
                .observe(this, newsObserver)
        refreshView.setOnRefreshListener {
            this.viewModel.getAll()
        }

        return view
    }

    private val newsObserver = Observer<List<NewsEntity>> { list ->
        if (newsView.childCount > 0) newsView.removeAllViews()
        if (list === null) return@Observer
        list.forEach { news ->
            val newsToAppend = NewsCard(context!!)
                    .setTitle(news.title)
                    .setDescription(news.description)
                    .setImage(news.image)
                    .setLink(news.link)
                    .setOnClickReadMore(View.OnClickListener  {
                        val intent = Intent(context, NewsActivity::class.java)
                        intent.putExtra("NEWS_ID", news.id)
                        startActivity(intent)
                    })
                    .setOnClickShare(View.OnClickListener {
                        Snackbar.make(view!!, R.string.not_implemented, Snackbar.LENGTH_SHORT).show()
                    })
                    .build()
            newsView.addView(newsToAppend)
        }
        refreshView.isRefreshing = false
        newsView.startAnimation(animation)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.viewModel.getAll().removeObserver(newsObserver)
    }

    companion object {
        fun newInstance() = NewsFragment()
    }

}
