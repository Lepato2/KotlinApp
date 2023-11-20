package com.optic.kotlinudemydelivery.activities

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.gson.Gson
import com.optic.kotlinudemydelivery.R
import com.optic.kotlinudemydelivery.activities.client.home.ClientHomeActivity
import com.optic.kotlinudemydelivery.activities.delivery.home.DeliveryHomeActivity
import com.optic.kotlinudemydelivery.activities.pedidos.home.PedidosHomeActivity
import com.optic.kotlinudemydelivery.models.ResponseHttp
import com.optic.kotlinudemydelivery.models.User
import com.optic.kotlinudemydelivery.providers.UsersProvider
import com.optic.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
/*Var es para definir una constante*/
    /* ImageView viene del paquete de android*/
    var imageViewGoToRegister: ImageView? = null
    var editTextEmail:EditText? = null
    var editTextPassword:EditText? = null
    var buttonLogin:Button? = null
    var usersProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //R.layout accede a estos identificadores para referenciar los archivos de diseño y crear la interfaz de usuario correspondiente en función de su contenido
        setContentView(R.layout.activity_main)
    //findViweById enlaza un recurso de la interfaz de usuario de una aplicación, con una variable en nuestro código
        imageViewGoToRegister=findViewById(R.id.imageview_go_to_register)
        editTextEmail = findViewById(R.id.edittext_email)
        editTextPassword = findViewById(R.id.edittext_password)
        buttonLogin = findViewById(R.id.btn_login)

        imageViewGoToRegister?.setOnClickListener { goToRegister()}
        //Usamos el ? para indicar si es null esto no se ejecutara El goToRegister se utiliza para especificar
        // el código que se debe ejecutar cuando el evento de clic ocurre en la vista

       buttonLogin?.setOnClickListener { login() }

        getUserFromSession()
        }

    private fun login(){

        val email = editTextEmail?.text.toString()
        val password = editTextPassword?.text.toString()

        if(isValidFrom(email,password)){

            usersProvider.login(email,password)?.enqueue(object:Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    Log.d("MainActivity","Response :${response.body()}")

                    if (response.body()?.isSuccess == true){
                        Toast.makeText(this@MainActivity,response.body()?.message, Toast.LENGTH_LONG).show()

                        saveUserInSession(response.body()?.data.toString())
                        // llamo al private fun Cuando el usuario se haya logeado

                    }
                    else{
                        Toast.makeText(this@MainActivity,"Los datos no son correctos", Toast.LENGTH_LONG).show()

                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d("MainActivity","Hubo u error ${t.message}")

                    Toast.makeText(this@MainActivity, "Hubo u error ${t.message}", Toast.LENGTH_LONG).show()

                }

            })


        }
        else{
            Toast.makeText(this, "No es valido", Toast.LENGTH_LONG).show()
        }

        //El simbolo de $ se usa para insertar el valor de una variable

        //para obtener una lista más completa de opciones
        //se utiliza para imprimir mensajes de depuración en la consola de registro (log) mientras la aplicación se está ejecutando
      //  Log.d("MainActivity","El password es :$password")

    }

    //Creo un Metodo para que nos envie a la pantalla , en este caso seria a la del  Cliente
    //Con un "private fun"
    private fun goToClientHome(){
        val i =Intent(this, ClientHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK//Eliminar el historial de pantallas
        startActivity(i)
    }
//MANDAMOS AL USUARIO AL PEDIDOS
    private fun goToPedidosHome(){
        val i =Intent(this, PedidosHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK//Eliminar el historial de pantallas
        startActivity(i)
//MANDAMOS AL USUARIO QUE VAYA AL DELIVERY
    }private fun goToDeliveryHome(){
        val i =Intent(this, DeliveryHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK//Eliminar el historial de pantallas
        startActivity(i)
    }
//MANDAMOS AL USUARIO A QUE SELECCIONE SU ROL
    private fun goToSelectRol(){
        val i =Intent(this, SelectRolesActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK//Eliminar el historial de pantallas
        startActivity(i)
    }

    private fun saveUserInSession(data:String){

        val sharedPref = SharedPref(this)
        val gson= Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user" , user)

        if(user.roles?.size!! > 1){//TIENE MAS DE UN ROL
            goToSelectRol()
        }
        else{// SOLO TIENE UN ROL
            goToClientHome()
        }




        
    }


    fun String.isEmailValid(): Boolean{

        return  !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
    private fun getUserFromSession(){

        val sharedPref = SharedPref(this)
        val gson = Gson()

        //Si el usuario existe en sesion
        if (!sharedPref.getData("user").isNullOrBlank()){
            //SI EL   USUARIO EXISTE EN SESION
            val user = gson.fromJson(sharedPref.getData("user"), User::class.java)

            if(!sharedPref.getData("rol").isNullOrBlank()){
                // SI EL USUARIO SELECCION O EL ROL
                val rol = sharedPref.getData("rol")?.replace("\"","")

                if(rol == "EMPRESA VORS") {
                goToPedidosHome()
                }
                else if(rol == "CLIENTE") {
                    goToClientHome()
                }
                else if (rol == "EMPLEADO VORS") {
                    goToDeliveryHome()
                }
            }
            else{
                Log.d("MainActivity","ROL NO EXISTE")
                goToClientHome()
            }



        }
    }

    private fun isValidFrom(email:String, password:String): Boolean{
         if(email.isBlank()){
             return false
         }
        if(password.isBlank()){
            return false
        }
        //"!" permite verificar si el correo electrónico NO es válido, y si eso es cierto, el código dentro del if se ejecutará
        if(!email.isEmailValid()){
            return false
        }
        return true
    }

    private fun goToRegister(){
        val i = Intent (this, RegisterActivity::class.java)
        /*representa una única pantalla en una aplicación
        El código  crea un intento para abrir la actividad RegisterActivity
        desde la actividad actual y luego puedes usar ese intento para
        realmente iniciar la nueva actividad y mostrar su contenido en la pantalla*/
        startActivity(i)
    }

}
