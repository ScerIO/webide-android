package io.scer.ide.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.schedulers.Schedulers
import io.scer.ide.GoogleAuthMutation
import io.scer.ide.api.ApiApolloClient
import io.scer.ide.db.AppDatabase
import io.scer.ide.db.entity.UserEntity
import io.scer.ide.model.Role

class UserViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app.applicationContext).userDao()

    fun get() = dao.get()

    fun auth(token: String): LiveData<UserEntity> {
        val query = ApiApolloClient.newInstance().mutate(
                GoogleAuthMutation
                        .builder()
                        .token(token)
                        .build()
        )

        Rx2Apollo.from(query)
                .subscribeOn(Schedulers.io())
                .subscribe({ userData: Response<GoogleAuthMutation.Data>? ->
                    val data = userData!!.data()!!.auth()!!.googleSign()!!.fragments().user()
                    Log.e("USER-DATA", data.firstName())
                    val user = UserEntity(id = data.id(),
                                          email = data.email(),
                                          role = data.role().toInt(),
                                          token = data.token(),
                                          firstName = data.firstName()!!,
                                          lastName = data.lastName()!!,
                                          picture = data.picture()!!)
                    dao.insert(user)
                }, { e: Throwable? ->
                    Log.e("UVM", e!!.message)
                })
        return dao.get()
    }
    fun add(user: UserEntity) = dao.insert(user)

    fun delete(user: UserEntity) = dao.delete(user)
}