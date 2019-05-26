package com.hyperdev.tungguin.network

import com.hyperdev.tungguin.model.addtocart.AddToCartResponse
import com.hyperdev.tungguin.model.announcement.AnnouncementResponse
import com.hyperdev.tungguin.model.cart.CartResponse
import com.hyperdev.tungguin.model.chat.ChatHistoriResponse
import com.hyperdev.tungguin.model.chat.MessageModel
import com.hyperdev.tungguin.model.checkkupon.CheckKuponResponse
import com.hyperdev.tungguin.model.checkout.CheckoutResponse
import com.hyperdev.tungguin.model.contactus.ContactUsResponse
import com.hyperdev.tungguin.model.dashboard.DashboardResponse
import com.hyperdev.tungguin.model.deletecart.DeleteCartResponse
import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.model.detailproduct.DetailProductResponse
import com.hyperdev.tungguin.model.fcm.FCMResponse
import com.hyperdev.tungguin.model.historiorder.HistoriOrderResponse
import com.hyperdev.tungguin.model.katalogdesain.KatalogDesainResponse
import com.hyperdev.tungguin.model.login.LoginResponse
import com.hyperdev.tungguin.model.orderlandingpage.OrderLandingPageResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.model.register.CityResponse
import com.hyperdev.tungguin.model.register.ProvinceResponse
import com.hyperdev.tungguin.model.register.RegisterResponse
import com.hyperdev.tungguin.model.searchproduct.SearchByNameResponse
import com.hyperdev.tungguin.model.topup.TopUpResponse
import com.hyperdev.tungguin.model.topuphistori.HistoriTopUpResponse
import com.hyperdev.tungguin.model.transactionhistory.TransactionResponse
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.GET

// Berisi Perintah Untuk Berkomunikasi Dengan API
interface BaseApiService {

    @POST("register")
    @FormUrlEncoded
    fun registerRequest(@FieldMap register: HashMap<String,String>): Call<RegisterResponse>

    @POST("login")
    @FormUrlEncoded
    fun loginRequest(@Field("email") email: String,
                     @Field("password") password: String): Call<LoginResponse>

    @GET("data/provinces")
    fun getAllProvince() : Flowable<ProvinceResponse>

    @GET("data/provinces/{id}/cities")
    fun getAllCity(@Path("id") id: String) : Flowable<CityResponse>

    @GET("profile")
    fun getProfile(@Header("Authorization") authHeader: String,
                   @Header("Accept") accept: String) : Flowable<ProfileResponse>

    @POST("profile-update")
    @FormUrlEncoded
    fun updateProfile(@Header("Authorization") authHeader: String,
                      @Header("Accept") accept: String,
                      @Field("name") name: String,
                      @Field("email") email: String,
                      @Field("phone_number") phone: String,
                      @Field("province_id") province: String,
                      @Field("city_id") city: String): Call<ProfileResponse>

    @POST("profile-update")
    @FormUrlEncoded
    fun updatePassword(@Header("Authorization") authHeader: String,
                       @Header("Accept") accept: String,
                       @Field("name") name: String,
                       @Field("email") email: String,
                       @Field("phone_number") phone: String,
                       @Field("province_id") province: String,
                       @Field("city_id") city: String,
                       @Field("password") password: String,
                       @Field("password_confirmation") c_password: String): Call<ProfileResponse>

    @POST("forgot-password")
    @FormUrlEncoded
    fun forgotPassword(@Field("email") email: String): Call<LoginResponse>

    @POST("wallet/topup")
    @FormUrlEncoded
    fun topUpMoney(@Header("Authorization") authHeader: String,
                   @Header("Accept") accept: String,
                   @Field("amount") amount: String): Call<TopUpResponse>

    @GET("wallet/topup")
    fun getTopUpHistory(@Header("Authorization") authHeader: String,
                        @Header("Accept") accept: String,
                        @Query("page") page: Int): Flowable<HistoriTopUpResponse>

    @GET("wallet")
    fun getTransactionHistory(@Header("Authorization") authHeader: String,
                              @Header("Accept") accept: String,
                              @Query("page") page: Int) : Flowable<TransactionResponse>

    @GET("wallet")
    fun getTransactionBalance(@Header("Authorization") authHeader: String,
                              @Header("Accept") accept: String) : Flowable<TransactionResponse>

    @GET("products/search")
    fun searchProuctByName(@Header("Authorization") authHeader: String,
                           @Header("Accept") accept: String,
                           @Query("name") name: String): Flowable<SearchByNameResponse>

