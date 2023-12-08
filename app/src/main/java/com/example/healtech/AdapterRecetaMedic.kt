package com.example.healtech

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterRecetaMedic (

    private var Receta:List<RecetasData>): RecyclerView.Adapter<AdapterRecetaMedic.ViewHolder>()
{
    private lateinit var myRef: DatabaseReference
    private lateinit var myRef2: DatabaseReference
    private lateinit var myRef3: DatabaseReference

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val linearReceta = view.findViewById<LinearLayout>(R.id.linearReceta)
        val txtnombre = view.findViewById<TextView>(R.id.txtnombreDoc_R)
        val txtTel = view.findViewById<TextView>(R.id.txtTel_R)
        val txtDiagnos = view.findViewById<TextView>(R.id.txtDiag_R)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRecetaMedic.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_adapeterecetas,parent,false)
        return AdapterRecetaMedic.ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return Receta.size
    }

    override fun onBindViewHolder(holder: AdapterRecetaMedic.ViewHolder, position: Int) {
        val show = Receta[position]
        holder.txtnombre.text = "Doctor: " +show.NombreDoc
        holder.txtTel.text = "Telefono: " +show.TelDoc
        if (show.Diagnostico!!.isNotEmpty()) {
            holder.txtDiagnos.text = show.Diagnostico
        } else {
            holder.txtDiagnos.text = "No se encontró diagnóstico"
        }

        holder.linearReceta.setOnClickListener {
            val mDialog = AlertDialog.Builder(holder.itemView.context)
            val inflater = LayoutInflater.from(holder.itemView.context)
            val mDialogView = inflater.inflate(R.layout.view_detallereceta, null)
            mDialog.setView(mDialogView)
            val alertDialog = mDialog.create()
            alertDialog.show()
            val txtviewNombre = mDialogView.findViewById<TextView>(R.id.txtnombre_viewR)
            val txtviewtel = mDialogView.findViewById<TextView>(R.id.txtTel_viewR)
            val txtviewDiag = mDialogView.findViewById<TextView>(R.id.txtDiag_viewR)
            val recyclerView = mDialogView.findViewById<RecyclerView>(R.id.recycler_ViewR)

            txtviewNombre.setText(show.NombreDoc)
            txtviewtel.setText(show.TelDoc)
            txtviewDiag.setText(show.Diagnostico)
            val sharedPreferences = holder.itemView.context.getSharedPreferences("MiSharedPreferences", Context.MODE_PRIVATE)

            val sharedDUI = sharedPreferences?.getString("DUI", "")

            //referencia al nodo
            val database = FirebaseDatabase.getInstance()

            myRef2 = database.getReference("Paciente/$sharedDUI/Recetas/${show.idReceta}/Medicamentos")




            recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            recyclerView.setHasFixedSize(true)

            val listaObjetos = ArrayList<MedicamentosData>() // Lista para almacenar los objetos obtenidos de Firebase
            val adapter = AdapterMedicamentos(listaObjetos)
            recyclerView.adapter = adapter

            myRef2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        listaObjetos.clear()
                        for (dataSnapshot in snapshot.children) {
                            val songData = dataSnapshot.getValue(MedicamentosData::class.java)
                            listaObjetos.add(songData!!)
                        }
                        Log.e("Valor", listaObjetos.toString())
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar el error
                    Log.e("Error", "Falló la lectura de Firebase: $error")
                }
            })
        }
    }


}