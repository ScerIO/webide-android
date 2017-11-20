package io.sker.phpide.home

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.sker.phpide.R
import io.sker.phpide.home.fragments.News
import io.sker.phpide.home.fragments.Projects
import io.sker.phpide.home.fragments.TagFragment
import io.sker.phpide.home.fragments.Tools

class Home : AppCompatActivity() {

    /**
     * Утилиты
     */
    private val fragmentManager = supportFragmentManager

    /**
     * Идентефикатор последнего запущеного фрагмента
     */
    private lateinit var latestFragmentTag: String

    /**
     * Фрагменты
     */
    private lateinit var news: TagFragment
    private lateinit var projects: TagFragment
    private lateinit var tools: TagFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        //findViewById<View>(R.id.container).setPadding(0, getStatusBarHeight(this), 0, 0)
        findViewById<BottomNavigationView>(R.id.navigation).setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        news = fragmentManager.findFragmentByTag(News.TAG) as TagFragment? ?: News()
        projects = fragmentManager.findFragmentByTag(Projects.TAG) as TagFragment? ?: Projects()
        tools = fragmentManager.findFragmentByTag(Tools.TAG) as TagFragment? ?: Tools()

        var latestFragment: TagFragment? = null
        if (savedInstanceState != null) {
            latestFragmentTag = savedInstanceState.getString("latestFragmentTag")
            latestFragment = fragmentManager.findFragmentByTag(latestFragmentTag) as TagFragment?
        }

        if ( latestFragment != null )
            openFragment(latestFragment)
        else
            openFragment(news)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.news -> {
                openFragment(news)
                return@OnNavigationItemSelectedListener true
            }
            R.id.projects -> {
                openFragment(projects)
                return@OnNavigationItemSelectedListener true
            }
            R.id.tools -> {
                openFragment(tools)
                return@OnNavigationItemSelectedListener true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        finish()
    }

    /**
     * Открытие фрагмента
     * @param fragment - Фрагмент
     */
    private fun openFragment(fragment: TagFragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction
                .replace(R.id.content, fragment as Fragment, fragment.getTag)
                .addToBackStack(null)
                .commit()
        latestFragmentTag = fragment.getTag
    }

    /**
     * Сохранение тега фрагмента при повороте экрана
     */
    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("latestFragmentTag", latestFragmentTag)
    }

}
