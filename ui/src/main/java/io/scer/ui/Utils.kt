package io.scer.ui

import android.content.Context

/**
 * Get status bar height
 * @param context
 * *
 * @return - status bar height in pixels
 */
fun getStatusBarHeight(context: Context): Int {
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
}