    @GET("products/search")
    fun searchProuctByPage(@Header("Authorization") authHeader: String,
                           @Header("Accept") accept: String,
                           @Query("page") page: Int): Flowable<SearchByNameResponse>

    @POST("contact-us")
    @FormUrlEncoded
    fun contactUsRequesr(@Header("Authorization") authHeader: String,
                         @Header("Accept") accept: String,
                         @Field("title") title: String,
                         @Field("content") content: String): Call<ContactUsResponse>

    @GET("products/landingpage")
    fun getOrderWithSlider(@Header("Authorization") authHeader: String) : Flowable<OrderLandingPageResponse>

    @GET("products/detail/{hashed_id}")
    fun getDetailProduct(@Header("Authorization") authHeader: String,
                         @Header("Accept") accept: String,
                         @Path("hashed_id") hashed_id: String) : Flowable<DetailProductResponse>

    @GET("products/catalogs")
    fun getKatalogDesain(@Header("Authorization") authHeader: String,
                         @Header("Accept") accept: String) : Flowable<KatalogDesainResponse>

    @POST("cart/add")
    @Multipart
    fun addToCart(@Header("Authorization") authHeader: String,
                  @Header("Accept") accept: String,
                  @PartMap cartMap: HashMap<String, RequestBody>,
                  @Part file: MutableList<MultipartBody.Part>,
                  @Part fileDoc: MultipartBody.Part?,
                  @Part("price") price: RequestBody?): Call<AddToCartResponse>

    @GET("cart")
    fun getCartData(@Header("Authorization") authHeader: String,
                    @Header("Accept") accept: String) : Flowable<CartResponse>

    @POST("cart/coupon-check")
    @FormUrlEncoded
    fun checkKupon(@Header("Authorization") authHeader: String,
                   @Header("Accept") accept: String,
                   @Field("code") code: String): Call<CheckKuponResponse>

    @GET("cart/delete/{hashed_id}")
    fun getCartDalete(@Header("Authorization") authHeader: String,
                      @Header("Accept") accept: String,
                      @Path("hashed_id") hashed_id: String) : Flowable<DeleteCartResponse>

    @POST("order/checkout")
    @FormUrlEncoded
    fun checkout(@Header("Authorization") authHeader: String,
                 @Header("Accept") accept: String,
                 @Field("payment_type") paymentType: String,
                 @Field("voucher") voucher: String): Call<CheckoutResponse>

    @GET("order")
    fun getOrderHistori(@Header("Authorization") authHeader: String,
                        @Header("Accept") accept: String,
                        @Query("page") page: Int) : Flowable<HistoriOrderResponse>

    @GET("order/{hashed_id}")
    fun getOrderDetail(@Header("Authorization") authHeader: String,
                       @Header("Accept") accept: String,
                       @Path("hashed_id") hashed_id: String) : Flowable<DetailOrderResponse>

    @POST("fcm-token")
    @FormUrlEncoded
    fun fcmRequest(@Header("Authorization") authHeader: String,
                   @Header("Accept") accept: String,
                   @Field("fcm_token") token: String): Call<FCMResponse>

    @GET("order/{hashed_id}/chat")
    fun getChatHistori(@Header("Authorization") authHeader: String,
                       @Header("Accept") accept: String,
                       @Path("hashed_id") hashed_id: String,
                       @Query("page") page: Int) : Flowable<ChatHistoriResponse>

    @POST("order/{hashed_id}/chat")
    @Multipart
    fun chatRequest(@Header("Authorization") authHeader: String,
                    @Header("Accept") accept: String,
                    @Path("hashed_id") hashed_id: String,
                    @Part("text") text: RequestBody,
                    @Part file: MultipartBody.Part?): Call<MessageModel>

    @POST("order/{hashed_id}/review")
    @FormUrlEncoded
    fun sendReview(@Header("Authorization") authHeader: String,
                   @Header("Accept") accept: String,
                   @Path("hashed_id") hashed_id: String,
                   @Field("star_rating") star: String,
                   @Field("designer_testimonial") designer_testi: String,
                   @Field("app_testimonial") app_testi: String,
                   @Field("tip") tip: String): Call<DetailOrderResponse>

    @GET("dashboard-slider")
    fun getDashboardSlider(@Header("Authorization") authHeader: String) : Flowable<DashboardResponse>

    @GET("customer-announcement")
    fun announcementDesigner(@Header("Authorization") authHeader: String) : Flowable<AnnouncementResponse>
}