package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.announcement.AnnouncementResponse
import com.hyperdev.tungguin.model.dashboard.DashboardResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.repository.ProfileRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.DashboardView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

// Digunakan untuk menjembatani Model dengan View pada Fragment
class DashboardPresenter(private val view: DashboardView.View,
                         private val context: Context,
                         private val profile: ProfileRepositoryImpl,
                         private val scheduler: SchedulerProvider) : DashboardView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getSliderImage(token: String) {
        compositeDisposable.add(profile.getImageSlider(token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<DashboardResponse>(){
                override fun onComplete() {
                    view.hideProgress()
                }

                override fun onNext(t: DashboardResponse) {
                    try{
                        t.data?.let { view.shodSliderImage(it) }
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

    override fun getUserProfile(token: String) {
        view.displayProgress()
        compositeDisposable.add(profile.getProfile(token, "application/json")
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
                    view.hideProgress()

                    if(ConnectivityStatus.isConnected(context)){
                        when (e) {
                            is HttpException -> // non 200 error codes
                                HandleError.handleError(e, e.code(), context)
                            is SocketTimeoutException -> // connection errors
                                Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(context, "Tidak Terhubung Dengan Internet!", Toast.LENGTH_LONG).show()
                    }
                }
            })
        )
    }

    override fun getAnnouncementData(token: String) {
        compositeDisposable.add(profile.announcementDesigner(token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<AnnouncementResponse>(){
                override fun onComplete() {}

                override fun onNext(t: AnnouncementResponse) {
                    try{
                        t.data?.let { view.loaddAnnouncement(it) }
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

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}