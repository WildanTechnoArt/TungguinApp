package com.hyperdev.tungguin.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.repository.ProfileRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.TopUpView
import com.hyperdev.tungguin.view.ui.LoginPage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.io.IOException

//Digunakan untuk menjembatani Model dengan View pada Fragment
class TopUpPresenter(private val view: TopUpView.View,
                     private val profile: ProfileRepositoryImpl,
                     private val scheduler: SchedulerProvider) : TopUpView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getUserAmount(context: Context, token: String) {
        view.displayProgress()

        compositeDisposable.add(profile.getProfile(token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<ProfileResponse>(){
                override fun onComplete() {
                    view.hideProgress()
                }

                override fun onNext(t: ProfileResponse) {
                    try{
                        t.data?.let { view.displayAmount(it) }
                    }catch(ex: Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    if(e is IOException){
                        SharedPrefManager.getInstance(context).deleteToken()
                        Toast.makeText(context, "Silakan Login Kembali", Toast.LENGTH_LONG).show()
                        context.startActivity(Intent(context, LoginPage::class.java))
                        (context as Activity).finishAffinity()
                    }else if(e is HttpException){
                        view.hideProgress()
                        Toast.makeText(context, "Koneksi Internet Bermasalah!", Toast.LENGTH_LONG).show()
                    }
                }
            })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}