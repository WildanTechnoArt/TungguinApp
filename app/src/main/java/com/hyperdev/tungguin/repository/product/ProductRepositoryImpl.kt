package com.hyperdev.tungguin.repository.product

import com.hyperdev.tungguin.model.detailproduct.DetailProductResponse
import com.hyperdev.tungguin.model.katalogdesain.KatalogDesainResponse
import com.hyperdev.tungguin.model.searchproduct.SearchProductResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class ProductRepositoryImpl(private val baseApiService: BaseApiService) : ProductRepository {

    // Method untuk menampilkan katalog desain
    override fun getKatalogDesain(token: String, accept: String): Flowable<KatalogDesainResponse> =
        baseApiService.getKatalogDesain(token, accept)

    // Method untuk mencari atau menampilkan produk (desain) berdasarkan namanya
    override fun searchProuctByName(token: String, accept: String, name: String): Flowable<SearchProductResponse> =
        baseApiService.searchProuctByName(token, accept, name)

    // Method untuk mencari atau menampilkan produk (desain) berdasarkan halaman
    override fun searchProuctByPage(token: String, accept: String, page: Int): Flowable<SearchProductResponse> =
        baseApiService.searchProuctByPage(token, accept, page)

    // Method untuk menampilkan detail produk (desain) yang akan dipesan
    override fun getDetailProduct(token: String, accept: String, hashed_id: String): Flowable<DetailProductResponse> =
        baseApiService.getDetailProduct(token, accept, hashed_id)

}