package com.example.healtech.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.healtech.AdapterMedicamentos
import com.example.healtech.AdapterRecetaMedic
import com.example.healtech.MedicamentosData
import com.example.healtech.RecetasData
import com.example.healtech.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var myRef: DatabaseReference
    private lateinit var myRef2: DatabaseReference
    private lateinit var myRef3: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = context?.getSharedPreferences("MiSharedPreferences", Context.MODE_PRIVATE)

        val sharedDUI = sharedPreferences?.getString("DUI", "")

        //referencia al nodo
        val database = FirebaseDatabase.getInstance()
        myRef = database.getReference("Paciente/$sharedDUI/Recetas")
        


        val recyclerView: RecyclerView = binding.recyclerRecetas
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val listaObjetos = ArrayList<RecetasData>() // Lista para almacenar los objetos obtenidos de Firebase
        val adapter = AdapterRecetaMedic(listaObjetos)
        recyclerView.adapter = adapter

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listaObjetos.clear()
                    for (dataSnapshot in snapshot.children) {
                        val songData = dataSnapshot.getValue(RecetasData::class.java)
                        listaObjetos.add(songData!!)
                    }
                    Log.e("Valor", listaObjetos.toString())
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Falló la lectura de Firebase")
                    .show()

            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
