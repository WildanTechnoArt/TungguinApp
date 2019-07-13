package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.authentication.CityItem
import com.hyperdev.tungguin.model.authentication.ProvinceItem

class RegisterView {

    interface View {
        fun displayProvince(provinceItem: List<ProvinceItem>)
        fun displayCity(cityItem: List<CityItem>)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun postDataUser(register: HashMap<String, String>)
        fun getProvinceAll()
        fun getCityAll(id: String)
        fun onDestroy()
    }
}