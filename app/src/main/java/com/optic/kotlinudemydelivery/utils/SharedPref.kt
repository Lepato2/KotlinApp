package com.optic.kotlinudemydelivery.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
class SharedPref(activity:Activity) {


    private var prefs: SharedPreferences?=null
    /*Aca usamos un private var para almacenar la sesion en el dipositivo*/
    init {
        prefs = activity.getSharedPreferences("com.optic.kotlinudemydelivery", Context.MODE_PRIVATE)
    }

    fun save(key: String, objeto: Any){

        try {

            val gson = Gson()
            val json = gson.toJson(objeto)
            with(prefs?.edit()){
                this?.putString(key,json)
                this?.commit()
            }


        }catch (e: Exception){
            Log.d("ERROR","Err ${e.message}")
        }
    }

//Creo un metodo que nos permita obtener esta data
    fun getData(key: String): String?{
        val data = prefs?.getString(key,"")
        return data
    }

    fun remove(key: String){
        prefs?.edit()?.remove(key)?.apply()
    }

}

//SharedPref apunta a un archivo que contiene pares clave-valor y proporciona métodos sencillos para leerlos y escribirlos