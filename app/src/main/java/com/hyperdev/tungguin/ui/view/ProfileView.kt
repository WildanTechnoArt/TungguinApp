package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.authentication.CityItem
import com.hyperdev.tungguin.model.authentication.ProvinceItem

class ProfileView {

    interface View {
        fun displayProvince(provinceItem: List<ProvinceItem>)
        fun displayCity(cityItem: List<CityItem>)
        fun displayProfile(profileItem: DataUser)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun getProvinceAll()
        fun getCityAll(id: String)
        fun getUserProfile(token: String)
        fun onDestroy()
    }
}