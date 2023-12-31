package com.optic.kotlinudemydelivery.api

import com.optic.kotlinudemydelivery.routes.CategoriesRoutes
import com.optic.kotlinudemydelivery.routes.ProductsRoutes
import com.optic.kotlinudemydelivery.routes.UsersRoutes
import retrofit2.create

class ApiRoutes {

    val API_URL = "http://192.168.18.108:3000/api/"
    val retrofit = RetrofitClient()

    fun getUsersRoutes(): UsersRoutes{
        return retrofit.gerClient(API_URL).create(UsersRoutes::class.java)
    }

    fun getUsersRoutesWithToken(token:String): UsersRoutes{
        return retrofit.getClientWithToken(API_URL,token).create(UsersRoutes::class.java)
    }

    fun getCategoriesRoutes(token:String): CategoriesRoutes {
        return retrofit.getClientWithToken(API_URL,token).create(CategoriesRoutes::class.java)
    }

    fun getProductsRoutes(token:String): ProductsRoutes {
        return retrofit.getClientWithToken(API_URL,token).create(ProductsRoutes::class.java)
    }

}