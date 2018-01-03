package io.sker.ide.editor.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.sker.codeeditor.NCodeEditor
import io.sker.ide.util.readFile
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class FileTab : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = NCodeEditor(context!!)

        val file = File(arguments!!.getString("path")!!)

        view.code = readFile(file)

        return view
    }

}