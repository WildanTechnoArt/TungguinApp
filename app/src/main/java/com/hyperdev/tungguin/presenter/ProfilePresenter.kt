package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.model.authentication.CityResponse
import com.hyperdev.tungguin.model.authentication.ProvinceResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.repository.profile.ProfileRepositoryImpl
import com.hyperdev.tungguin.repository.authentication.AuthRepositoryImp
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.ProfileView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ProfilePresenter(
    private val view: ProfileView.View,
    private val context: Context,
    private val profile: ProfileRepositoryImpl,
    private val register: AuthRepositoryImp,
    private val scheduler: SchedulerProvider
) : ProfileView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getUserProfile(token: String) {
        view.displayProgress()

        compositeDisposable.add(
            profile.getProfile(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ProfileResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: ProfileResponse) {
                        try {
                            t.data?.let { view.displayProfile(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (ConnectivityStatus.isConnected(context)) {
                            when (e) {
                                is HttpException -> // non 200 error codes
                                    HandleError.handleError(e, e.code(), context)
                                is SocketTimeoutException -> // connection errors
                                    Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Tidak Terhubung Dengan Internet!", Toast.LENGTH_LONG).show()
                        }
                    }
                })
        )
    }

    override fun getCityAll(id: String) {
        compositeDisposable.add(
            register.getCity(id)
                .observeOn(scheduler.ui())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<CityResponse>() {
                    override fun onComplete() {}

                    override fun onNext(t: CityResponse) {
                        try {
                            t.cityList?.let { view.displayCity(it) }
                        } catch (ex: Exception) {
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

        compositeDisposable.add(
            register.getProvince()
                .observeOn(scheduler.ui())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ProvinceResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: ProvinceResponse) {
                        try {
                            t.provinceList?.let { view.displayProvince(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        view.hideProgress()
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}