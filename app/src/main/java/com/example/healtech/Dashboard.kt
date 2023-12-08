package com.example.healtech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.healtech.databinding.ActivityDashboardBinding
import com.example.healtech.ui.gallery.GalleryFragment
import com.example.healtech.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth

class Dashboard : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarDashboard.toolbar)


        mAuth =FirebaseAuth.getInstance()

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        // Obtener el rol de SharedPreferences
        val sharedPreferences = baseContext.getSharedPreferences("MiSharedPreferences", Context.MODE_PRIVATE)
        val sharedRol = sharedPreferences?.getString("rol", "")

        // Determinar el fragmento inicial según el rol
        val startDestination = if (sharedRol == "Medico") {
            R.id.nav_gallery // Fragmento inicial para el rol "Medico"
        } else {
            R.id.nav_home // Fragmento inicial para otros roles
        }

        // Navegar al fragmento inicial
        navController.navigate(startDestination)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        val sharedPreferences = baseContext.getSharedPreferences("MiSharedPreferences", Context.MODE_PRIVATE)
        val sharedRol = sharedPreferences?.getString("rol", "")

        if (sharedRol == "Medico"){
            when (menuItem.itemId) {
                R.id.nav_gallery -> {
                    // Navegar al fragmento GalleryFragment
                    navController.navigate(R.id.nav_gallery)
                    return true
                }
                R.id.nav_slideshow -> {
                    showLogoutConfirmationDialog()
                    return false
                }
            }
        }else{
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Navegar al fragmento HomeFragment
                    navController.navigate(R.id.nav_home)
                    return true
                }

                R.id.nav_slideshow -> {
                    showLogoutConfirmationDialog()
                    return false
                }
            }
        }

        return false
    }



    private fun showLogoutConfirmationDialog() {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Cerrar sesión")
            .setContentText("¿Estás seguro de que deseas cerrar sesión?")
            .setConfirmText("Sí, cerrar sesión")
            .setConfirmClickListener { sDialog ->
                // Obtén una referencia al objeto SharedPreferences
                val sharedPreferences = this.getSharedPreferences("SharedDuiPacientes", Context.MODE_PRIVATE)
                // Editor para realizar cambios en SharedPreferences
                val editor = sharedPreferences?.edit()
                // Guardar datos en SharedPreferences
                editor?.putString("DUIPaciente", "")
                // Aplicar los cambios
                editor?.apply()

                // Obtén una referencia al objeto SharedPreferences
                val sharedPreferences2 = getSharedPreferences("MiSharedPreferences", Context.MODE_PRIVATE)
                // Editor para realizar cambios en SharedPreferences
                val editor2 = sharedPreferences2?.edit()
                // Guardar datos en SharedPreferences
                editor2?.putString("DUIDoctor", "")
                editor2?.putString("rol", "")
                // Aplicar los cambios
                editor2?.apply()

                mAuth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                sDialog.dismiss()
            }
            .setCancelText("No,cancelar!")
            .setCancelClickListener { sDialog ->
                // Acción al hacer clic en "No, cancel!"
                // Por ejemplo, cerrar el diálogo
                sDialog.dismiss()
            }
            .show()




    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}