package com.manddprojectconsulant.stegapics.retrofit_setup

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @POST("v1/customers")
    fun getCustomerId(@HeaderMap headerMap: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("v1/ephemeral_keys")
    fun getEphemeralKey(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap stringMap: Map<String, String>
    ): Call<ResponseBody>
    @FormUrlEncoded
    @POST("v1/payment_intents")
    fun getClientSecret(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap stringMap: Map<String, String>
    ): Call<ResponseBody>
}