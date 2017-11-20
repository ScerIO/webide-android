package io.sker.fileexplorer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ListView
import io.sker.fileexplorer.R.drawable.ic_add_black
import io.sker.fileexplorer.R.drawable.ic_check_black
import io.sker.fileexplorer.items.Item
import java.io.File

/**
 * Dialog fragment
 */
class FileExplorerDialogFragment : DialogFragment(), FileExplorer.IExplorer, View.OnKeyListener {

    /**
     * Absolute root
     */
    private var absoluteRoot: File? = null

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
     * Show add dir button
     */
    private var showAddDirButton: Boolean = false

    /**
     * Callback
     */
    private var resultListener: ((patch: String, explorer: FileExplorer.IExplorer) -> Unit?)? = null

    /**
     * FileExplorer
     */
    private lateinit var explorer: FileExplorer

    /**
     * Check read storage permissions
     * @param savedInstanceState - Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
    }

    /**
     * Create all views element and main init
     * @param inflater - Layout inflater
     * @param container - View group
     * @param savedInstanceState - Bundle
     * *
     * @return - View
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.file_exlorer, container, false)
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(this)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val listView = view.findViewById<ListView>(R.id.list_view)

        val createDirectory = object : CreateDirectory() {
            override fun nameEmpty() = Snackbar.make(view, R.string.dirname_empty, Snackbar.LENGTH_LONG).show()
            override fun error() = Snackbar.make(view, R.string.error_creating_dir, Snackbar.LENGTH_LONG).show()
        }
        explorer = FileExplorer(
                context = context!!,
                createDirectory = createDirectory,
                listView = listView,
                mode = mode,
                visibleExtensions = visibleExtensions,
                resultListener = resultListener)
        toolbar.title = if (mode == FileExplorer.MODE_FILE) resources.getString(R.string.select_file) else resources.getString(R.string.select_dir)

        if (showAddDirButton) {
            val addDirMenuItem = toolbar.menu.add(Menu.NONE, R.id.menu_add_dir, Menu.NONE, "Add dir") // TODO Добавить текстовый ресурс
            addDirMenuItem.setIcon(ic_add_black)
            addDirMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        if (mode == FileExplorer.MODE_DIR) {
            val selectDirMenuItem = toolbar.menu.add(Menu.NONE, R.id.menu_confirm, Menu.NONE, "Select dir") // TODO Добавить текстовый ресурс
            selectDirMenuItem.setIcon(ic_check_black)
            selectDirMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        toolbar.setOnMenuItemClickListener{ item ->
            when (item.itemId) {
                R.id.menu_confirm -> {
                    if (resultListener != null)
                        resultListener!!(currentDir.path, this)
                    true
                }
                R.id.menu_add_dir -> explorer.addDirectory()
                else -> false
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = explorer.adapter.getItem(position)!!
            if (item.type == Item.TYPE_DIR) {
                currentDir = File(item.path)
                explorer.explore(absoluteRoot ?: Environment.getExternalStorageDirectory(), currentDir)
            } else
                if (resultListener != null)
                    resultListener!!(item.path, this)
        }
        explorer.explore(absoluteRoot ?: Environment.getExternalStorageDirectory(), currentDir)
        return view
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
             dismiss()
     }

    /**
     * Set showing mode
     * @param mode - Only dirs or dirs & files
     * *
     * @return instance
     */
    fun setMode(mode: Byte): FileExplorerDialogFragment {
        this.mode = mode
        return this
    }

    /**
     * Set root dir
     * @param root - Root
     * *
     * @return instance
     */
    fun setRootDir(root: File): FileExplorerDialogFragment {
        this.absoluteRoot = root
        this.currentDir = root
        return this
    }

    /**
     * Set visible extensions
     * @param extensions - List of extensions
     * *
     * @return instance
     */
    fun setVisibleExtensions(extensions: Array<String>): FileExplorerDialogFragment {
        this.visibleExtensions = extensions
        return this
    }

    /**
     * Show add dir button
     * @param show - default false
     * *
     * @return instance
     */
    fun showAddDirButton(show: Boolean): FileExplorerDialogFragment {
        this.showAddDirButton = show
        return this
    }

    /**
     * Result callback
     * @param resultListener - Callback for select file or dir
     * *
     * @return instance
     */
    fun setResultListener(resultListener: (patch: String, explorer: FileExplorer.IExplorer) -> Unit): FileExplorerDialogFragment {
        this.resultListener = resultListener
        return this
    }

    /**
     * Check read permission result
     * @param requestCode - Request code
     * @param permissions - All permissions
     * @param grantResults - Results
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() )
            grantResults
                    .filter { it -> it != PackageManager.PERMISSION_GRANTED }
                    .forEach { this.dismiss() }
    }

}
