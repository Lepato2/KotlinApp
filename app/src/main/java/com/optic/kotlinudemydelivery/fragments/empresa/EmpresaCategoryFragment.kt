package com.optic.kotlinudemydelivery.fragments.empresa

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.optic.kotlinudemydelivery.R
import com.optic.kotlinudemydelivery.models.Category
import com.optic.kotlinudemydelivery.models.ResponseHttp
import com.optic.kotlinudemydelivery.models.User
import com.optic.kotlinudemydelivery.providers.CategoriesProvider
import com.optic.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class EmpresaCategoryFragment : Fragment() {

    val TAG = "EmpresaCategoryFragment"
    var myView: View? = null
    var imageViewCategory : ImageView ? = null
    var editTextCategory: EditText?= null
    var buttonCreate: Button? = null

    private var imageFile: File?=null

    var categoriesProvider : CategoriesProvider? = null
    var sharedPref : SharedPref? =null
    var user:User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_empresa_category, container, false)

        sharedPref = SharedPref(requireActivity())

        imageViewCategory = myView?.findViewById(R.id.imageview_category)
        editTextCategory = myView?.findViewById(R.id.edittext_category)
        buttonCreate = myView?.findViewById(R.id.btn_create)

        imageViewCategory?.setOnClickListener { selectImage() }
        buttonCreate?.setOnClickListener { createCategory() }

        getUserFromSession()
        categoriesProvider = CategoriesProvider(user?.sessionToken!!)

        return myView
    }

    private fun getUserFromSession(){
        val gson = Gson()

        //Si el usuario existe en sesion
        if (!sharedPref?.getData("user").isNullOrBlank()){
            //SI EL USUARIO INICIA SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)

        }
    }

    private fun  createCategory() {
        val name = editTextCategory?.text.toString()

        if (imageFile != null) {

            val category = Category(name = name)

            categoriesProvider?.create(imageFile!!, category)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    Log.d(TAG, "RESPONSE: $response")
                    Log.d(TAG, "BODY: ${response.body()}")


                    val message = response.body()?.message // Obtén el mensaje de la respuesta

                    if (message != null) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Categoria Creada", Toast.LENGTH_LONG).show()
                    }
                    if (response.body()?.isSuccess == true) {
                        clearFrom()
                    }
                }
                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "ERROR: ${t.message}")
                    Toast.makeText(requireContext(), "ERROR: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })

        }
        else {
            Toast.makeText(requireContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFrom() {
        editTextCategory?.setText("")
        imageFile = null
        imageViewCategory?.setImageResource(R.drawable.ic_image)
    }

    private val startImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

            val resultCode = result.resultCode
            //capturammos del dato que el usuario selecciono
            val data = result.data

            //si el usuario selecciono una imagen corretamente creamos este archivo
            if(resultCode == Activity.RESULT_OK){
                //El archivo creado es val fileUri
                val fileUri = data?.data
                imageFile = File(fileUri?.path)//El archivo que vamos a guardar como imagen en el servidor
                imageViewCategory?.setImageURI(fileUri)
            }
            else if (resultCode== ImagePicker.RESULT_ERROR){
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(requireContext(), "Se cancelo la accion", Toast.LENGTH_LONG).show()
            }
        }

    //creo un metodo que permita seleccionar una imagen de galeria o Tomar una fotografia
    private fun selectImage(){
        //Llamo a la clase "ImagePicker"
        ImagePicker.with(this)
            //.crop Permite al usuario cortar la imagen
            .crop()
            //.compress Permite comprimir la imagen , de recomendacion es poner 1024
            .compress(1024)
            //.maxResultsize Es el macimo permitido para las imagenes esto es en pixeles
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startImageForResult.launch(intent)
            }
    }

}