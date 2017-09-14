package io.sker.phpide.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.sker.phpide.R
import io.sker.ui.NewsCard


class News : TagFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        val news = view.findViewById<LinearLayout>(R.id.layout)
        val news1 = NewsCard(context)
        news1
                .setTitle("Test news title 1")
                .setDescription("Test news description 1")
                .build()
        news.addView(news1)

        val news2 = NewsCard(context)
        news2
                .setTitle("Test title 2")
                .setDescription("Test description Test description Test description Test description " +
                        "Test description Test description Test description Test description" +
                        "Test description Test description Test description Test description" +
                        "Test description Test description Test description Test description" +
                        "Test description Test description Test description Test description" +
                        "Test description Test description Test description Test description")
                .build()
        news.addView(news2)

        return view
    }

    companion object {
        const val TAG: String = "FRAGMENT_NEWS"
    }

}
