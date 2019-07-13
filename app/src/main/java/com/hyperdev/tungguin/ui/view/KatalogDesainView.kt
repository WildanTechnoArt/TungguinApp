package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.katalogdesain.KatalogItem

class KatalogDesainView {

    interface View {
        fun showKatalogItemList(katalogItem: List<KatalogItem>)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun getKatalogDesain(token: String)
        fun onDestroy()
    }
}