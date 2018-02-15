package io.scer.codeeditor.listeners

/**
 * On scroll reached listener
 */
interface OnScrollReachedListener {
    /**
     * Call if scrollview reach top
     */
    fun onTopReached(callback: () -> Unit)

    /**
     * Call if scrollview reach bottom
     */
    fun onBottomReached(callback: () -> Unit)
}