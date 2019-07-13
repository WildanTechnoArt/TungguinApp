package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.cart.CartItem
import com.hyperdev.tungguin.model.cart.DeleteCartResponse
import com.hyperdev.tungguin.repository.cart.CartRepositoryImp
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.MyCartView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subscribers.ResourceSubscriber

class CartListAdapter(
    private val cartList: ArrayList<CartItem>,
    private val view: MyCartView.View,
    private val cart: CartRepositoryImp,
    private val scheduler: SchedulerProvider,
    private val token: String
) : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // Deklarasi View
        val getCartName: TextView = view.findViewById(R.id.cart_nama_desain)
        val getCartNumber: TextView = view.findViewById(R.id.cart_number_desain)
        val getCartPrice: TextView = view.findViewById(R.id.cart_desain_price)
        val getDeleteItem: ImageButton = view.findViewById(R.id.delete_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
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

        holder.getCartName.text = getNameCart
        holder.getCartNumber.text = getNumberCart
        holder.getCartPrice.text = getPriceCart

        view.showItemCount(getItemCount)

        holder.getDeleteItem.setOnClickListener {
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
}