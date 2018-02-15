package io.scer.ide.news

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.squareup.picasso.Picasso
import io.scer.ide.R

import kotlinx.android.synthetic.main.activity_news.*

class News : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        setSupportActionBar(toolbar)

        val imageView = findViewById<ImageView>(R.id.image)

        Picasso
            .with(this)
            .load(R.drawable.test)
            .fit()
            .into(imageView)
    }

}
