package io.scer.ide.ui.home.views

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import io.scer.ide.R

/**
 * Карта проекта
 */
class ProjectCardView(context: Context) : LinearLayout(context) {

    private var title: String? = null
    private var description: String? = null
    private var onRemoveClickListener: View.OnClickListener? = null
    private var onOpenClickListener: View.OnClickListener? = null

    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var removeView: Button
    private lateinit var openView: Button

    fun setTitle(title: String): ProjectCardView {
        this.title = title
        return this
    }

    fun setDescription(description: String): ProjectCardView {
        this.description = description
        return this
    }

    fun onRemoveClickListener(onRemoveClickListener: View.OnClickListener): ProjectCardView {
        this.onRemoveClickListener = onRemoveClickListener
        return this
    }

    fun onOpenClickListener(onOpenClickListener: View.OnClickListener): ProjectCardView {
        this.onOpenClickListener = onOpenClickListener
        return this
    }

    fun build() {
        this.setBackgroundColor(Color.TRANSPARENT)
        View.inflate(context, R.layout.project_card, this)
        this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        titleView = findViewById(R.id.name)
        descriptionView = findViewById(R.id.description)
        removeView = findViewById(R.id.remove)
        openView = findViewById(R.id.open)

        if (!this.title.isNullOrEmpty())       titleView.text = this.title             else titleView.visibility = View.GONE
        if (!this.description.isNullOrEmpty()) descriptionView.text = this.description else descriptionView.visibility = View.GONE

        if (this.onRemoveClickListener != null) removeView.setOnClickListener(onRemoveClickListener)
        if (this.onOpenClickListener != null)   openView.setOnClickListener(onOpenClickListener)
    }

}