package io.scer.fileexplorer

import android.content.Context
import android.os.Environment
import android.text.format.DateFormat
import io.scer.fileexplorer.items.Item
import org.apache.commons.io.FileExistsException
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Explorer base
 * @param context - Context
 * @param mode - Showing mode (Only dirs or dirs & files)
 * @param visibleExtensions - Visible extensions list
 * @param resultListener - Callback
 */
class FileExplorer (
        private val context: Context,
        private val onExplore: (items: ArrayList<Item>) -> Unit,
        private val mode: Byte?,
        private var visibleExtensions: Array<String>?,
        private var resultListener: ((patch: String, explorer: FileExplorer.IExplorer) -> Unit?)?) {

    /**
     * Absolute root
     */
    private lateinit var absoluteRoot: File

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
    fun explore(absoluteRoot: File, rootDirectory: File) {
        this.absoluteRoot = absoluteRoot
        currentDir = rootDirectory
        val dirs = rootDirectory.listFiles()
        val dir = ArrayList<Item>()
        val files = ArrayList<Item>()
        try {
            for (file in dirs) {
                val lastModDate = Date(file.lastModified())
                val date = DateFormat.format("dd.MM.yyyy HH:mm", lastModDate).toString()
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
            dir.sort()
            if (mode == FileExplorer.MODE_FILE) {
                files.sort()
                dir.addAll(files)
            }
            if (rootDirectory.path != absoluteRoot.path)
                dir.add(0, Item(
                        type = Item.TYPE_DIR,
                        title = "...",
                        subTitle = context.resources.getString(R.string.parent_directory),
                        date = "",
                        path = rootDirectory.parent,
                        imageDrawableId = R.drawable.ic_arrow_up_black))
            this.onExplore(dir)
        }
    }

    fun exploreUp(): Boolean {
        val condition = currentDir.path != Environment.getExternalStorageDirectory().path
        if (condition) explore(absoluteRoot, File(currentDir.parent))
        return condition
    }

    /**
     * Add directory to filesystem
     * @param folderName
     * *
     * @return - True for toolbar menu click listener
     */
    fun addFolder (folderName: String) {
        val file = File("$currentDir/$folderName")
        if (!file.isDirectory) {
            file.mkdir()
            explore(absoluteRoot, currentDir)
        } else
            throw FileExistsException()
    }

    /**
     * Add file to filesystem
     * @param fileName
     * *
     * @return - True for toolbar menu click listener
     */
    fun addFile (fileName: String) {
        val file = File("$currentDir/$fileName")
        if (!file.exists()) {
            file.createNewFile()
            explore(absoluteRoot, currentDir)
        } else
            throw FileExistsException()
    }

    interface IExplorer

}
