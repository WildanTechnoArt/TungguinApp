package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.promodesain.BannerResponse
import com.hyperdev.tungguin.model.promodesain.PromoResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.PromoDesainView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class PromoPresenter(
    private val view: PromoDesainView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : PromoDesainView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getBannerDesain(token: String) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getBannerPromotion(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<BannerResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: BannerResponse) {
                        try {
                            t.data?.let { view.showBennerItemL(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.hideProgress()
                        view.handleError(e)
                    }
                })
        )
    }

    override fun getKatalogDesain(token: String) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getKatalogDesain(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<PromoResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: PromoResponse) {
                        try {
                            t.data?.let { view.showKatalogItemList(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.hideProgress()
                        view.handleError(e)
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}