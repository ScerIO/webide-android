package io.scer.ide.ui.editor.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.scer.codeeditor.NCodeEditor
import io.scer.ide.util.readFile
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class FileTabFragment : Fragment() {

    private lateinit var view: NCodeEditor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        view = NCodeEditor(context!!)

        val file = File(arguments!!.getString("path")!!)

        view.code = readFile(file)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        view.code = ""
    }

}