package io.scer.ide.ui.news

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.widget.ImageView
import com.squareup.picasso.Picasso
import io.scer.ide.R
import io.scer.ide.db.entity.NewsEntity
import io.scer.ide.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(NewsViewModel::class.java)
    }
    private lateinit var imageView: ImageView
    private lateinit var contentView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        setSupportActionBar(toolbar)

        val newsID = intent.getStringExtra("NEWS_ID") ?: "null"
        viewModel.getById(newsID).observe(this, newsObserver)

        imageView = findViewById(R.id.image)
        contentView = findViewById(R.id.content)
    }

    private val newsObserver = Observer<NewsEntity> { news ->
        if (news == null) return@Observer
        title = news.title

        Picasso
                .with(this)
                .load(news.image)
                .fit()
                .centerCrop()
                .into(imageView)

        contentView.loadData(news.content, "text/html; charset=UTF-8", null)
    }

}