package io.scer.fileexplorer.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import io.scer.fileexplorer.R
import io.scer.fileexplorer.items.Item

/**
 * Files adapter
 * @param appContext - Context
 * @param textViewLayoutId - Text view layout resource id
 * @param items - All files & dirs
 */
class FilesAdapter(appContext: Context,
                   private val textViewLayoutId: Int,
                   private val items: List<Item>) : ArrayAdapter<Item>(appContext, textViewLayoutId, items) {

    /**
     * Get item by position in list
     * *
     * @return - Item
     */
    override fun getItem(position: Int): Item? = items[position]

    /**
     * Item wrapped in layout
     * @param position - Position number
     * @param parentView - Parent view
     * @param parent - View group
     * *
     * @return - Item view
     */
    override fun getView(position: Int, parentView: View?, parent: ViewGroup): View? {
        var view = parentView
        if (view == null)
            view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(textViewLayoutId, null)

        val item = items[position]

        val name = view!!.findViewById<TextView>(R.id.name)
        val nestedItemsCount = view.findViewById<TextView>(R.id.nestedItemsCount)
        val lastEditDate = view.findViewById<TextView>(R.id.lastEditDate)
        val icon = view.findViewById<ImageView>(R.id.icon)
        // Set icon
        val iconDrawable: Drawable =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            context.resources.getDrawable(item.imageDrawableId, context.theme)
        } else {
            context.resources.getDrawable(item.imageDrawableId)
        }
        icon.setImageDrawable(iconDrawable)

        // Bind values
        if (name != null)
            name.text = item.title
        if (nestedItemsCount != null)
            nestedItemsCount.text = item.subTitle
        if (lastEditDate != null)
            lastEditDate.text = item.date

        // Fade and translate animation
        val animation1: Animation =  AnimationUtils.loadAnimation(context,
            R.anim.fade_and_translate)
        view.startAnimation(animation1)

        return view
    }

}
