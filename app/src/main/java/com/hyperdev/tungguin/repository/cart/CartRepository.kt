package com.hyperdev.tungguin.repository.cart

import com.hyperdev.tungguin.model.cart.*
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface CartRepository {

    // Method untuk menambahkan item ke keranjang
    fun addToCart(
        token: String, accept: String,
        cartMap: HashMap<String, RequestBody>,
        file: MutableList<MultipartBody.Part>,
        fileDoc: MultipartBody.Part?,
        price: RequestBody?
    ): Observable<AddToCartResponse>

    // Method untuk menghapus item di keranjang
    fun deleteCartItem(token: String, accept: String, hashed_id: String): Flowable<DeleteCartResponse>

    // Method untuk memuat orderan yang telah masuk di keranjang
    fun getCartItem(token: String, accept: String): Flowable<CartResponse>

    // Method untuk mengecek ketersediaan voucher promo
    fun checkVoucher(token: String, accept: String, code: String): Observable<CheckVoucherResponse>

    // Method untuk checkout atau melakukan pembayaran
    fun checkout(token: String, accept: String, paymentType: String, voucher: String): Observable<CheckoutResponse>

}