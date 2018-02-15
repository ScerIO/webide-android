package io.scer.ui

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

/**
 * Текст с обводкой
 */
class TextViewOutline : AppCompatTextView {

    constructor(context: Context) : super(context) {
        setShadowLayer(1.6f, 1.5f, 1.3f, 0xFFFFFF)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setShadowLayer(1.5f, 1.1f, 1.1f, 0xFFFFFF)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setShadowLayer(1.5f, 1.1f, 1.1f, 0xFFFFFF)
    }

}
