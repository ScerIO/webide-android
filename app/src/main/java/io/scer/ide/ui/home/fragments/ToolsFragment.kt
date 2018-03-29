package io.scer.ide.ui.home.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 *
 */
class ToolsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(io.scer.ide.R.layout.fragment_tools, container, false)

        return view
    }

    companion object {
        fun newInstance() = ToolsFragment()
    }

}
