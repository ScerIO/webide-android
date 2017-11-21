package io.sker.ide.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 *
 */
class Tools : TagFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(io.sker.ide.R.layout.fragment_tools, container, false)

        return view
    }

    companion object {
        const val TAG: String = "FRAGMENT_TOOLS"
    }

}
