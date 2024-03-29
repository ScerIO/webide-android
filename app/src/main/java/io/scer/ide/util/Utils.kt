package io.scer.ide.util

import android.Manifest
import android.content.Context
import android.content.res.Resources
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.Toolbar
import android.widget.TextView

/**
 * Получение высоты статусбара
 * @param context - Контекст
 * *
 * @return - Высота статусбара
 */
fun getStatusBarHeight(context: Context): Int {
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
}

/**
 * Получение высоты навбара
 * @param context - Контекст
 * *
 * @return - Высота навбара
 */
fun getNavigationBarHeight(context: Context): Int {
    val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
}

/**
 * @param context - Контекст
 * @param permissions - Разрешения приложения
 * *
 * @return - Массив, содержащий ответы
 */
fun checkPermissions(context: Context, permissions: Array<String>): ArrayList<Int> {
    val result: ArrayList<Int> = ArrayList()
    for ( permission in permissions )
        result.add(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE))
    return result
}

/**
 * Generate unique id
 */
fun uniqueId (): Int {
    return System.currentTimeMillis().toInt()
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()