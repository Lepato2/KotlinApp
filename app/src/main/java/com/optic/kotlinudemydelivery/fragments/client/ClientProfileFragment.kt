package com.optic.kotlinudemydelivery.fragments.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.optic.kotlinudemydelivery.R
import com.optic.kotlinudemydelivery.activities.MainActivity
import com.optic.kotlinudemydelivery.activities.SelectRolesActivity
import com.optic.kotlinudemydelivery.activities.client.update.ClientUpdateActivity
import com.optic.kotlinudemydelivery.models.User
import com.optic.kotlinudemydelivery.utils.SharedPref
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.log


class ClientProfileFragment : Fragment() {
//instanciamos un boton en un FRAGMENT
    var myView:View? = null
    var buttonSelectRol: Button? = null
    var buttonUpdateProfile: Button? = null
    var circleImageUser: CircleImageView? = null
    var textViewName: TextView? = null
    var textViewEmail: TextView? = null
    var textViewPhone: TextView? = null
    var imageViewLogout: ImageView? = null

    var sharedPref: SharedPref ? = null
    var user : User ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView= inflater.inflate(R.layout.fragment_client_profile, container, false)
        //Uso el findViewById para enlazar un recurso de la interfaz de usuario

        sharedPref = SharedPref(requireActivity())

        buttonSelectRol = myView?.findViewById(R.id.btn_select_rol)
        buttonUpdateProfile = myView?.findViewById(R.id.btn_update_profile)
        textViewName = myView?.findViewById(R.id.textview_name)
        textViewEmail = myView?.findViewById(R.id.textview_email)
        textViewPhone = myView?.findViewById(R.id.textview_phone)
        circleImageUser= myView?.findViewById(R.id.circleimage_user)
        imageViewLogout= myView?.findViewById(R.id.imageview_logout)

        buttonSelectRol?.setOnClickListener { goToSelectRol() }
        imageViewLogout?.setOnClickListener { logout() }
        buttonUpdateProfile?.setOnClickListener { goToUpdated() }

        getUserFromSession()

        textViewName?.text ="${user?.name} ${user?.lastname}"
        textViewEmail?.text = user?.email
        textViewPhone?.text =user?.phone

        if(!user?.image.isNullOrBlank()) {
            Glide.with(requireContext()).load(user?.image).into(circleImageUser!!)
        }

        return myView
    }

    private fun logout(){
        sharedPref?.remove("user")
        val i = Intent(requireContext(), MainActivity::class.java)
        startActivity(i)
    }

    private fun getUserFromSession(){
        val gson = Gson()

        //Si el usuario existe en sesion
        if (!sharedPref?.getData("user").isNullOrBlank()){
            //SI EL USUARIO INICIA SESION
             user = gson.fromJson(sharedPref?.getData("user"), User::class.java)

        }
    }

    private fun goToUpdated() {
        val i = Intent(requireContext(), ClientUpdateActivity::class.java)
        startActivity(i)
    }

    private fun goToSelectRol(){
        val i = Intent(requireContext(), SelectRolesActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK//Eliminar el historial de pantallas
        startActivity(i)
    }
}