package com.hyperdev.tungguin.network

import com.hyperdev.tungguin.model.cart.AddToCartResponse
import com.hyperdev.tungguin.model.dashboard.AnnouncementResponse
import com.hyperdev.tungguin.model.cart.CartResponse
import com.hyperdev.tungguin.model.chat.ChatHistoryResponse
import com.hyperdev.tungguin.model.chat.MessageModel
import com.hyperdev.tungguin.model.cart.CheckVoucherResponse
import com.hyperdev.tungguin.model.cart.CheckoutResponse
import com.hyperdev.tungguin.model.contact.ContactUsResponse
import com.hyperdev.tungguin.model.dashboard.DashboardResponse
import com.hyperdev.tungguin.model.cart.DeleteCartResponse
import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.model.detailproduct.DetailProductResponse
import com.hyperdev.tungguin.model.dashboard.FCMResponse
import com.hyperdev.tungguin.model.historiorder.HistoriOrderResponse
import com.hyperdev.tungguin.model.promodesain.PromoResponse
import com.hyperdev.tungguin.model.authentication.LoginResponse
import com.hyperdev.tungguin.model.orderlandingpage.OrderLandingPageResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.model.authentication.CityResponse
import com.hyperdev.tungguin.model.authentication.ProvinceResponse
import com.hyperdev.tungguin.model.authentication.RegisterResponse
import com.hyperdev.tungguin.model.chat.ChatListResponse
import com.hyperdev.tungguin.model.promodesain.BannerResponse
import com.hyperdev.tungguin.model.searchproduct.SearchProductResponse
import com.hyperdev.tungguin.model.topup.TopUpResponse
import com.hyperdev.tungguin.model.topup.HistoriTopUpResponse
import com.hyperdev.tungguin.model.topup.TopUpItemResponse
import com.hyperdev.tungguin.model.transaction.TransactionResponse
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.http.GET

interface BaseApiService {

    @POST("register")
    @FormUrlEncoded
    fun registerRequest(
        @Header("Accept") accept: String,
        @FieldMap register: HashMap<String, String>
    ): Observable<RegisterResponse>

    @POST("login")
    @FormUrlEncoded
    fun loginRequest(
        @Header("Accept") accept: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Observable<LoginResponse>

    @GET("data/provinces")
    fun getAllProvince(): Flowable<ProvinceResponse>

    @GET("data/provinces/{id}/cities")
    fun getAllCity(@Path("id") id: String): Flowable<CityResponse>

    @GET("profile")
    fun getProfile(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String
    ): Flowable<ProfileResponse>

    @POST("profile-update")
    @FormUrlEncoded
    fun updateProfile(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone_number") phone: String,
        @Field("province_id") province: String,
        @Field("city_id") city: String
    ): Observable<ProfileResponse>

    @POST("profile-update")
    @FormUrlEncoded
    fun updatePassword(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone_number") phone: String,
        @Field("province_id") province: String,
        @Field("city_id") city: String,
        @Field("password") password: String,
        @Field("password_confirmation") c_password: String
    ): Observable<ProfileResponse>

    @POST("forgot-password")
    @FormUrlEncoded
    fun forgotPassword(
        @Header("Accept") accept: String,
        @Field("email") email: String
    ): Observable<LoginResponse>

    @POST("wallet/topup")
    @FormUrlEncoded
    fun topUpMoney(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Field("amount") amount: String
    ): Observable<TopUpResponse>

    @GET("wallet/topup")
    fun getTopUpHistory(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Query("page") page: Int
    ): Flowable<HistoriTopUpResponse>

    @GET("wallet")
    fun getTransactionHistory(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Query("page") page: Int
    ): Flowable<TransactionResponse>

    @GET("wallet")
    fun getTransactionBalance(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String
    ): Flowable<TransactionResponse>

    @GET("products/search")
    fun searchProuctByName(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Query("name") name: String
    ): Flowable<SearchProductResponse>

    @GET("products/search")
    fun searchProuctByPage(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Query("page") page: Int
    ): Flowable<SearchProductResponse>

    @POST("contact-us")
    @FormUrlEncoded
    fun contactUs(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Field("title") title: String,
        @Field("content") content: String
    ): Observable<ContactUsResponse>

    @GET("products/landingpage")
    fun getOrderWithSlider(@Header("Authorization") token: String): Flowable<OrderLandingPageResponse>

    @GET("products/detail/{hashed_id}")
    fun getDetailProduct(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Path("hashed_id") hashed_id: String
    ): Flowable<DetailProductResponse>

    @GET("products/catalogs")
    fun getKatalogDesain(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String
    ): Flowable<PromoResponse>

    @POST("cart/add")
    @Multipart
    fun addToCart(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @PartMap cartMap: HashMap<String, RequestBody>,
        @Part file: MutableList<MultipartBody.Part>,
        @Part fileDoc: MultipartBody.Part?,
        @Part("price") price: RequestBody?
    ): Observable<AddToCartResponse>

    @GET("cart")
    fun getCartData(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String
    ): Flowable<CartResponse>

    @POST("cart/coupon-check")
    @FormUrlEncoded
    fun checkKupon(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Field("code") code: String
    ): Observable<CheckVoucherResponse>

    @GET("cart/delete/{hashed_id}")
    fun getCartDalete(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Path("hashed_id") hashed_id: String
    ): Flowable<DeleteCartResponse>

    @POST("order/checkout")
    @FormUrlEncoded
    fun checkout(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Field("payment_type") paymentType: String,
        @Field("voucher") voucher: String
    ): Observable<CheckoutResponse>

    @GET("order")
    fun getOrderHistori(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Query("page") page: Int
    ): Flowable<HistoriOrderResponse>

    @GET("order/{hashed_id}")
    fun getOrderDetail(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Path("hashed_id") hashed_id: String
    ): Flowable<DetailOrderResponse>

    @POST("fcm-token")
    @FormUrlEncoded
    fun fcmRequest(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Field("fcm_token") tokenFcm: String?
    ): Observable<FCMResponse>

    @GET("order/{hashed_id}/chat")
    fun getChatHistori(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Path("hashed_id") hashed_id: String,
        @Query("page") page: Int?
    ): Flowable<ChatHistoryResponse>

    @POST("order/{hashed_id}/chat")
    @Multipart
    fun chatRequest(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Path("hashed_id") hashed_id: String,
        @Part("text") text: RequestBody,
        @Part file: MultipartBody.Part?
    ): Observable<MessageModel>

    @POST("order/{hashed_id}/review")
    @FormUrlEncoded
    fun sendReview(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Path("hashed_id") hashed_id: String,
        @Field("star_rating") star: String,
        @Field("designer_testimonial") designer_testi: String,
        @Field("app_testimonial") app_testi: String,
        @Field("tip") tip: String
    ): Observable<DetailOrderResponse>

    @GET("chat")
    fun getChat(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Query("page") page: Int?
    ): Flowable<ChatListResponse>

    @GET("dashboard-slider")
    fun getDashboardSlider(@Header("Authorization") token: String): Flowable<DashboardResponse>

    @GET("customer-announcement")
    fun announcementDesigner(@Header("Authorization") token: String): Flowable<AnnouncementResponse>

    @GET("banner-promotion")
    fun getBannerPromotion(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String
    ): Flowable<BannerResponse>

    @GET("wallet/topup-available")
    fun getTopupAvailable(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String
    ): Flowable<TopUpItemResponse>
}