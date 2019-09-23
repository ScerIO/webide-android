package io.scer.fileexplorer

import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import io.scer.fileexplorer.adapters.FilesAdapter
import io.scer.fileexplorer.items.Item
import org.apache.commons.io.FileExistsException
import java.io.File

/**
 * Files view
 */
class FilesExplorerView : RelativeLayout, FileExplorer.IExplorer, View.OnKeyListener {
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
        listView = findViewById(R.id.list_view)

        explorer = FileExplorer(
                context = context,
                onExplore = this::onExplore,
                mode = mode,
                visibleExtensions = visibleExtensions,
                resultListener = resultListener)
        toolbar.title = if (mode == FileExplorer.MODE_FILE) resources.getString(R.string.select_file) else resources.getString(R.string.select_dir)

        if (showAddDirButton || showAddFileButton) {
            val addMenuItem = toolbar.menu.addSubMenu(Menu.NONE, R.id.menu_add, Menu.NONE, R.string.add)
            addMenuItem.setIcon(R.drawable.ic_add_black)
            addMenuItem.item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)

            if (showAddDirButton) {
                val addDirMenuItem = addMenuItem.add(Menu.NONE, R.id.menu_add_dir, Menu.NONE, R.string.folder)
                addDirMenuItem.setIcon(R.drawable.ic_folder_black)
            }

            if (showAddFileButton) {
                val addFileMenuItem = addMenuItem.add(Menu.NONE, R.id.menu_add_file, Menu.NONE, R.string.file)
                addFileMenuItem.setIcon(R.drawable.ic_insert_drive_file_black)
            }
        }

        if (showUpdateButton) {
            val updateMenuItem = toolbar.menu.add(Menu.NONE, R.id.menu_update, Menu.NONE, R.string.update)
            updateMenuItem.setIcon(R.drawable.ic_update_black)
            updateMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        if (mode == FileExplorer.MODE_DIR) {
            val selectDirMenuItem = toolbar.menu.add(Menu.NONE, R.id.menu_confirm, Menu.NONE, R.string.select_dir)
            selectDirMenuItem.setIcon(R.drawable.ic_check_black)
            selectDirMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
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
                                    Snackbar.make(this, R.string.dirname_empty, Snackbar.LENGTH_LONG).show()
                                else try {
                                    explorer.addFolder(filename.text.toString())
                                } catch (exception: FileExistsException) {
                                    Snackbar.make(this, R.string.error_creating_dir, Snackbar.LENGTH_LONG).show()
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
                                    Snackbar.make(this, R.string.filename_empty, Snackbar.LENGTH_LONG).show()
                                else try {
                                    explorer.addFile(filename.text.toString())
                                } catch (exception: FileExistsException) {
                                    Snackbar.make(this, R.string.error_creating_file, Snackbar.LENGTH_LONG).show()
                                }
                            })
                            .show()
                    true
                }
                R.id.menu_update -> {
                    explorer.explore(absoluteRoot ?: Environment.getExternalStorageDirectory(), currentDir)
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
        return this
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
     * Set root dir
     * @param root - Root
     * *
     * @return instance
     */
    fun setRootDir(root: File): FilesExplorerView {
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
    fun setVisibleExtensions(extensions: Array<String>): FilesExplorerView {
        this.visibleExtensions = extensions
        return this
    }

    /**
     * Show add dir button
     * @param show - default false
     * *
     * @return instance
     */
    fun showAddDirButton(show: Boolean): FilesExplorerView {
        this.showAddDirButton = show
        return this
    }

    /**
     * Show add file button
     * @param show - default false
     * *
     * @return instance
     */
    fun showAddFileButton(show: Boolean): FilesExplorerView {
        this.showAddFileButton = show
        return this
    }

    /**
     * Show update button
     * @param show - default false
     * *
     * @return instance
     */
    fun showUpdateButton(show: Boolean): FilesExplorerView {
        this.showUpdateButton = show
        return this
    }

    /**
     * Result callback
     * @param resultListener - Callback for select file or dir
     * *
     * @return instance
     */
    fun setResultListener(resultListener: (path: String, explorer: FileExplorer.IExplorer) -> Unit): FilesExplorerView {
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
