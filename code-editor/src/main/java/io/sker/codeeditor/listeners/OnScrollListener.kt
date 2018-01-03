package io.sker.codeeditor.listeners

/**
 * Scroll custom listener
 */
interface OnScrollListener {
    /**
     * Call on scroll
     */
    fun onScrolled()

    /**
     * Call only on scroll up
     */
    fun onScrolledUp()

    /**
     * Call only on scroll down
     */
    fun onScrolledDown()
}