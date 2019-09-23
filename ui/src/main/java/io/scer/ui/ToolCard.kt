package io.scer.ui

import android.content.Context
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.util.AttributeSet
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
class ToolCard : LinearLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init(): ToolCard {
        this.setBackgroundColor(Color.TRANSPARENT)
        View.inflate(context, R.layout.tool_card, this)
        val windowManager = this.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val deviceWidth = displayMetrics.widthPixels
        this.layoutParams = ViewGroup.LayoutParams(deviceWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        return this
    }

}
