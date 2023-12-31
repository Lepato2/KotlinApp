package com.optic.kotlinudemydelivery.fragments.empresa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.optic.kotlinudemydelivery.R
import com.optic.kotlinudemydelivery.models.Category
import com.optic.kotlinudemydelivery.models.Product
import com.optic.kotlinudemydelivery.models.ResponseHttp
import com.optic.kotlinudemydelivery.models.User
import com.optic.kotlinudemydelivery.providers.CategoriesProvider
import com.optic.kotlinudemydelivery.providers.ProductsProvider
import com.optic.kotlinudemydelivery.utils.SharedPref
import com.tommasoberlose.progressdialog.ProgressDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class EmpresaProductFragment : Fragment() {

    val TAG = "ProductFragment"
    var myView: View? = null
    var editTextName: EditText? = null
    var editTextDescription: EditText? = null
    var editTextPrice: EditText? = null
    var imageViewProduct1: ImageView? = null
    var imageViewProduct2: ImageView? = null
    var imageViewProduct3: ImageView? = null
    var buttonCreate: Button? = null
    var spinnerCategories: Spinner? = null


    var imageFile1 :File? = null
    var imageFile2 :File? = null
    var imageFile3 :File? = null

    var categoriesProvider: CategoriesProvider? = null
    var productsProvider: ProductsProvider? = null
    var user: User? = null
    var sharedPref : SharedPref? = null
    var categories = ArrayList<Category>()
    var idCategory = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_empresa_product, container, false)

        editTextName = myView?.findViewById(R.id.edittext_name)
        editTextDescription = myView?.findViewById(R.id.edittext_description)
        editTextPrice = myView?.findViewById(R.id.edittext_price)
        imageViewProduct1 = myView?.findViewById(R.id.imageview_1)
        imageViewProduct2 = myView?.findViewById(R.id.imageview_2)
        imageViewProduct3 = myView?.findViewById(R.id.imageview_3)
        buttonCreate = myView?.findViewById(R.id.btn_create)
        spinnerCategories = myView?.findViewById(R.id.spinner_categories)


        buttonCreate?.setOnClickListener { createProduct() }
        imageViewProduct1?.setOnClickListener { selectImage(101) }
        imageViewProduct2?.setOnClickListener { selectImage(102) }
        imageViewProduct3?.setOnClickListener { selectImage(103) }

        sharedPref = SharedPref(requireActivity())

        getUserFromSession()

        categoriesProvider = CategoriesProvider(user?.sessionToken!!)
        productsProvider = ProductsProvider(user?.sessionToken!!)

        getCategories()

        return  myView
    }

    private fun getCategories() {
        categoriesProvider?.getAll()?.enqueue(object: Callback<ArrayList<Category>> {
            override fun onResponse(call: Call<ArrayList<Category>>, response: Response<ArrayList<Category>>
            ) {
                if(response.body() !=null){

                    categories = response.body()!!

                    val arrayAdapter = ArrayAdapter<Category>(requireActivity(),android.R.layout.simple_dropdown_item_1line,categories)
                    spinnerCategories?.adapter = arrayAdapter
                    spinnerCategories?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, posotion: Int, l: Long) {
                            idCategory = categories[posotion].id!!
                            Log.d(TAG,"Id category:$idCategory")
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }
                }

            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                Log.d(TAG,"Error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getUserFromSession(){
        val gson = Gson()

        //Si el usuario existe en sesion
        if (!sharedPref?.getData("user").isNullOrBlank()){
            //SI EL USUARIO INICIA SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)

        }
    }

    private fun createProduct() {
        val name = editTextName?.text.toString()
        val description = editTextDescription?.text.toString()
        val priceText = editTextPrice?.text.toString()

        val files = ArrayList<File>()

        if (isValidFrom(name,description,priceText)) {

            val product = Product(
                name= name,
                description = description,
                price = priceText.toDouble(),
                idCategory =idCategory
            )

            files.add(imageFile1!!)
            files.add(imageFile2!!)
            files.add(imageFile3!!)

            ProgressDialogFragment.showProgressBar(requireActivity())

            productsProvider?.create(files, product)?.enqueue(object :Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    ProgressDialogFragment.hideProgressBar(requireActivity())

                    if (response.body()?.message != null) {
                        Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Mensaje no disponible", Toast.LENGTH_SHORT).show()
                    }
                    if (response.body()?.isSuccess == true) {
                        resetFrom()
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    ProgressDialogFragment.hideProgressBar(requireActivity())
                    Log.d(TAG,"Error: ${t.message}")
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })

        }

    }
    private  fun resetFrom() {
        editTextName?.setText("")
        editTextDescription?.setText("")
        editTextPrice?.setText("")
        imageFile1 = null
        imageFile2= null
        imageFile3 = null
        imageViewProduct1?.setImageResource(R.drawable.ic_image)
        imageViewProduct2?.setImageResource(R.drawable.ic_image)
        imageViewProduct3?.setImageResource(R.drawable.ic_image)
    }

    private fun isValidFrom(name :String, description:String, price:String):Boolean {

        if ( name.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Ingresa el nombre del Producto", Toast.LENGTH_SHORT).show()
            return false
        }
        if ( description.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Ingresa la descripcion del Producto", Toast.LENGTH_SHORT).show()
            return false
        }
        if ( price.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Ingresa el precio del Producto", Toast.LENGTH_SHORT).show()
            return false
        }

        if (imageFile1 == null) {
            Toast.makeText(requireContext(), "Selecciona la imagen 1", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile2 == null) {
            Toast.makeText(requireContext(), "Selecciona la imagen 2", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile3 == null) {
            Toast.makeText(requireContext(), "Selecciona la imagen 3", Toast.LENGTH_SHORT).show()
            return false
        }
        if(idCategory.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Selecciona la categoria del producto", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val fileUri = data?.data
            if (requestCode == 101) {
                imageFile1 = File (fileUri?.path) //EL ARCHIVO QUE VAMOS A GUARDAR COMO IMAGEN EN EL SERVIDOR
                imageViewProduct1?.setImageURI(fileUri)
            }
            else if (requestCode == 102) {
                imageFile2 = File (fileUri?.path) //EL ARCHIVO QUE VAMOS A GUARDAR COMO IMAGEN EN EL SERVIDOR
                imageViewProduct2?.setImageURI(fileUri)
            }
            else if (requestCode == 103) {
                imageFile3 = File (fileUri?.path) //EL ARCHIVO QUE VAMOS A GUARDAR COMO IMAGEN EN EL SERVIDOR
                imageViewProduct3?.setImageURI(fileUri)
            }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }




    //creo un metodo que permita seleccionar una imagen de galeria o Tomar una fotografia
    private fun selectImage(requestCode: Int) {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080,1080)
            .start(requestCode)

    }
}