package io.scer.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment

/**
 * Диалог с вкладками
 */
class TabDialogFullscreen : TabDialog() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
    }

}