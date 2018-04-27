package io.scer.ide.ui.editor.fragments


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
        val code = readFile(file)

        view.code = if (code.isEmpty()) " " else code
        view.codeEditor.isFocusableInTouchMode = true
        view.codeEditor.setSelection(1)

        view.setOnClickListener({
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view.codeEditor, InputMethodManager.SHOW_IMPLICIT)
        })

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        view.code = ""
    }

}