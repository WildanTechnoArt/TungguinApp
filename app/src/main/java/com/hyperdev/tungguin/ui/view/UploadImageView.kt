package com.hyperdev.tungguin.ui.view

import android.app.Activity
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData

class UploadImageView {

    interface View {
        fun loadImage(fileData: MutableList<FileData?>)
        fun loadFileDoc(fileDoc: FileData?)
        fun hideProgressFile()
        fun showProgressFile()
    }

    interface Presenter {
        fun takePhotoGallery(activity: Activity)
        fun takeFileDoc(activity: Activity)
        fun onDestroy()
    }
}