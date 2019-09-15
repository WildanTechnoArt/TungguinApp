package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.promodesain.BannerData
import com.hyperdev.tungguin.model.promodesain.PromoData

class PromoDesainView {

    interface View {
        fun showKatalogItemList(katalogItem: List<PromoData>)
        fun showBennerItemL(bannerItem: List<BannerData>)
        fun displayProgress()
        fun hideProgress()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun getKatalogDesain(token: String)
        fun getBannerDesain(token: String)
        fun onDestroy()
    }
}