package com.optic.kotlinudemydelivery.models

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class ResponseHttp(
    @SerializedName("message") val message: String,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: JsonObject, // int , double , boolean
    @SerializedName("error") val error: String,
) {
    //Esto se crea con un "Alt+Insert y selecciono ToString()"
    override fun toString(): String {
        return "ResponseHttp(message='$message', isSuccess=$isSuccess, data=$data, error='$error')"
    }
}
//MIN 19:03