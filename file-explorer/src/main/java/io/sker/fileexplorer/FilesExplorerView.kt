package io.sker.fileexplorer

import android.content.Context
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import io.sker.fileexplorer.items.Item
import java.io.File

/**
 * Files view
 */
class FilesExplorerView : RelativeLayout, FileExplorer.IExplorer, View.OnKeyListener {

    /**
     * Actually current patch
     */
    private var currentDir: File = Environment.getExternalStorageDirectory()

    /**
     * Showing mode (Only dirs or dirs & files)
     */
    private var mode: Byte? = null

    /**
     * Visible extensions list
     */
    private var visibleExtensions: Array<String>? = null

    /**
     * FileExplorer
     */
    private lateinit var explorer: FileExplorer

    /**
     * Callback
     */
    private var resultListener: ((patch: String, explorer: FileExplorer.IExplorer) -> Unit?)? = null

    private var backButtonPressedInRootDirectory: (() -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun init(): FilesExplorerView {
        inflate(context, R.layout.file_exlorer, this)
        isFocusableInTouchMode = true
        requestFocus()
        setOnKeyListener(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val listView = findViewById<ListView>(R.id.list_view)

        val createDirectory = object : CreateDirectory() {
            override fun nameEmpty() = Snackbar.make(this@FilesExplorerView, R.string.dirname_empty, Snackbar.LENGTH_LONG).show()
            override fun error() = Snackbar.make(this@FilesExplorerView, R.string.error_creating_dir, Snackbar.LENGTH_LONG).show()
        }
        explorer = FileExplorer(
                context = context,
                createDirectory = createDirectory,
                listView = listView,
                mode = mode,
                visibleExtensions = visibleExtensions,
                resultListener = resultListener)
        toolbar.title = if (mode == FileExplorer.MODE_FILE) resources.getString(R.string.select_file) else resources.getString(R.string.select_dir)
        if (mode == FileExplorer.MODE_DIR) toolbar.inflateMenu(R.menu.context)
        toolbar.setOnMenuItemClickListener{ item ->
            when (item.itemId){
                R.id.select_dir -> {
                    if (resultListener != null)
                        resultListener!!(currentDir.path, this)
                    true
                }
                R.id.add_dir -> explorer.addDirectory()
                else -> false
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = explorer.adapter.getItem(position)!!
            if (item.type == Item.TYPE_DIR) {
                currentDir = File(item.path)
                explorer.explore(currentDir)
            } else
                if (resultListener != null)
                    resultListener!!(item.path, this)
        }
        explorer.explore(currentDir)
        return this
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackButtonPressed()
                return true
            }
        }

        return false
    }

    private fun onBackButtonPressed() {
        if(!explorer.exploreUp())
            if (backButtonPressedInRootDirectory != null) backButtonPressedInRootDirectory!!()
    }

    /**
     * Set showing mode
     * @param mode - Only dirs or dirs & files
     * *
     * @return instance
     */
    fun setMode(mode: Byte): FilesExplorerView {
        this.mode = mode
        return this
    }

    /**
     * Set visible extensions
     * @param extensions - List of extensions
     * *
     * @return instance
     */
    fun setVisibleExtensions(extensions: Array<String>): FilesExplorerView {
        this.visibleExtensions = extensions
        return this
    }

    /**
     * Result callback
     * @param resultListener - Callback for select file or dir
     * *
     * @return instance
     */
    fun setResultListener(resultListener: (patch: String, explorer: FileExplorer.IExplorer) -> Unit): FilesExplorerView {
        this.resultListener = resultListener
        return this
    }

    /**
     * Back Button
     * @param backButtonPressedInRootDirectory - Listener for tap back button in root directory (Only FilesExplorerView)
     * *
     * @return instance
     */
    fun setBackButtonPressedInRootDirectory(backButtonPressedInRootDirectory: (() -> Unit)): FilesExplorerView {
        this.backButtonPressedInRootDirectory = backButtonPressedInRootDirectory
        return this
    }

}