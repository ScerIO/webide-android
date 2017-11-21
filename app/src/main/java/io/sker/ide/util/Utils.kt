package io.sker.ide.util

import android.Manifest
import android.content.Context
import android.support.v4.app.ActivityCompat


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