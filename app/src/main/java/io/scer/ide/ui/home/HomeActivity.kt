package io.scer.ide.ui.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.github.javiersantos.piracychecker.PiracyChecker
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError
import com.github.javiersantos.piracychecker.enums.PirateApp
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import io.scer.ide.R
import io.scer.ide.db.entity.UserEntity
import io.scer.ide.ui.home.fragments.NewsFragment
import io.scer.ide.ui.home.fragments.ProjectsFragment
import io.scer.ide.ui.home.fragments.ToolsFragment
import io.scer.ide.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_home.*

const val SIGN_IN = 9002

class HomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    /**
     * Initialize
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null)
            replaceFragment(NewsFragment.newInstance())

        navigation.setOnNavigationItemSelectedListener(this)

        PiracyChecker(this)
                .enableGooglePlayLicensing("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkx8PqMbYunmJ+ZMR7YkHK0Z64vTaLGz527+R54Ku4jdCpv0APOCb6fzRjbh2HUP98rbtAbzPT04j05VEgAsjMWl2OcIFr74ycm3+Oy2H9eX/qJESqsi6ZqqBNSfCudPZMNDMOVZm4pu22Yh0MYU/uoeZ0Vj/yIOu0OEWzzPc4TdIM1RgImXHRgSH0B+kfWReYjFFj+MJh2MQmBS3nZdJZxcIFul6DCOyKbvqJJrXPPcxG7lMOupvFo32PsEMapcJG5DhlbCnh8TyfXt8FCNzPl8Kdnm1yksoPt70IFjZ54PxPbHnUAu9cWAEedbBVZgrvebhF1MvoX2JjtzTXcyNkQIDAQAB")
                .saveResultToSharedPreferences("license_preferences", "valid_license")
                .callback(this.licenseCheckCallback)
                .start()

        this@HomeActivity.googleAuth()
    }

    /**
     * TODO: Пофиксить отображение номера строки в редакторе
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

    /**
     * Check license callback
     */
    private val licenseCheckCallback = object: PiracyCheckerCallback() {
        override fun allow() {
            Log.e("LICENSE", "allow")
        }

        override fun dontAllow(error: PiracyCheckerError, app: PirateApp?) {
            Log.e("LICENSE", "dontAllow ${error.name}")
            this@HomeActivity.licenseDialog()
        }

        override fun onError(error: PiracyCheckerError) {
            Log.e("LICENSE", "error ${error.name}")
            this@HomeActivity.licenseDialog()
        }
    }

    /**
     * License status dialog
     */
    fun licenseDialog() =
        AlertDialog.Builder(this)
                .setMessage(R.string.license_error)
                .setCancelable(false)
                .setPositiveButton(R.string.license_purchase) { _: DialogInterface, _: Int->
                    val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse(
                            "http://market.android.com/details?id=$packageName"))
                    this.startActivity(marketIntent) }
                .setNegativeButton(R.string.close, { _: DialogInterface, _: Int -> this.finish() })
                .create()
                .show()

    /**
     * Auth in api by google token
     */
    private fun googleAuth() {
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
            Log.e("Home", account.idToken)
        }

        this.viewModel.get().observe(this, Observer<UserEntity> { user ->
            Log.e("User", if (user !== null) "${user.firstName} ${user.lastName} ${user.id}" else "user == null")
        })
    }

    /**
     * Handle google auth result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                Log.d("Home", account!!.idToken)
                this.viewModel.auth(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("Home", "signInResult:failed code=" + e.statusCode)
            }
        }
    }

}
