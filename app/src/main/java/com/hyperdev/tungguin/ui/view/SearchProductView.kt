package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.searchproduct.SearchItem
import com.hyperdev.tungguin.model.searchproduct.SearchProductData

class SearchProductView {

    interface View{

        // Menampilkan produk (desain) berdasarkan nama yang di cari (search)
        fun showProductByName(product: List<SearchItem>)

        // Menampilkan produk (desain) berdasarkan nomor halaman
        fun showProductByPage(product: List<SearchItem>)

        // Mendapatkan next url produk untuk berpindah halaman
        fun getProductPageUrl(product: SearchProductData)

        // Menampilkan progressBar saat memuat data produk yang dicari
        fun showSearchProgressBar()

        //Menghilangkan progressBar setelah data produk yang dicari berhasil dimuat
        fun hideSearchProgressBar()
    }

    interface Presenter{

        // Untuk mencari produk berdasarkan nama produknya
        fun getProductByName(token: String, name: String?)

        // Untuk mencari produk berdasarkan nomor halaman
        fun getProductByPage(token: String, page: Int?)

        // Untuk menghentikan proses request ke server dan mengembalikan keadaan menjadi seperti semula
        fun onDestroy()
    }
}