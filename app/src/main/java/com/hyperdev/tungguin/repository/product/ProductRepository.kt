package com.hyperdev.tungguin.repository.product

import com.hyperdev.tungguin.model.detailproduct.DetailProductResponse
import com.hyperdev.tungguin.model.katalogdesain.KatalogDesainResponse
import com.hyperdev.tungguin.model.searchproduct.SearchProductResponse
import io.reactivex.Flowable

interface ProductRepository {

    // Method untuk menampilkan katalog desain
    fun getKatalogDesain(token: String, accept: String) : Flowable<KatalogDesainResponse>

    // Method untuk mencari atau menampilkan produk (desain) berdasarkan namanya
    fun searchProuctByName(token: String, accept: String, name: String) : Flowable<SearchProductResponse>

    // Method untuk mencari atau menampilkan produk (desain) berdasarkan halaman
    fun searchProuctByPage(token: String, accept: String, page: Int) : Flowable<SearchProductResponse>

    // Method untuk menampilkan detail produk (desain) yang akan dipesan
    fun getDetailProduct(token: String, accept: String, hashed_id: String) : Flowable<DetailProductResponse>

}