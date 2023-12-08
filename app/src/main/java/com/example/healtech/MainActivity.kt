package com.example.healtech
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth =FirebaseAuth.getInstance()
        val buttonSignIn = findViewById<Button>(R.id.buttonSignIn)
        val ButtonbuttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonSignIn.setOnClickListener {
            // Aquí defines la acción al hacer clic en el botón
            val intent = Intent(this, opLogin::class.java)
            startActivity(intent)

        }
        ButtonbuttonRegister.setOnClickListener {
            val intent = Intent(this, OpcionRegister::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser != null){
            startActivity(Intent(this@MainActivity,Dashboard::class.java))
            finish()
        }
    }

    }
