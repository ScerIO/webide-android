package io.scer.ide.ui.home

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import io.scer.ide.R
import io.scer.ide.ui.home.fragments.NewsFragment
import io.scer.ide.ui.home.fragments.ProjectsFragment
import io.scer.ide.ui.home.fragments.ToolsFragment
import kotlinx.android.synthetic.main.activity_home.*

const val SIGN_IN = 9002

class HomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    /**
     * Initialize
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null)
            replaceFragment(NewsFragment.newInstance())

        navigation.setOnNavigationItemSelectedListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("869275983652-07h1lnhc4viov33j8c4nmotp4v4qsmo3.apps.googleusercontent.com")
                .requestEmail()
                .build()

        val mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {
            if (!mGoogleApiClient.isConnected) {
                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                startActivityForResult(signInIntent, SIGN_IN)
            }
        } else {
            Log.d("Home", account.idToken)
        }
    }

    /**
     * TODO: Если сборка тестовая - смотреть на локальный апи
     * TODO: Детальный просмотр новости
     * TODO: Пофиксить отображение номера строки в редакторе
     * TODO: Добавить проверку лицензии
     */

    /**
     * Select view fragment
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.news -> {
                replaceFragment(NewsFragment.newInstance())
                return true
            }
            R.id.projects -> {
                replaceFragment(ProjectsFragment.newInstance())
                return true
            }
            R.id.tools -> {
                replaceFragment(ToolsFragment.newInstance())
                return true
            }
        }
        return false
    }

    /**
     * Exit on back button pressed
     */
    override fun onBackPressed() {
        finish()
    }

    /**
     * Replace fragment view
     * *
     * @param fragment - Fragment view
     */
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                Log.d("Home", "Complete" + task.isSuccessful)
                val account = task.getResult(ApiException::class.java)

                Log.d("Home", account.idToken)
            } catch (e: ApiException) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w("Home", "signInResult:failed code=" + e.statusCode)
            }
        }
    }

}
