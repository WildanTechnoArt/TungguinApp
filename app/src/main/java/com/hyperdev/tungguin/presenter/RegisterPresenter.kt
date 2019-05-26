package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.register.CityResponse
import com.hyperdev.tungguin.model.register.ProvinceResponse
import com.hyperdev.tungguin.repository.RegisterRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.RegisterView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

//Digunakan untuk menjembatani Model dengan View pada Fragment
class RegisterPresenter(private val view: RegisterView.View,
                        private val register: RegisterRepositoryImpl,
                        private val scheduler: SchedulerProvider) : RegisterView.Presenter{

    private val compositeDisposable = CompositeDisposable()

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

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}