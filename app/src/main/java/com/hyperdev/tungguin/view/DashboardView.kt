package com.hyperdev.tungguin.view

import com.hyperdev.tungguin.model.announcement.AnnouncementData
import com.hyperdev.tungguin.model.profile.DataUser

class DashboardView {

    interface View{
        fun displayProfile(profileItem: DataUser)
        fun shodSliderImage(image: List<String>)
        fun loaddAnnouncement(text: AnnouncementData)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getUserProfile(token: String)
        fun getSliderImage(token: String)
        fun getAnnouncementData(token: String)
        fun onDestroy()
    }
}