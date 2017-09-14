package io.sker.fileexplorer

import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.text.format.DateFormat
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import io.sker.fileexplorer.adapters.FilesAdapter
import io.sker.fileexplorer.items.Item
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.util.*

/**
 * Explorer base
 * @param context - Context
 * @param createDirectory - Wrapper
 * @param listView - Files ListView
 * @param mode - Showing mode (Only dirs or dirs & files)
 * @param visibleExtensions - Visible extensions list
 * @param resultListener - Callback
 */
class FileExplorer (
        private val context: Context,
        private val createDirectory: CreateDirectory,
        private val listView: ListView,
        private val mode: Byte?,
        private var visibleExtensions: Array<String>?,
        private var resultListener: ((patch: String, explorer: FileExplorer.IExplorer) -> Unit?)?) {

    /**
     * Files adapter
     */
    lateinit var adapter: FilesAdapter

    /**
     * Actually current patch
     */
    private var currentDir: File = Environment.getExternalStorageDirectory()

    /**
     * Constants
     */
    companion object {
        // Show dirs and files
        const val MODE_FILE: Byte = 0
        // Show only dirs
        const val MODE_DIR: Byte = 1
    }

    /**
     * Fill files list
     * @param rootDirectory - Directory for explore
     */
    fun explore(rootDirectory: File) {
        currentDir = rootDirectory
        val dirs = rootDirectory.listFiles()
        val dir = ArrayList<Item>()
        val files = ArrayList<Item>()
        try {
            for (file in dirs) {
                val lastModDate = Date(file.lastModified())
                val date = DateFormat.format("dd-MM-yyyy HH:mm", lastModDate).toString()
                if (file.isDirectory) {
                    val listFiles = file.listFiles()
                    dir.add(Item(
                            type = Item.TYPE_DIR,
                            title = file.name,
                            subTitle = "${listFiles?.size ?: 0} ${context.resources.getString(R.string.items)}",
                            date = date,
                            path = file.absolutePath,
                            imageDrawableId = R.drawable.ic_folder_black))
                } else {
                    if (mode == FileExplorer.MODE_FILE) {
                        if (visibleExtensions != null) {
                            if (visibleExtensions!!.indexOf(FilenameUtils.getExtension(file.path)) != -1)
                                files.add(Item(
                                        type = Item.TYPE_FILE,
                                        title = file.name,
                                        subTitle = "${file.length()} ${context.resources.getString(R.string.weight)}",
                                        date = date,
                                        path = file.absolutePath,
                                        imageDrawableId = R.drawable.ic_insert_drive_file_black))
                        } else
                            files.add(Item(
                                    type = Item.TYPE_FILE,
                                    title = file.name,
                                    subTitle = "${file.length()} ${context.resources.getString(R.string.weight)}",
                                    date = date,
                                    path = file.absolutePath,
                                    imageDrawableId = R.drawable.ic_insert_drive_file_black))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            Collections.sort(dir)
            if (mode == FileExplorer.MODE_FILE) {
                Collections.sort(files)
                dir.addAll(files)
            }
            if (rootDirectory.path != Environment.getExternalStorageDirectory().path)
                dir.add(0, Item(
                        type = Item.TYPE_DIR,
                        title = "..",
                        subTitle = context.resources.getString(R.string.parent_directory),
                        date = "",
                        path = rootDirectory.parent,
                        imageDrawableId = R.drawable.ic_arrow_upward_black))
            adapter = FilesAdapter(context, R.layout.file_item, dir)
            listView.adapter = adapter
        }
    }

    fun exploreUp(): Boolean {
        val condition = currentDir.path != Environment.getExternalStorageDirectory().path
        if (condition) explore(File(currentDir.parent))
        return condition
    }

    /**
     * Add directory to filesystem
     * *
     * @return - True for toolbar menu click listener
     */
    fun addDirectory () : Boolean {
        val filename = EditText(context)
        val wrapper = LinearLayout(context)
        val margin = context.resources.getDimension(R.dimen.alert_dialog_text_edit_margin).toInt()
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
                        createDirectory.nameEmpty()
                    else {
                        val file = File("$currentDir/${filename.text}")
                        if (!file.isDirectory) {
                            file.mkdir()
                            explore(currentDir)
                        } else
                            createDirectory.error()
                    }
                })
                .show()
        return true
    }

    interface IExplorer

}
