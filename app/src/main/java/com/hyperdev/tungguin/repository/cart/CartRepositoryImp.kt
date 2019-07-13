package com.hyperdev.tungguin.repository.cart

import com.hyperdev.tungguin.model.cart.*
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CartRepositoryImp(private val baseApiService: BaseApiService) : CartRepository {

    // Method untuk menambahkan item ke keranjang
    override fun addToCart(
        token: String, accept: String,
        cartMap: HashMap<String, RequestBody>,
        file: MutableList<MultipartBody.Part>,
        fileDoc: MultipartBody.Part?,
        price: RequestBody?
    ): Observable<AddToCartResponse> = baseApiService.addToCart(token, accept, cartMap, file, fileDoc, price)

    // Method untuk menghapus item di keranjang
    override fun getCartItem(token: String, accept: String): Flowable<CartResponse> =
        baseApiService.getCartData(token, accept)

    // Method untuk memuat orderan yang telah masuk di keranjang
    override fun deleteCartItem(token: String, accept: String, hashed_id: String): Flowable<DeleteCartResponse> =
        baseApiService.getCartDalete(token, accept, hashed_id)

    // Method untuk mengecek ketersediaan voucher promo
    override fun checkVoucher(token: String, accept: String, code: String): Observable<CheckVoucherResponse> =
        baseApiService.checkKupon(token, accept, code)

    // Method untuk checkout atau melakukan pembayaran
    override fun checkout(token: String, accept: String, paymentType: String, voucher: String):
            Observable<CheckoutResponse> = baseApiService.checkout(token, accept, paymentType, voucher)

}