package io.sker.fileexplorer.items

/**
 * File item
 * @param type - File type (dir or file)
 * @param title - Title. For example - file name
 * @param subTitle - Subtitle. For example nested item count in dir
 * @param date - Last edit date
 * @param path - Full path of dir or file
 * @param imageDrawableId - Image resource id
 */
class Item(
        val type: Byte,
        val title: String,
        val subTitle: String,
        val date: String,
        val path: String,
        val imageDrawableId: Int) : Comparable<Item> {

    /**
     * Constants
     */
    companion object {
        // File type dir
        val TYPE_DIR: Byte = 0
        // File type file
        val TYPE_FILE: Byte = 1
    }

    /**
     * Compare for collection sort
     * @param other - Item
     * *
     * @return - Int
     */
    override fun compareTo(other: Item): Int = this.title.compareTo(other.title)

}
