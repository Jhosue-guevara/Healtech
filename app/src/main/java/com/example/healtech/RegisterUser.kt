package com.example.healtech

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.viewmodel.CreationExtras
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterUser : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var emailR: String
    lateinit var clavee: String
    lateinit var DUI_R : String
    lateinit var nombreR : String
    lateinit var telefonoR : String
    lateinit var direccionR : String
    lateinit var jvpmL:String
    lateinit var intet_Rol: String
    lateinit var datosList: Datos
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        val direccion = findViewById<TextInputEditText>(R.id.editTextDireccion)
        val DUI = findViewById<TextInputEditText>(R.id.editTextDui)
        val nombre = findViewById<TextInputEditText>(R.id.editTextNombre)
        val telefono = findViewById<TextInputEditText>(R.id.editTextTelefono)
        val email = findViewById<TextInputEditText>(R.id.editTextEmail)
        val clave = findViewById<TextInputEditText>(R.id.editTextClave)
        val jvpm = findViewById<TextInputEditText>(R.id.editTextjvpm)
        val jvpm_campo = findViewById<TextInputLayout>(R.id.textInputLayoutjvpm)
        val btnReturn = findViewById<ImageView>(R.id.btnReturn3)



        intet_Rol = intent.getStringExtra("rol").toString()
        if (intet_Rol == "Paciente"){
            jvpm_campo.visibility = View.GONE
        }

        mAuth =FirebaseAuth.getInstance()
        val refBD = FirebaseDatabase.getInstance()
        database = refBD.getReference("$intet_Rol")



        val btn = findViewById<Button>(R.id.btnRegistrar)
        btn.setOnClickListener {
            clavee = clave.text.toString()
            DUI_R = DUI.text.toString()
            nombreR = nombre.text.toString()
            telefonoR = telefono.text.toString()
            direccionR = direccion.text.toString()
            emailR = email.text.toString()
            jvpmL = jvpm.text.toString()

            if (intet_Rol == "Medico") {

                if (!clavee.isNullOrEmpty() && !DUI_R.isNullOrEmpty() && !nombreR.isNullOrEmpty() && !telefonoR.isNullOrEmpty() && !direccionR.isNullOrEmpty() && !emailR.isNullOrEmpty() && !jvpmL.isNullOrEmpty()) {
                    if (clavee.length >= 6) {
                        if (jvpmL.length >= 3 && jvpmL.length <=5) {
                            if (DUI_R.length != 9){
                                DUI.requestFocus()
                                DUI.setError("El DUI debe tener 9 digitos")
                            }else{

                                registrarUser()
                            }

                        } else {
                            jvpm.requestFocus()
                            jvpm.setError("El JVPM debe tener 5 o menos digitos")
                        }
                    } else {
                        clave.requestFocus()
                        clave.setError("La clave debe tener mas de 6 digitos")
                    }
                } else {
                    SweetAlertDialog(this@RegisterUser, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Por favor complete todos los campos")
                        .show()
                }


            } else {

                if (!clavee.isNullOrEmpty() && !DUI_R.isNullOrEmpty() && !nombreR.isNullOrEmpty() && !telefonoR.isNullOrEmpty() && !direccionR.isNullOrEmpty() && !emailR.isNullOrEmpty()) {
                    if (clavee.length >= 6) {
                        if (DUI_R.length != 9){
                            DUI.requestFocus()
                            DUI.setError("El DUI debe tener 9 digitos")
                        }else{

                            registrarUser()
                        }
                    } else {
                        clave.requestFocus()
                        clave.setError("La clave debe ser de 6 digitos")
                    }
                } else {
                    SweetAlertDialog(this@RegisterUser, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Por favor complete los campos")
                        .show()
                }

            }
        }


        btnReturn.setOnClickListener{
            finish()
        }

    }

    //funcion para registrar usuariio
private fun registrarUser() {
        val duiRef = database.child(DUI_R)
        duiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El DUI ya está registrado
                    SweetAlertDialog(this@RegisterUser, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("DUI Registrado")
                        .setContentText("El DUI ya está registrado. Por favor, inicie sesión o contáctese con el equipo técnico.")
                        .setConfirmText("OK")
                        .setConfirmClickListener { sDialog ->
                            // Acción al hacer clic en "OK"
                            sDialog.dismiss()
                        }
                        .show()

                } else {
                    //crear usaurio por email y clave
                    mAuth.createUserWithEmailAndPassword(emailR, clavee)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (intet_Rol == "Medico"){
                                datosList = DatosMedic(
                                    DUI_R,
                                    nombreR,
                                    direccionR,
                                    telefonoR,
                                    emailR,
                                    clavee,
                                    jvpmL
                                )
                                }else{
                                    datosList = DatosPaciente(
                                        DUI_R,
                                        nombreR,
                                        direccionR,
                                        telefonoR,
                                        emailR,
                                        clavee
                                    )
                                }
                                //se registran los datos
                                database.child(DUI_R).setValue(datosList)
                                    .addOnCompleteListener { task2 ->
                                        if (task2.isSuccessful) {

                                            // Obtén una referencia al objeto SharedPreferences
                                            val sharedPreferences = getSharedPreferences("MiSharedPreferences", Context.MODE_PRIVATE)
                                            // Editor para realizar cambios en SharedPreferences
                                            val editor = sharedPreferences?.edit()
                                            // Guardar datos en SharedPreferences
                                            editor?.putString("DUI", DUI_R)
                                            editor?.putString("rol", intet_Rol)
                                            // Aplicar los cambios
                                            editor?.apply()

                                            val intent = Intent(this@RegisterUser, Dashboard::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                this@RegisterUser,
                                                "No se pudo registrar los datos",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                    }

                            } else {
                                //valida si la cuenta ya esta registrada
                                val exception = task.exception
                                if (exception is FirebaseAuthUserCollisionException) {

                                    SweetAlertDialog(this@RegisterUser, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Correo electronico registrado")
                                        .setContentText("La cuenta de correo electrónico ya está registrada. Por favor, inicie sesión o contáctese con el equipo técnico.")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener { sDialog ->
                                            // Acción al hacer clic en "OK"
                                            sDialog.dismiss()
                                        }
                                        .show()


                                } else {
                                    SweetAlertDialog(this@RegisterUser, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("No se pudo registrar los datos")
                                        .show()



                                }
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                SweetAlertDialog(this@RegisterUser, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Error al leer la base de datos .Por favor, inicie sesión o contáctese con el equipo técnico.")
                    .show()

            }
        })
    }



    }






