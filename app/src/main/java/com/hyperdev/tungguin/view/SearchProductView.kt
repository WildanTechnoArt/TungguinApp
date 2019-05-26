package com.hyperdev.tungguin.view

import com.hyperdev.tungguin.model.searchproduct.DesignItem
import com.hyperdev.tungguin.model.searchproduct.SearchProductData

class SearchProductView {

    interface View{
        fun showProductByName(product: List<DesignItem>)
        fun showProductByPage(product: List<DesignItem>)
        fun showProduct(product: SearchProductData)
        fun displayProgressItem()
        fun hideProgressItem()
    }

    interface Presenter{
        fun getProductByName(auth: String, name: String?)
        fun getProductByPage(auth: String, page: Int?)
        fun onDestroy()
    }
}