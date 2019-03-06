package com.hyperdev.tungguin.network

import com.hyperdev.tungguin.model.login.LoginResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.model.register.CityResponse
import com.hyperdev.tungguin.model.register.ProvinceResponse
import com.hyperdev.tungguin.model.register.RegisterResponse
import com.hyperdev.tungguin.model.topup.TopUpResponse
import com.hyperdev.tungguin.model.topuphistori.HistoriTopUpResponse
import com.hyperdev.tungguin.model.transactionhistory.TransactionResponse
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.GET

//Berisi Perintah Untuk Berkomunikasi Dengan API
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
    fun getProfile(@Header("Authorization") authHeader: String) : Flowable<ProfileResponse>

    @POST("profile-update")
    @FormUrlEncoded
    fun updateProfile(@Header("Authorization") authHeader: String,
                      @Field("name") name: String,
                      @Field("email") email: String,
                      @Field("phone_number") phone: String,
                      @Field("province_id") province: String,
                      @Field("city_id") city: String): Call<ProfileResponse>

    @POST("profile-update")
    @FormUrlEncoded
    fun updatePassword(@Header("Authorization") authHeader: String,
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
                   @Field("amount") amount: String): Call<TopUpResponse>

    @GET("wallet/topup")
    fun getTopUpHistory(@Header("Authorization") authHeader: String,
                        @Query("page") page: Int): Flowable<HistoriTopUpResponse>

    @GET("wallet")
    fun getTransactionHistory(@Header("Authorization") authHeader: String,
                              @Query("page") page: Int) : Flowable<TransactionResponse>

    @GET("wallet")
    fun getTransactionBalance(@Header("Authorization") authHeader: String) : Flowable<TransactionResponse>
}