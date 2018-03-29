package io.scer.ui

import android.content.Context
import android.graphics.Color
import android.support.design.widget.Snackbar
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
    private var onClickReadMore: (view: View) -> Unit? = {}

    private lateinit var imageView: ImageView

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

    fun setOnClickReadMore (onClickReadMore: (view: View) -> Unit): NewsCard  {
        this.onClickReadMore = onClickReadMore
        return this
    }

    fun build(): NewsCard {
        this.setBackgroundColor(Color.TRANSPARENT)
        View.inflate(context, R.layout.news_card, this)
        val windowManager = this.context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val deviceWidth = displayMetrics.widthPixels
        this.layoutParams = ViewGroup.LayoutParams(deviceWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.imageView = findViewById(R.id.image)
        val titleView = findViewById<TextView>(R.id.title)
        val descriptionView = findViewById<TextView>(R.id.description)
        val shareView = findViewById<Button>(R.id.share)
        val readMoreView = findViewById<Button>(R.id.read_more)

        readMoreView.setOnClickListener { this.onClickReadMore(it) }
        shareView.setOnClickListener {
            Snackbar.make(this, "Not implemented", Snackbar.LENGTH_LONG).show()
        }

        titleView.text = this.title ?: ""
        descriptionView.text = this.description ?: ""
        return this
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Picasso
            .with(context)
            .load(image)
            .fit()
            .centerCrop()
            .into(imageView)
    }

}
