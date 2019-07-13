package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.detailproduct.FieldListFormatted
import com.hyperdev.tungguin.model.detailproduct.PriceList
import com.hyperdev.tungguin.model.detailproduct.ProductDetailItem

class DetailProductView {

    interface View {
        fun showPriceList(priceList: List<PriceList>)
        fun showFieldList(fieldListFormatted: List<FieldListFormatted>)
        fun showDetailProductItem(productItem: ProductDetailItem)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun getDetailProduct(token: String, hashed_id: String)
        fun onDestroy()
    }
}