package io.sker.phpide

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.sker.fileexplorer.FileExplorer
import io.sker.fileexplorer.FilesExplorerView
import io.sker.phpide.util.getStatusBarHeight
import java.io.File

class Editor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        val path = intent.getStringExtra("CONFIG_PATH") ?: "null"

        Log.v("Editor", path) // TODO Удалить тестовый вывод

        val explorer = FilesExplorerView(this)
                .setMode(FileExplorer.MODE_FILE)
                .setRootDir(File(path))
                .showAddDirButton(true)
                .setResultListener { patch, _ ->
                    Log.e("Editor", "onResultExplorer")
                }
                .setBackButtonPressedInRootDirectory {
                    Log.e("Editor", "onDismissExplorer")
                }
                .init()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setPadding(0, getStatusBarHeight(this), 0, 0)

        navigationView.addView(explorer)

        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        toggle.syncState()

        val tabView = findViewById<TabLayout>(R.id.tab_layout)
        if (tabView.tabCount == 0)
            tabView.visibility = View.GONE
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
