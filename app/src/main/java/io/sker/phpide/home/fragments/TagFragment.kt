package io.sker.phpide.home.fragments

import android.support.v4.app.Fragment

abstract class TagFragment : Fragment() {
    val getTag: String = TagFragment.TAG
    companion object {
        const val TAG = ""
    }
}