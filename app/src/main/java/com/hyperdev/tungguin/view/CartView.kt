package com.hyperdev.tungguin.view

import com.hyperdev.tungguin.model.cart.CartData
import com.hyperdev.tungguin.model.cart.Item

class CartView {

    interface View{
        fun showCartItem(cartItem: List<Item>)
        fun showCartData(cartData: CartData)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getCartData(token: String)
        fun onDestroy()
    }
}