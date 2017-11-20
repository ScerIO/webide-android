package io.sker.ui

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

/**
 * Диалог с вкладками
 */
class TabDialog : DialogFragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    private var tabs: List<Tab>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = context

        val view = LinearLayout(context)
        view.orientation = LinearLayout.VERTICAL

        tabLayout = TabLayout(context)
        view.addView(tabLayout)

        val viewPagerParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        viewPagerParams.weight = 1.0f

        viewPager = ViewPager(context!!)
        viewPager.id = R.id.tab_dialog_viewpager
        viewPager.layoutParams = viewPagerParams
        view.addView(viewPager)

        tabLayout.post {
            setupViewPager(viewPager)
            tabLayout.setupWithViewPager(viewPager)
            for (i in tabs!!.indices) {
                val tab = tabLayout.getTabAt(i) ?: continue
                if (tabs!![i].icon != 0)
                    tab.setIcon(tabs!![i].icon)
                tabs!![i].fragment.dialogInstance = this
            }
        }
        return view
    }

    fun setTabs(tabs: List<Tab>) {
        this.tabs = tabs
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        this.dismiss()
    }

    private inner class ViewPagerAdapter internal constructor(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        override fun getCount(): Int = tabs!!.size

        override fun getItem(position: Int): TabDialogFragment = tabs!![position].fragment

    }

    data class Tab (val icon: Int,
                    val fragment: TabDialogFragment)

    abstract class TabDialogFragment : Fragment() {
        lateinit var dialogInstance: TabDialog
    }

}
