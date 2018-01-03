package io.sker.codeeditor.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.ScrollView
import io.sker.codeeditor.listeners.OnScrollListener
import io.sker.codeeditor.listeners.OnScrollReachedListener

/**
 * Custom scrollView with top & bottom reached listeners
 */
class InteractiveScrollView : ScrollView {
    /**
     * Scroll listener
     */
    private var onScrollListener: OnScrollListener? = null

    /**
     * On scroll reached listener (Bottom or top)
     */
    private var onScrollReachedListener: OnScrollReachedListener? = null

    /**
     * @constructor
     */
    constructor(context: Context) : super(context)

    /**
     * @constructor
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    /**
     * Detect scroll reached (Top or bottom)
     * *
     * @param scrollX      - Scroll in pixels
     * @param scrollY      - Scroll in pixels
     * @param oldScrollX   - Old scroll in pixels
     * @param oldScrollY   - Old scroll in pixels
     */
    override fun onScrollChanged(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val view = this.getChildAt(this.childCount - 1)
        val topDiff = view.top - (this.height - scrollY)
        val bottomDiff = view.bottom - (this.height + scrollY)

        if (topDiff > 30 && scrollY > oldScrollY)
            topReached = false
        else if (bottomDiff > 30 && scrollY < oldScrollY)
            bottomReached = false

        when {
            (this.onScrollListener != null) -> {
                this.onScrollListener!!.onScrolled()

                if (scrollY > oldScrollY)
                    this.onScrollListener!!.onScrolledDown()
                else
                    this.onScrollListener!!.onScrolledUp()

                super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY)
            }
            (this.onScrollReachedListener != null) -> {
                when {
                    // Top reached
                    (!topReached && scrollY < oldScrollY && topDiff <= 30) -> {
                        topReached = true
                        this.onScrollReachedListener!!.onTopReached({
                            super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY)
                        })
                    }
                    // Bottom reached
                    (!bottomReached && scrollY > oldScrollY && bottomDiff <= 30) -> {
                        bottomReached = true
                        this.onScrollReachedListener!!.onBottomReached({
                            super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY)
                        })
                    }
                }
            }
            else -> super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY)
        }
    }

    private var topReached = false
    private var bottomReached = false

    /**
     *
     */
    fun setOnScrollListener(onScrollListener: OnScrollListener) {
        this.onScrollListener = onScrollListener
    }

    fun getOnScrollListener(): OnScrollListener? = this.onScrollListener

    fun setOnScrollReachedListener(onScrollReachedListener: OnScrollReachedListener) {
        this.onScrollReachedListener = onScrollReachedListener
    }

    fun getOnScrollReachedListener(): OnScrollReachedListener? = this.onScrollReachedListener
}