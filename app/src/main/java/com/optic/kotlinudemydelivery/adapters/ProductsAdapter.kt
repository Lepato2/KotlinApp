package com.optic.kotlinudemydelivery.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.optic.kotlinudemydelivery.R
import com.optic.kotlinudemydelivery.activities.client.home.ClientHomeActivity
import com.optic.kotlinudemydelivery.activities.client.products.detail.ClientProductsDetailActivity
import com.optic.kotlinudemydelivery.activities.client.products.list.ClientProductsListActivity
import com.optic.kotlinudemydelivery.activities.delivery.home.DeliveryHomeActivity
import com.optic.kotlinudemydelivery.activities.pedidos.home.PedidosHomeActivity
import com.optic.kotlinudemydelivery.models.Category
import com.optic.kotlinudemydelivery.models.Product
import com.optic.kotlinudemydelivery.models.Rol
import com.optic.kotlinudemydelivery.utils.SharedPref

class ProductsAdapter(val context:Activity, val products: ArrayList<Product>): RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    val sharedPref = SharedPref(context)



//override fun facilita que se muestren de manera eficiente grandes conjuntos de datos
    override fun onCreateViewHolder(parent: ViewGroup,viewType:Int): ProductsViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_product,parent,false)
        return ProductsViewHolder(view)

    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder:ProductsViewHolder,position:Int){

        val product = products[position]// CAda una de las categorias

        holder.textViewName.text = product.name
        holder.textViewPrice.text = "${product.price}$"
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)

        holder.itemView.setOnClickListener{goToDetail(product)}

    }
    private fun goToDetail(product:Product){
            val i = Intent(context, ClientProductsDetailActivity::class.java)
            context.startActivity(i)
    }



    class ProductsViewHolder(view:View): RecyclerView.ViewHolder(view){

        val textViewName:TextView
        val textViewPrice: TextView
        val imageViewProduct:ImageView

        init {
            textViewName = view.findViewById(R.id.textview_name)
            textViewPrice = view.findViewById(R.id.textview_price)
            imageViewProduct = view.findViewById(R.id.imageview_product)

        }
    }
}