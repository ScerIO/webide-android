package io.scer.ide.ui.editor

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
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

    private var tabs = ArrayList<Tab>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        val tabView = findViewById<TabLayout>(R.id.tab_layout)
        if (tabView.tabCount == 0)
            tabView.visibility = View.GONE

        this.tabLayout = findViewById(R.id.tab_layout)
        this.viewPager = findViewById(R.id.viewpager)

        val configPath = intent.getStringExtra("CONFIG_PATH") ?: "null"
        val explorer = FilesExplorerView(this)
                .setMode(FileExplorer.MODE_FILE)
                .setRootDir(File(configPath))
                .showAddDirButton(true)
                .showAddFileButton(true)
                .setResultListener { path, _ ->
                    if (tabView.visibility  != View.VISIBLE)
                        tabView.visibility = View.VISIBLE

                    var exist = false
                    (tabs.indices)
                        .filter { this.tabs[it].file.path == path }
                        .forEach {
                            exist = true
                            tabLayout.getTabAt(it)!!.select()
                        }

                    if (!exist) {
                        val bundle = Bundle()
                        bundle.putString("path", path)

                        val fileTab = FileTabFragment()
                        fileTab.arguments = bundle

                        this.tabs.add(Tab(0, File(path), fileTab))
                        viewPager.adapter!!.notifyDataSetChanged()

                        (tabs.indices)
                                .filter { this.tabs[it].file.path == path }
                                .forEach { tabLayout.getTabAt(it)!!.select() }
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
            setupViewPager(viewPager)
            tabLayout.setupWithViewPager(viewPager)
            for (i in tabs.indices) {
                val tab = tabLayout.getTabAt(i) ?: continue
                if (tabs[i].icon != 0)
                    tab.setIcon(tabs[i].icon)
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> Snackbar.make(this.viewPager, R.string.not_implemented, Snackbar.LENGTH_SHORT).show()
            R.id.save -> Snackbar.make(this.viewPager, R.string.not_implemented, Snackbar.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class ViewPagerAdapter internal constructor(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        override fun getCount(): Int = tabs.size

        override fun getItem(position: Int): Fragment = tabs[position].fragment

        override fun getPageTitle(position: Int): CharSequence = tabs[position].file.name
    }

    data class Tab (val icon: Int,
                    val file: File,
                    val fragment: Fragment)
}
