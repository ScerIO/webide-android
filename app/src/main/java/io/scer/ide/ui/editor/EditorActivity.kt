package io.scer.ide.ui.editor

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.scer.fileexplorer.FileExplorer
import io.scer.fileexplorer.FilesExplorerView
import io.scer.ide.R
import io.scer.ide.ui.editor.fragments.FileTabFragment
import io.scer.ide.util.getStatusBarHeight
import java.io.File

class EditorActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var drawer: DrawerLayout

    private var tabs = ArrayList<Tab>()
    private val adapter = ViewPagerAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        this.tabLayout = findViewById(R.id.tab_layout)
        this.viewPager = findViewById(R.id.viewpager)

        val configPath = intent.getStringExtra("CONFIG_PATH") ?: "null"
        val explorer = FilesExplorerView(this)
                .setMode(FileExplorer.MODE_FILE)
                .setRootDir(File(configPath))
                .showAddDirButton(true)
                .showAddFileButton(true)
                .setResultListener { path, _ ->
                    if (tabLayout.visibility != View.VISIBLE)
                        tabLayout.visibility = View.VISIBLE

                    var exist = false
                    (tabs.indices)
                        .filter { this.tabs[it].fragment.file.path == path }
                        .forEach {
                            exist = true
                            viewPager.currentItem = it
                        }

                    if (!exist) {
                        val fileTab = FileTabFragment.newInstance(path)

                        viewPager.currentItem = adapter.addItem(Tab(0, fileTab))
                    }

                    this.onBackPressed()
                }
                .init()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setPadding(0, getStatusBarHeight(this), 0, 0)

        navigationView.addView(explorer)

        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        tabLayout.post {
            viewPager.adapter = adapter
            tabLayout.setupWithViewPager(viewPager)
            tabs.forEachIndexed { index, tab ->
                val tabView = tabLayout.getTabAt(index) ?: return@forEachIndexed
                if (tab.icon != 0)
                    tabView.setIcon(tab.icon)
            }
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = menu@ when(item.itemId) {
            R.id.settings -> {
                Snackbar.make(this.viewPager, R.string.not_implemented, Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.save -> {
                try {
                    tabs[tabLayout.selectedTabPosition].fragment.save()
                    Snackbar.make(this.viewPager, R.string.success, Snackbar.LENGTH_SHORT).show()
                } catch (error: Exception) {
                    Snackbar.make(this.viewPager, R.string.editor_save_error, Snackbar.LENGTH_SHORT).show()
                }
                true
            }
            R.id.close -> {
                val tabPosition = viewPager.currentItem
                if (adapter.count == 0 || tabPosition < 0) return@menu false

                if (tabPosition != 0)
                    viewPager.currentItem = tabPosition - 1
                else if (tabPosition == 0 && viewPager.adapter!!.count > 1)
                    viewPager.currentItem = 1
                else
                    tabLayout.visibility = View.GONE

                adapter.removeItem(tabPosition, tabs[tabPosition].fragment)
                true
            }
            else -> false
        }

    private inner class ViewPagerAdapter internal constructor(val manager: FragmentManager) : FragmentPagerAdapter(manager) {
        override fun getCount(): Int = tabs.size

        override fun getItem(position: Int): FileTabFragment = tabs[position].fragment

        override fun getPageTitle(position: Int): CharSequence = tabs[position].fragment.file.name

        override fun getItemPosition(tabFragment: Any): Int {
            val position = getItemPositionByFragment(tabFragment as FileTabFragment)
            return if (position == -1) POSITION_NONE else position
        }

        fun addItem(tab: Tab): Int {
            tabs.add(tab)
            notifyDataSetChanged()
            return tabs.indexOf(tab)
        }

        fun removeItem(position: Int, tabFragment: Any) {
//            this.destroyItem(viewPager, position, tabFragment)
            manager
                    .beginTransaction()
                    .detach(tabFragment as FileTabFragment)
                    .remove(tabFragment)
                    .commit()
            tabs.removeAt(position)
//            tabs = tabs.filter { tab -> tab.fragment !== tabFragment } as ArrayList<Tab>
            notifyDataSetChanged()
        }

        fun getItemPositionByFragment(tabFragment: FileTabFragment): Int {
            Log.e("Path", tabFragment.file.path)
            tabs.forEachIndexed { index, tab ->
                if (tab.fragment.file.path === tabFragment.file.path)
                    return index
            }
            return -1
        }
    }

    data class Tab (val icon: Int,
                    val fragment: FileTabFragment)
}
