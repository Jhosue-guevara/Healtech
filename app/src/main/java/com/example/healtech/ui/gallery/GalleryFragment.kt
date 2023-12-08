package com.example.healtech.ui.gallery

import AdapterMedicTemp
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.healtech.MedicamentosData
import com.example.healtech.R
import com.example.healtech.RecetasData
import com.example.healtech.databinding.FragmentGalleryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        galleryViewModel.text.observe(viewLifecycleOwner) {

        }



        //Lee los datos del SharedPreference
        val sharedPreferences = context?.getSharedPreferences("MiSharedPreferences", Context.MODE_PRIVATE)

        val sharedDUIMedico = sharedPreferences?.getString("DUI", "")



        //TextViews
        val nombreDoctortxt: TextView = binding.NombreDoctorReceta      //Doctor
        val direccionDoctortxt: TextView = binding.direccionReceta
        val telefonoDoctortxt: TextView = binding.telefonoReceta

        val nombrePacientetxt: TextView = binding.nombrePacienteReceta      //Paciente
        val direccionPacientetxt: TextView = binding.direccionPacienteReceta
        val telefonoPacientetxt: TextView = binding.telefonoPacienteReceta



        


        //Elementos para buscar un paciente
        val btnBuscarPaciente: Button = binding.btnBuscarPacienteReceta


        var DuiPacienteShared = ""

        //Para buscar un paciente
        btnBuscarPaciente.setOnClickListener{
            val editBuscarPaciente: EditText = binding.BuscarPacienteDuiReceta
            val editBuscarPaciente2 = editBuscarPaciente.text.toString()
            DuiPacienteShared = editBuscarPaciente2


            // Obtén una referencia al objeto SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("SharedDuiPacientes", Context.MODE_PRIVATE)
            // Editor para realizar cambios en SharedPreferences
            val editor = sharedPreferences?.edit()
            // Guardar datos en SharedPreferences
            editor?.putString("DUIPaciente", DuiPacienteShared)
            // Aplicar los cambios
            editor?.apply()

            //Conexion con la base de datos
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Paciente/$DuiPacienteShared")

            myRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        //Para leer los datos
                        myRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val genericTypeIndicator = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                                val hashMap = snapshot.getValue(genericTypeIndicator)

                                if (hashMap != null) {
                                    nombrePacientetxt.text = hashMap["nombre"].toString()
                                    direccionPacientetxt.text = hashMap["direccion"].toString()
                                    telefonoPacientetxt.text = hashMap["telefono"].toString()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Manejar el error si ocurre
                            }
                        })

                        editBuscarPaciente.text.clear()
                        //Se muestran los datos en el rycler
                        val listado = arrayListOf<MedicamentosData>()
                        val recycler = binding.recyclerMedicamentosReceta

                        val database2 = FirebaseDatabase.getInstance()
                        val myRef2 = database2.getReference("Paciente/$DuiPacienteShared/nd_medic_temp")
                        val adapter = AdapterMedicTemp(listado)
                        recycler.adapter = adapter
                        recycler.layoutManager = LinearLayoutManager(context)
                        myRef2.addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    for(songsnap in snapshot.children){
                                        val songdata = songsnap.getValue(MedicamentosData::class.java)
                                        listado.add(songdata!!)
                                    }
                                    Log.e("Valor",listado.toString())
                                    adapter.notifyDataSetChanged()

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }
                    else{
                        Toast.makeText(requireContext(), "No se encontró el paciente", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        }


        //Conexion con la base de datos
        val databaseee = FirebaseDatabase.getInstance()
        val myRefff = databaseee.getReference("Medico/$sharedDUIMedico")

        //Para leer los datos
        myRefff.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val genericTypeIndicator = object : GenericTypeIndicator<HashMap<String, String>>() {}
                val hashMap = snapshot.getValue(genericTypeIndicator)

                if (hashMap != null) {
                    nombreDoctortxt.text = hashMap["nombre"].toString()
                    direccionDoctortxt.text = hashMap["direccion"].toString()
                    telefonoDoctortxt.text = hashMap["telefono"].toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error si ocurre
            }
        })







        //Boton para enviar la receta
        val btnEnviarReceta: Button = binding.btnEnviarReceta
        btnEnviarReceta.setOnClickListener{

            //Lee los datos del SharedPreference
            val sharedPreferences = context?.getSharedPreferences("SharedDuiPacientes", Context.MODE_PRIVATE)
            val sharedDUIPaciente = sharedPreferences?.getString("DUIPaciente", "")

            val diagnosticoPaciente = binding.diagnosticoReceta
            val diagnosticoPaciente2 = diagnosticoPaciente.text.toString()


            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Paciente")

            //Genera el id de la receta y registra los datos
            val idReceta = myRef.push().key
            val Receta = RecetasData(idReceta, nombreDoctortxt.text.toString(),
                telefonoDoctortxt.text.toString(), nombrePacientetxt.text.toString(), diagnosticoPaciente2)
            myRef.child("$sharedDUIPaciente").child("Recetas").child(idReceta!!).setValue(Receta)

            diagnosticoPaciente.text.clear()

            //Migra los datos del nodo nd_medic_temp a Medicamentos
            val database2 = FirebaseDatabase.getInstance()
            val referencia = database2.reference

            val nodoOrigenRef = referencia.child("Paciente/$sharedDUIPaciente/nd_medic_temp")
            nodoOrigenRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val valor = dataSnapshot.value // Obtener el valor del nodo origen

                    val nodoDestinoRef = referencia.child("Paciente/$sharedDUIPaciente/Recetas/$idReceta/Medicamentos")
                    nodoDestinoRef.setValue(valor) // Guardar el valor en el nodo destino

                    // Aquí puedes realizar acciones con el valor obtenido, como guardarlo en otro nodo
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejar el error en caso de que la lectura del nodo origen falle
                }
            })

            nodoOrigenRef.removeValue()
            val activity = this as? Activity
            activity?.recreate()

            SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("RECETA CREADA!")
                .setContentText("La receta fue enviada satisfactoriamente!")
                .show()

        }







        //Muestra el AlertDialog
        val btnAgregarMedicamento: Button = binding.btnAddMedicamentoReceta

        btnAgregarMedicamento.setOnClickListener {


            val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.view_alert_medica, null)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
                .setTitle("Agregar Medicamento")
            val mAlertDialog = mBuilder.show()





            //spinner para diaHora
            var opcionSeleccionada = ""
            val diaHora = mDialogView.findViewById<Spinner>(R.id.diaHoraAlert)
            // Crea un ArrayAdapter utilizando un arreglo de opciones
            val opciones = arrayOf("Dias", "Horas")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)

            // Especifica el layout para usar cuando el spinner despliega las opciones
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Aplica el adapter al spinner
            diaHora.adapter = adapter

            // Opcional: Agrega un listener para detectar la opción seleccionada
            diaHora.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    opcionSeleccionada = opciones[position]
                    Toast.makeText(requireContext(), "Opción seleccionada: $opcionSeleccionada", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Implementación opcional si no se selecciona ninguna opción
                }
            })





            //spinner para diaHora2-------------------
            var opcionSeleccionada2 = ""
            val diaHora2 = mDialogView.findViewById<Spinner>(R.id.diaHoraAlert2)
            // Crea un ArrayAdapter utilizando un arreglo de opciones
            val opciones2 = arrayOf("Dias", "Horas")
            val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones2)

            // Especifica el layout para usar cuando el spinner despliega las opciones
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Aplica el adapter al spinner
            diaHora2.adapter = adapter2

            // Opcional: Agrega un listener para detectar la opción seleccionada
            diaHora2.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    opcionSeleccionada2 = opciones2[position]
                    Toast.makeText(requireContext(), "Opción seleccionada: $opcionSeleccionada2", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Implementación opcional si no se selecciona ninguna opción
                }
            })





            //spinner para formaFarmaceutica-------------------
            var formaFarmaceuticaOp = ""
            val formaFarmaceutica = mDialogView.findViewById<Spinner>(R.id.spnFormaFarmaceuticaAlert)
            // Crea un ArrayAdapter utilizando un arreglo de opciones
            val opciones3 = arrayOf("Tabletas", "Cápsulas","Jarabe","Inyectable","Ampolla","Crema","Inhaladores","Supositorios")
            val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones3)

            // Especifica el layout para usar cuando el spinner despliega las opciones
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Aplica el adapter al spinner
            formaFarmaceutica.adapter = adapter3

            // Opcional: Agrega un listener para detectar la opción seleccionada
            formaFarmaceutica.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    formaFarmaceuticaOp = opciones3[position]
                    Toast.makeText(requireContext(), "Opción seleccionada: $formaFarmaceuticaOp", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Implementación opcional si no se selecciona ninguna opción
                }
            })




            //spinner para viaAdministracion-------------------
            var viaAdministracionOp = ""
            val viaAdministracion = mDialogView.findViewById<Spinner>(R.id.spnViaAlert)
            // Crea un ArrayAdapter utilizando un arreglo de opciones
            val opciones4 = arrayOf("Vía oral", "Vía tópica","Vía intravenosa","Vía intramuscular","Vía inhalatoria")
            val adapter4 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones4)

            // Especifica el layout para usar cuando el spinner despliega las opciones
            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Aplica el adapter al spinner
            viaAdministracion.adapter = adapter4

            // Opcional: Agrega un listener para detectar la opción seleccionada
            viaAdministracion.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    viaAdministracionOp = opciones4[position]
                    Toast.makeText(requireContext(), "Opción seleccionada: $viaAdministracionOp", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Implementación opcional si no se selecciona ninguna opción
                }
            })





            mDialogView.findViewById<Button>(R.id.btnAdd).setOnClickListener {
                //Lee los datos del SharedPreference
                val sharedPreferences = context?.getSharedPreferences("SharedDuiPacientes", Context.MODE_PRIVATE)
                val sharedDUIPaciente = sharedPreferences?.getString("DUIPaciente", "")

                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("Paciente")

                val nombreMedicamento = mDialogView.findViewById<EditText>(R.id.nombreMedicamentoAlert).text.toString()
                val Und = mDialogView.findViewById<EditText>(R.id.undAlert).text.toString()
                val cadaCuanto = mDialogView.findViewById<EditText>(R.id.cadaCuantoAlert).text.toString()
                val numero = mDialogView.findViewById<EditText>(R.id.numeroAlert).text.toString()
                val instrucciones = mDialogView.findViewById<EditText>(R.id.instruccionesAlert).text.toString()



                //Genera el id del medicamento y registra los datos
                val idMedicamentoTemporal = myRef.push().key
                val medicamentosData = MedicamentosData(idMedicamentoTemporal, nombreMedicamento, Und, cadaCuanto,
                    opcionSeleccionada, formaFarmaceuticaOp,viaAdministracionOp,
                    numero,opcionSeleccionada2,instrucciones)
                myRef.child("$sharedDUIPaciente").child("nd_medic_temp").child(idMedicamentoTemporal!!).setValue(medicamentosData)

                mAlertDialog.dismiss()

                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Medicamento Guardado correctamente!")
                    .setContentText("El medicamento fue enviado satisfactoriamente a Firebase")
                    .show()


            }
            mDialogView.findViewById<Button>(R.id.btnDismiss).setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}