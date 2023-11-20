package com.optic.kotlinudemydelivery.activities.client.products.list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.optic.kotlinudemydelivery.R
import com.optic.kotlinudemydelivery.adapters.ProductsAdapter
import com.optic.kotlinudemydelivery.models.Product
import com.optic.kotlinudemydelivery.models.User
import com.optic.kotlinudemydelivery.providers.ProductsProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientProductsListActivity : AppCompatActivity() {

    val TAG = "ClientProducts"
    var recyclerViewProducts : RecyclerView? = null
    var adapter : ProductsAdapter? = null
    var user : User? = null
    var productsProvider : ProductsProvider? = null
    var products: ArrayList<Product> = ArrayList()

    var idCategory :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_products_list)

        idCategory = intent.getStringExtra("idCategory")

        recyclerViewProducts = findViewById(R.id.recyclerview_products)
        //Muestro en cuenatas columnas quiero ver los productos
        recyclerViewProducts?.layoutManager = GridLayoutManager(this,2)
        getProduct()
    }

    private fun getProduct() {
        productsProvider?.findByCategory(idCategory!!)?.enqueue(object :Callback<ArrayList<Product>> {
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                if (response.body() != null) {
                    products = response.body()!!
                    adapter = ProductsAdapter(this@ClientProductsListActivity, products)
                    recyclerViewProducts?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                Toast.makeText(this@ClientProductsListActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.d(TAG,"Error: ${t.message}")
            }

        })
    }
}