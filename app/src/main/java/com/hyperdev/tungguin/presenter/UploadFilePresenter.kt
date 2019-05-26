package com.hyperdev.tungguin.presenter

import android.app.Activity
import android.app.Activity.RESULT_OK
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import com.hyperdev.tungguin.view.UploadImageView

class UploadFilePresenter(private val view: UploadImageView.View,
                          private val scheduler: SchedulerProvider) : UploadImageView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun takePhotoGallery(activity: Activity){
        view.showProgressFile()
        compositeDisposable.add(
            RxPaparazzo.multiple(activity)
                .usingGallery()
                .subscribeOn(scheduler.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.resultCode() != RESULT_OK) {
                        view.hideProgressFile()
                        return@subscribe
                    }

                    view.hideProgressFile()
                    view.loadImage(it.data())
                }
        )
    }

    override fun takeFileDoc(activity: Activity) {
        view.showProgressFile()
        compositeDisposable.add(
            RxPaparazzo.single(activity)
                .usingFiles()
                .subscribeOn(scheduler.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.resultCode() != RESULT_OK) {
                        view.hideProgressFile()
                        return@subscribe
                    }

                    view.hideProgressFile()
                    view.loadFileDoc(it.data())
                }
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}