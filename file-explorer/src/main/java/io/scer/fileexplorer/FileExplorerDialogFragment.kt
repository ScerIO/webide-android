package io.scer.fileexplorer

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import io.scer.fileexplorer.R.drawable.*
import io.scer.fileexplorer.adapters.FilesAdapter
import io.scer.fileexplorer.items.Item
import org.apache.commons.io.FileExistsException
import java.io.File

/**
 * Dialog fragment
 */
class FileExplorerDialogFragment : DialogFragment(), FileExplorer.IExplorer, View.OnKeyListener {
    /**
     * All files view
     */
    private lateinit var listView: ListView

    /**
     * Files adapter
     */
    lateinit var adapter: FilesAdapter

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
     * Show add file button
     */
    private var showAddFileButton: Boolean = false

    /**
     * Show update button
     */
    private var showUpdateButton: Boolean = true

    /**
     * Show close button
     */
    private var showCloseButton: Boolean = true

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
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
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
        listView = view.findViewById(R.id.list_view)

        explorer = FileExplorer(
                context = context!!,
                onExplore = this::onExplore,
                mode = mode,
                visibleExtensions = visibleExtensions,
                resultListener = resultListener)
        toolbar.title = if (mode == FileExplorer.MODE_FILE) resources.getString(R.string.select_file) else resources.getString(R.string.select_dir)

        if (showAddDirButton || showAddFileButton) {
            val addMenuItem = toolbar.menu.addSubMenu(Menu.NONE, R.id.menu_add, Menu.NONE, R.string.add)
            addMenuItem.setIcon(ic_add_black)
            addMenuItem.item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)

            if (showAddDirButton) {
                val addDirMenuItem = addMenuItem.add(Menu.NONE, R.id.menu_add_dir, Menu.NONE, R.string.folder)
                addDirMenuItem.setIcon(ic_folder_black)
            }

            if (showAddFileButton) {
                val addFileMenuItem = addMenuItem.add(Menu.NONE, R.id.menu_add_file, Menu.NONE, R.string.file)
                addFileMenuItem.setIcon(ic_insert_drive_file_black)
            }
        }

        if (showUpdateButton) {
            val updateMenuItem = toolbar.menu.add(Menu.NONE, R.id.menu_update, Menu.NONE, R.string.update)
            updateMenuItem.setIcon(R.drawable.ic_update_black)
            updateMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        if (showCloseButton) {
            val item = toolbar.menu.add(Menu.NONE, R.id.menu_close, Menu.NONE, R.string.close)
            item.setIcon(R.drawable.ic_close_black)
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        if (mode == FileExplorer.MODE_DIR) {
            val selectDirMenuItem = toolbar.menu.add(Menu.NONE, R.id.menu_confirm, Menu.NONE, R.string.select_dir)
            selectDirMenuItem.setIcon(ic_check_black)
            selectDirMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }

        toolbar.setOnMenuItemClickListener{ item ->
            when (item.itemId) {
                R.id.menu_confirm -> {
                    if (resultListener != null)
                        resultListener!!(currentDir.path, this)
                    true
                }
                R.id.menu_add_dir -> {
                    val filename = EditText(context)
                    val wrapper = LinearLayout(context)
                    val margin = context!!.resources.getDimension(R.dimen.alert_dialog_text_edit_margin).toInt()
                    val textEditParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
                    textEditParams.marginStart = margin
                    textEditParams.marginEnd = margin
                    filename.layoutParams = textEditParams
                    wrapper.addView(filename)

                    AlertDialog.Builder(context)
                            .setTitle(R.string.dir_name)
                            .setView(wrapper)
                            .setNegativeButton(android.R.string.cancel, { dialog, _ ->
                                dialog.dismiss()
                            })
                            .setPositiveButton(android.R.string.ok, { _, _ ->
                                if (filename.text.isEmpty())
                                    Snackbar.make(view, R.string.dirname_empty, Snackbar.LENGTH_LONG).show()
                                else try {
                                    explorer.addFolder(filename.text.toString())
                                } catch (exception: FileExistsException) {
                                    Snackbar.make(view, R.string.error_creating_dir, Snackbar.LENGTH_LONG).show()
                                }
                            })
                            .show()
                    true
                }
                R.id.menu_add_file -> {
                    val filename = EditText(context)
                    val wrapper = LinearLayout(context)
                    val margin = context!!.resources.getDimension(R.dimen.alert_dialog_text_edit_margin).toInt()
                    val textEditParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
                    textEditParams.marginStart = margin
                    textEditParams.marginEnd = margin
                    filename.layoutParams = textEditParams
                    wrapper.addView(filename)

                    AlertDialog.Builder(context)
                            .setTitle(R.string.file_name)
                            .setView(wrapper)
                            .setNegativeButton(android.R.string.cancel, { dialog, _ ->
                                dialog.dismiss()
                            })
                            .setPositiveButton(android.R.string.ok, { _, _ ->
                                if (filename.text.isEmpty())
                                    Snackbar.make(view, R.string.filename_empty, Snackbar.LENGTH_LONG).show()
                                else try {
                                    explorer.addFile(filename.text.toString())
                                } catch (exception: FileExistsException) {
                                    Snackbar.make(view, R.string.error_creating_file, Snackbar.LENGTH_LONG).show()
                                }
                            })
                            .show()
                    true
                }
                R.id.menu_update -> {
                    explorer.explore(absoluteRoot ?: Environment.getExternalStorageDirectory(), currentDir)
                    true
                }
                R.id.menu_close -> {
                    this@FileExplorerDialogFragment.dismiss()
                    true
                }
                else -> false
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = adapter.getItem(position)!!
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

    private fun onExplore(items: ArrayList<Item>) {
        adapter = FilesAdapter(context!!, R.layout.file_item, items)
        listView.adapter = adapter
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
     * Show add file button
     * @param show - default false
     * *
     * @return instance
     */
    fun showAddFileButton(show: Boolean): FileExplorerDialogFragment {
        this.showAddFileButton = show
        return this
    }

    /**
     * Show update button
     * @param show - default false
     * *
     * @return instance
     */
    fun showUpdateButton(show: Boolean): FileExplorerDialogFragment {
        this.showUpdateButton = show
        return this
    }

    /**
     * Show update button
     * @param show - default false
     * *
     * @return instance
     */
    fun showCloseButton(show: Boolean): FileExplorerDialogFragment {
        this.showCloseButton = show
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
        if (grantResults.isNotEmpty())
            grantResults
                    .filter { it -> it != PackageManager.PERMISSION_GRANTED }
                    .forEach { this.dismiss() }
    }

}
