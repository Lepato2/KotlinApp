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
import com.optic.kotlinudemydelivery.activities.client.products.list.ClientProductsListActivity
import com.optic.kotlinudemydelivery.activities.delivery.home.DeliveryHomeActivity
import com.optic.kotlinudemydelivery.activities.pedidos.home.PedidosHomeActivity
import com.optic.kotlinudemydelivery.models.Category
import com.optic.kotlinudemydelivery.models.Rol
import com.optic.kotlinudemydelivery.utils.SharedPref

class CategoriesAdapter(val context:Activity, val categories: ArrayList<Category>): RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    val sharedPref = SharedPref(context)



//override fun facilita que se muestren de manera eficiente grandes conjuntos de datos
    override fun onCreateViewHolder(parent: ViewGroup,viewType:Int): CategoriesViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_categories,parent,false)
        return CategoriesViewHolder(view)

    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder:CategoriesViewHolder,position:Int){

        val category = categories[position]// CAda una de las categorias

        holder.textViewCategory.text = category.name
        Glide.with(context).load(category.image).into(holder.imageViewCategory)

        holder.itemView.setOnClickListener{goToProducts(category)}

    }
    private fun goToProducts(category:Category){
            val i = Intent(context, ClientProductsListActivity::class.java)
        i.putExtra("idCategory", category.id)
          context.startActivity(i)
    }



    class CategoriesViewHolder(view:View): RecyclerView.ViewHolder(view){

        val textViewCategory:TextView
        val imageViewCategory: ImageView

        init {
            textViewCategory = view.findViewById(R.id.textview_category)
            imageViewCategory = view.findViewById(R.id.imageview_category)
        }
    }
}