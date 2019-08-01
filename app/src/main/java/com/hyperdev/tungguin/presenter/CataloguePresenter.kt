package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.katalogdesain.KatalogDesainResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.KatalogDesainView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class CataloguePresenter(
    private val view: KatalogDesainView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : KatalogDesainView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getKatalogDesain(token: String) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getKatalogDesain(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<KatalogDesainResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: KatalogDesainResponse) {
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