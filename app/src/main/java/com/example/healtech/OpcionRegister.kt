package com.example.healtech

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class OpcionRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opcion_register)
        // Obtén las referencias a las vistas en tu código Kotlin
        val textView: TextView = findViewById(R.id.OpRegistes)
        val imageView1: ImageView = findViewById(R.id.imageView1)
        val imageView2: ImageView = findViewById(R.id.imageView2)
        val BtnReturn: ImageButton = findViewById(R.id.btnReturn2)

        imageView1.setOnClickListener {
            val intent = Intent(this, RegisterUser::class.java)
            val rol = "Medico"
            intent.putExtra("rol",rol)
            startActivity(intent)
        }
        imageView2.setOnClickListener {
            val intent = Intent(this, RegisterUser::class.java)
            val rol = "Paciente"
            intent.putExtra("rol",rol)
            startActivity(intent)
        }
        BtnReturn.setOnClickListener {

            finish()
        }

        val textViewAnimation = ObjectAnimator.ofFloat(textView, "translationY", -500f, 0f)
        textViewAnimation.duration = 1000
        textViewAnimation.start()


        val imageView1Animation = ObjectAnimator.ofFloat(imageView1, "translationY", 500f, 0f)
        imageView1Animation.duration = 1000
        imageView1Animation.start()

        val imageView2Animation = ObjectAnimator.ofFloat(imageView2, "translationY", 500f, 0f)
        imageView2Animation.duration = 1000
        imageView2Animation.start()

    }

    }
