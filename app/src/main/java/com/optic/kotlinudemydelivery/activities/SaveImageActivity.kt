package com.optic.kotlinudemydelivery.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.optic.kotlinudemydelivery.R
import com.optic.kotlinudemydelivery.activities.client.home.ClientHomeActivity
import com.optic.kotlinudemydelivery.models.ResponseHttp
import com.optic.kotlinudemydelivery.models.User
import com.optic.kotlinudemydelivery.providers.UsersProvider
import com.optic.kotlinudemydelivery.utils.SharedPref
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SaveImageActivity : AppCompatActivity() {
//Instanciamos las vistas
// Creamos las variables:

    val TAG = "SaveImageActivity"
    var circleImageUser: CircleImageView? = null
    var buttonNext: Button? = null
    var buttonConfirm: Button? = null

    private var imageFile: File?=null

//Creo variables para asignar los valores
    var usersProvider : UsersProvider?=null
    var user: User? = null
    var sharedPref: SharedPref? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_image)


        sharedPref = SharedPref(this)

        getUserFromSession()

        usersProvider = UsersProvider(user?.sessionToken)

        circleImageUser = findViewById(R.id.circleimage_user)
        buttonNext = findViewById(R.id.btn_next)
        buttonConfirm = findViewById(R.id.btn_confirm)

        circleImageUser?.setOnClickListener{selectImage() }

//mandamos al usuario al inicio si da a saltar este paso para esoo llamamo a la funcion
        buttonNext?.setOnClickListener { goToClientHome() }
        buttonConfirm?.setOnClickListener { saveImage() }
    }

    private fun saveImage(){

        if(imageFile != null && user != null){
            usersProvider?.update(imageFile!!,user!!)?.enqueue(object :Callback<ResponseHttp>{
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    Log.d(TAG, "RESPONSE: $response")
                    Log.d(TAG, "BODY: ${response.body()}")

                    saveUserInSession(response.body()?.data.toString())
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "ERROR: ${t.message}")
                    Toast.makeText(this@SaveImageActivity, "ERROR: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }
        else{
            Toast.makeText(this, "La imagen no puede ser nula ni tampoco de sesion del usuario", Toast.LENGTH_LONG).show()
        }

    }

    private fun saveUserInSession(data:String){
        val gson= Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref?.save("user" , user)
            goToClientHome()
        }

    private fun goToClientHome(){
        val i = Intent(this, ClientHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK//Eliminar el historial de pantallas
        startActivity(i)
    }


    private fun getUserFromSession(){
        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()){

            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)

        }
    }

//Creamos un valor que este esperando un resultado
    private val startImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result:ActivityResult ->

            val resultCode = result.resultCode
            //capturammos del dato que el usuario selecciono
            val data = result.data

            //si el usuario selecciono una imagen corretamente creamos este archivo
            if(resultCode ==Activity.RESULT_OK){
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