package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.searchproduct.SearchByNameResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.repository.SearchProductRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.SearchProductView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

// Digunakan untuk menjembatani Model dengan View pada Fragment
class SearchProductPresenter(private val view: SearchProductView.View,
                             private val context: Context,
                             private val product: SearchProductRepositoryImpl,
                             private val scheduler: SchedulerProvider) : SearchProductView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getProductByName(auth: String, name: String?) {
        view.displayProgressItem()

        compositeDisposable.add(product.searchProuctByName(auth, "application/json", name.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<SearchByNameResponse>(){
                override fun onComplete() {
                    view.hideProgressItem()
                }

                override fun onNext(t: SearchByNameResponse) {
                    try{
                        t.data?.let { it.dataProduct?.let { it1 -> view.showProductByName(it1) } }
                        t.data?.let { view.showProduct(it) }
                    }catch(ex: Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    view.hideProgressItem()
                    view.showProductByName(Collections.emptyList())

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

    override fun getProductByPage(auth: String, page: Int?) {
        view.displayProgressItem()

        compositeDisposable.add(product.searchProuctByPage(auth, "application/json", page!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<SearchByNameResponse>(){
                override fun onComplete() {
                    view.hideProgressItem()
                }

                override fun onNext(t: SearchByNameResponse) {
                    try{
                        t.data?.let { it.dataProduct?.let { it1 -> view.showProductByPage(it1) } }
                        t.data?.let { view.showProduct(it) }
                    }catch(ex: Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    view.hideProgressItem()
                    view.showProductByPage(Collections.emptyList())

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

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}