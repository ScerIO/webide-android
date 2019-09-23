package io.scer.ide.ui.editor.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import io.scer.codeeditor.NCodeEditor
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class FileTabFragment : Fragment() {

    private lateinit var view: NCodeEditor

    val file by lazy(LazyThreadSafetyMode.NONE) {
        File(arguments!!.getString("path")!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = NCodeEditor(context!!)
        Log.e("FileTabFragmentView", "onCreateView")

        val code = file.readText()
        view.code = if (code.isEmpty()) " " else code
        view.codeEditor.isFocusableInTouchMode = true
        view.codeEditor.setSelection(1)

        view.setOnClickListener({
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view.codeEditor, InputMethodManager.SHOW_IMPLICIT)
        })

//        val view = TextView(context)
//        view.text = file.path

        return view
    }

    fun save () {
        file.writeText(view.code)
    }

    companion object {
        fun newInstance(path: String): FileTabFragment {
            val bundle = Bundle()
            bundle.putString("path", path)

            val fileTab = FileTabFragment()
            fileTab.arguments = bundle
            return fileTab
        }
    }

}