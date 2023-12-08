package com.example.healtech

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var intet_Rol: String
    lateinit var email_L :String
    lateinit var clave_L: String
    lateinit var DUI_L : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val BtnReturn: ImageButton = findViewById(R.id.btnReturn)
        val registrate = findViewById<TextView>(R.id.textRegistrate)
        val BtnLoginUser= findViewById<Button>(R.id.btnIniciar)


        BtnReturn.setOnClickListener {
            finish()
        }
        registrate.setOnClickListener{
            val intent = Intent(this, OpcionRegister::class.java)
            startActivity(intent)
        }

        val email = findViewById<TextInputEditText>(R.id.editTextEmail)
        val clave = findViewById<TextInputEditText>(R.id.editTextPassword)
        val DUI = findViewById<TextInputEditText>(R.id.editTextDUI)


        intet_Rol = intent.getStringExtra("rol_ini").toString()

        mAuth =FirebaseAuth.getInstance()
        val refBD = FirebaseDatabase.getInstance()
        database = refBD.getReference("$intet_Rol")

        BtnLoginUser.setOnClickListener {
            email_L = email.text.toString()
            clave_L = clave.text.toString()
            DUI_L = DUI.text.toString()

            if (!email_L.isNullOrEmpty() && !clave_L.isNullOrEmpty() && !DUI_L.isNullOrEmpty()){
                if (DUI_L.length == 9){
                    loginUser()
                }else{
                    DUI.requestFocus()
                    DUI.setError("El DUI debe tener 9 digitos")
                }

            }else{
                SweetAlertDialog(this@Login, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Complete los campos por favor ")
                    .show()
            }

        }

    }
    private fun loginUser(){
        val duiRef = database.child(DUI_L)
        duiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    mAuth.signInWithEmailAndPassword(email_L, clave_L)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {


                                // Obtén una referencia al objeto SharedPreferences
                                val sharedPreferences = getSharedPreferences("MiSharedPreferences", Context.MODE_PRIVATE)
                                // Editor para realizar cambios en SharedPreferences
                                val editor = sharedPreferences?.edit()
                                // Guardar datos en SharedPreferences
                                editor?.putString("DUI", DUI_L)
                                editor?.putString("rol", intet_Rol)
                                // Aplicar los cambios
                                editor?.apply()


                                val intent = Intent(this@Login, Dashboard::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                SweetAlertDialog(this@Login, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("No se encontraron los datos de inicio de sesión. Verifique los datos e intente nuevamente.")
                                    .show()


                            }
                        }

                } else {
                    SweetAlertDialog(this@Login, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("DUI no regitrado")
                        .setContentText("El DUI no esta regitrado. Por favor, Registrate.")
                        .setConfirmText("OK")
                        .setConfirmClickListener { sDialog ->
                            // Acción al hacer clic en "OK"
                            sDialog.dismiss()
                        }
                        .show()

                }
            }
            override fun onCancelled(error: DatabaseError) {
                SweetAlertDialog(this@Login, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("No se pudo conectar a Firebase.Por favor contáctese con el equipo técnico. ")
                    .show()
            }
        })
    }



}