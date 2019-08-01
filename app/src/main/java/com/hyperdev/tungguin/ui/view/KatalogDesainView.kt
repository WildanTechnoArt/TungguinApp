package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.katalogdesain.KatalogItem

class KatalogDesainView {

    interface View {
        fun showKatalogItemList(katalogItem: List<KatalogItem>)
        fun displayProgress()
        fun hideProgress()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun getKatalogDesain(token: String)
        fun onDestroy()
    }
}