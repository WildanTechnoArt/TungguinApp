package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.cart.CartItem
import com.hyperdev.tungguin.model.cart.DeleteCartResponse
import com.hyperdev.tungguin.repository.cart.CartRepositoryImp
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.MyCartView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.cart_item.view.*

/*
  CartAdapter digunkan untuk mengatur item pada daftar
  belanjaan yang tampil pada CartActivity
 */

class CartAdapter(
    private val cartList: ArrayList<CartItem>,
    private val view: MyCartView.View,
    private val cart: CartRepositoryImp,
    private val scheduler: SchedulerProvider,
    private val token: String
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = cartList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val getNameCart = cartList[position].productName.toString()
        val getNumberCart = cartList[position].formattedNumberOfDesign.toString()
        val getPriceCart = cartList[position].formattedPrice.toString()
        val getId = cartList[position].hashedId.toString()
        val getItemCount = cartList.size.toString()

        holder.itemView.tv_product_name.text = getNameCart
        holder.itemView.tv_number_designs.text = getNumberCart
        holder.itemView.tv_design_price.text = getPriceCart

        view.showItemCount(getItemCount)

        holder.itemView.btn_delete_item.setOnClickListener {
            val message: AlertDialog.Builder = AlertDialog.Builder(it.context)
                .setMessage("Anda yakin ingin menghapusnya?")
                .setPositiveButton("Ya") { _, _ ->
                    view.showProgressBar()

                    cart.deleteCartItem(token, "application/json", getId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(scheduler.io())
                        .subscribeWith(object : ResourceSubscriber<DeleteCartResponse>() {
                            override fun onComplete() {
                                view.onSuccessDeleteItem()

                                if (cartList.size < 1) {
                                    view.showItemCount("0")
                                } else {
                                    view.showItemCount(getItemCount)
                                }
                            }

                            override fun onNext(t: DeleteCartResponse) {
                                onDeleteData(position)
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                                view.hideProgressBar()
                            }
                        })
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
            message.create()
            message.show()
        }
    }

    private fun onDeleteData(position: Int?) {
        cartList.removeAt(position!!)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, cartList.size)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}