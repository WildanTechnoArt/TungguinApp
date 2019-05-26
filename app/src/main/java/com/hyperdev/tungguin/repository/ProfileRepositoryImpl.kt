package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.announcement.AnnouncementResponse
import com.hyperdev.tungguin.model.dashboard.DashboardResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class ProfileRepositoryImpl (private val baseApiService: BaseApiService): ProfileRepository {
    override fun getProfile(token: String, accept: String): Flowable<ProfileResponse> = baseApiService.getProfile(token, accept)
    override fun getImageSlider(token: String): Flowable<DashboardResponse> = baseApiService.getDashboardSlider(token)
    override fun announcementDesigner(token: String): Flowable<AnnouncementResponse> = baseApiService.announcementDesigner(token)
}