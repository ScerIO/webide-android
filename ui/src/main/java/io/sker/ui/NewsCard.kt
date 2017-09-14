package io.sker.ui

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.CardView
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.squareup.picasso.Picasso

/**
 * Карта новости
 */
class NewsCard(context: Context) : LinearLayout(context) {

    private var image: String? = null
    private var title: String? = null
    private var description: String? = null
    private var link: String? = null

    fun setImage(image: String): NewsCard {
        this.image = image
        return this
    }

    fun setTitle(title: String): NewsCard {
        this.title = title
        return this
    }

    fun setDescription(description: String): NewsCard {
        this.description = description
        return this
    }

    fun setLink(link: String): NewsCard {
        this.link = link
        return this
    }

    fun build() {
        this.setBackgroundColor(Color.TRANSPARENT)
        View.inflate(context, R.layout.news_card, this)
        val windowManager = this.context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val deviceWidth = displayMetrics.widthPixels
        this.layoutParams = ViewGroup.LayoutParams(deviceWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        val cardView = findViewById<CardView>(R.id.card)
        val imageView = findViewById<ImageView>(R.id.image)
        Picasso.with(context).load(R.drawable.test).into(imageView)
        val titleView = findViewById<TextView>(R.id.title)
        val descriptionView = findViewById<TextView>(R.id.description)
        val shareView = findViewById<Button>(R.id.share)
        val readMoreView = findViewById<Button>(R.id.read_more)

        titleView.text = this.title ?: ""
        descriptionView.text = this.description ?: ""

    }

}
