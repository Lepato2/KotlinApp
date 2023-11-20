package com.optic.kotlinudemydelivery.activities.client.update

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.optic.kotlinudemydelivery.R
import com.optic.kotlinudemydelivery.models.ResponseHttp
import com.optic.kotlinudemydelivery.models.User
import com.optic.kotlinudemydelivery.providers.UsersProvider
import com.optic.kotlinudemydelivery.utils.SharedPref
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ClientUpdateActivity : AppCompatActivity() {

    val TAG = "ClientUpdateActivity"
    var circleImageUser: CircleImageView? = null
    var editTextName: EditText? = null
    var editTextLastName: EditText? = null
    var editTextPhone: EditText? = null
    var buttonUpdate: Button? = null

    var sharedPref: SharedPref? = null
    var user: User? = null

    private var imageFile: File?=null
    var usersProvider : UsersProvider? = null

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_update)

        sharedPref = SharedPref(this)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.title = "Editar perfil"
        toolbar?.setTitleTextColor(ContextCompat.getColor(this,R.color.white))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        circleImageUser = findViewById(R.id.circleimage_user)
        editTextName = findViewById(R.id.edittext_name)
        editTextLastName = findViewById(R.id.edittext_lastname)
        editTextPhone = findViewById(R.id.edittext_phone)
        buttonUpdate = findViewById(R.id.btn_update)

        getUserFromSession()
        usersProvider = UsersProvider(user?.sessionToken)

        editTextName?.setText(user?.name)
        editTextLastName?.setText(user?.lastname)
        editTextPhone?.setText(user?.phone)

        if(!user?.image.isNullOrBlank()) {
            Glide.with(this).load(user?.image).into(circleImageUser!!)
        }

        circleImageUser?.setOnClickListener { selectImage() }
        buttonUpdate?.setOnClickListener { updateData() }

    }

    private fun updateData() {
        val name = editTextName?.text.toString()
        val lastname = editTextLastName?.text.toString()
        val phone = editTextPhone?.text.toString()

        user?.name = name
        user?.lastname = lastname
        user?.phone = phone

        if (imageFile != null) {
            usersProvider?.update(imageFile!!, user!!)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    Log.d(TAG, "RESPONSE: $response")
                    Log.d(TAG, "BODY: ${response.body()}")

                    val message = response.body()?.message // Obtén el mensaje de la respuesta

                    if (message != null) {
                        Toast.makeText(this@ClientUpdateActivity, message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ClientUpdateActivity, "Perfil Actualizado", Toast.LENGTH_SHORT).show()
                    }

                    if (response.body()?.isSuccess == true) {
                        saveUserInSession(response.body()?.data.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "ERROR: ${t.message}")
                    Toast.makeText(this@ClientUpdateActivity, "ERROR: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            usersProvider?.updateWithoutImage(user!!)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    Log.d(TAG, "RESPONSE: $response")
                    Log.d(TAG, "BODY: ${response.body()}")

                    val message = response.body()?.message // Obtén el mensaje de la respuesta

                    if (message != null) {
                        Toast.makeText(this@ClientUpdateActivity, message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ClientUpdateActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    }

                    if (response.body()?.isSuccess == true) {
                        saveUserInSession(response.body()?.data.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "ERROR: ${t.message}")
                    Toast.makeText(this@ClientUpdateActivity, "ERROR: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })

        }


    }

    private fun saveUserInSession(data:String){
        val gson= Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref?.save("user" , user)

    }

    private fun getUserFromSession(){
        val gson = Gson()

        //Si el usuario existe en sesion
        if (!sharedPref?.getData("user").isNullOrBlank()){
            //SI EL USUARIO INICIA SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)

        }
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
                circleImageUser?.setImageURI(fileUri)
            }
            else if (resultCode== ImagePicker.RESULT_ERROR){
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this, "Se cancelo la accion", Toast.LENGTH_LONG).show()
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