package com.hyperdev.tungguin.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.model.register.CityResponse
import com.hyperdev.tungguin.model.register.ProvinceResponse
import com.hyperdev.tungguin.repository.ProfileRepositoryImpl
import com.hyperdev.tungguin.repository.RegisterRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.ProfileView
import com.hyperdev.tungguin.view.ui.LoginPage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.io.IOException

//Digunakan untuk menjembatani Model dengan View pada Fragment
class ProfilePresenter(private val view: ProfileView.View,
                       private val context: Context,
                       private val profile: ProfileRepositoryImpl,
                       private val register: RegisterRepositoryImpl,
                       private val scheduler: SchedulerProvider) : ProfileView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getUserProfile(token: String) {
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
                        t.data?.let { view.displayProfile(it) }
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

    override fun getCityAll(id: String) {
        compositeDisposable.add(register.getCityAll(id)
            .observeOn(scheduler.ui())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<CityResponse>(){
                override fun onComplete() {}

                override fun onNext(t: CityResponse) {
                    try{
                        t.cityList?.let { view.displayCity(it) }
                    }catch(ex: Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
        )
    }

    override fun getProvinceAll() {
        view.displayProgress()

        compositeDisposable.add(register.getProvinceAll()
            .observeOn(scheduler.ui())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<ProvinceResponse>(){
                override fun onComplete() {
                    view.hideProgress()
                }

                override fun onNext(t: ProvinceResponse) {
                    try{
                        t.provinceList?.let { view.displayProvince(it) }
                    }catch(ex: Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    view.hideProgress()
                    Toast.makeText(context, "Data gagal dimuat!", Toast.LENGTH_LONG).show()
                }
            })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}