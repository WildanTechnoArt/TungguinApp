package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.dashboard.AnnouncementData
import com.hyperdev.tungguin.model.profile.DataUser

class DashboardView {

    interface View {

        // Menampilkan beberapa data user pada halaman dashboard
        fun showProfile(profileItem: DataUser)

        // Menampilkan gambar slider pada halaman dashboard
        fun showSliderImage(image: List<String>)

        // Menampilkan pengumuman atau informasi dari admin pada halaman dashboard
        fun showAnnouncement(text: AnnouncementData)

        // Menampilkan progressBar
        fun showProgressBar()

        // Menghilangkan progressBar
        fun hideProgressBar()

        // Untuk membuat statement jika token berhasil di reset saat logout
        fun onSuccessResetToken()
    }

    interface Presenter {

        // Mendapatkan beberapa data user, seperti nama dan wallet
        fun getUserProfile(token: String)

        // Mendapatkan data gambar slider yang akan ditampilkan pada halaman dashboard
        fun getSliderImage(token: String)

        // Mendapatkan data pengumuman atau informasi
        fun getAnnouncementData(token: String)

        // Digunakan untuk mengirim token fcm ke web service
        fun sendTokenFcm(token: String, accept: String, tokenFcm: String?)

        // Digunakan untuk mereset ulang token fcm setelah user logout
        fun resetTokenFcm(token: String, accept: String, tokenFcm: String?)

        // Untuk menghentikan proses request ke server dan mengembalikan keadaan menjadi seperti semula
        fun onDestroy()
    }
}