package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.announcement.AnnouncementResponse
import com.hyperdev.tungguin.model.dashboard.DashboardResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import io.reactivex.Flowable

interface ProfileRepository {
    fun getProfile(token: String, accept: String) : Flowable<ProfileResponse>
    fun getImageSlider(token: String) : Flowable<DashboardResponse>
    fun announcementDesigner(token: String) : Flowable<AnnouncementResponse>
}