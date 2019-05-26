package com.hyperdev.tungguin.view

import com.hyperdev.tungguin.model.register.CityItem
import com.hyperdev.tungguin.model.register.ProvinceItem

class RegisterView {

    interface View{
        fun displayProvince(provinceItem: List<ProvinceItem>)
        fun displayCity(cityItem: List<CityItem>)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getProvinceAll()
        fun getCityAll(id: String)
        fun onDestroy()
    }
}