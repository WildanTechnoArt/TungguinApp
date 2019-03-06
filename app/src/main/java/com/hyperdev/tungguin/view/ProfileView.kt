package com.hyperdev.tungguin.view

import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.register.CityItem
import com.hyperdev.tungguin.model.register.ProvinceItem

class ProfileView {

    interface View{
        fun displayProvince(provinceItem: List<ProvinceItem>)
        fun displayCity(cityItem: List<CityItem>)
        fun displayProfile(profileItem: DataUser)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getProvinceAll()
        fun getCityAll(id: String)
        fun getUserProfile(token: String)
        fun onDestroy()
    }
}