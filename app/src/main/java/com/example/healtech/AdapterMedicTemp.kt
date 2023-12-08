import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.healtech.MedicamentosData
import com.example.healtech.R
import com.google.firebase.database.FirebaseDatabase


class AdapterMedicTemp(
    private var Usuarios:List<MedicamentosData>):RecyclerView.Adapter<AdapterMedicTemp.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreMedicamentoRecycler =
            view.findViewById<TextView>(R.id.nombreMedicamentoVistaAdapter)
        val btnEdit = view.findViewById<Button>(R.id.btnEditarVistaAdapter)
        val btnDel = view.findViewById<Button>(R.id.btnEliminarVistaAdapter)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_adaptertempmedic, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return Usuarios.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val User = Usuarios[position]
        holder.nombreMedicamentoRecycler.text = User.nombreMedicamentoData
        val linearLayout = holder.itemView.findViewById<LinearLayout>(R.id.linearLayout)
        when (User.formaFarmaceuticaData) {
            "Tabletas" -> linearLayout.setBackgroundResource(R.drawable.background_analgesico)
            "Cápsulas" -> linearLayout.setBackgroundResource(R.drawable.capsulas)
        }
        holder.btnDel.
        setOnClickListener {
            val sharedPreferences = holder.itemView.context.getSharedPreferences("SharedDuiPacientes", Context.MODE_PRIVATE)
            val sharedDUI = sharedPreferences?.getString("DUIPaciente", "")

            SweetAlertDialog(holder.itemView.context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Reecta ")
                .setContentText("¿Está seguro de que desea borrar la receta para este Usuario?")
                .setConfirmText("Sí, Eliminar")
                .setConfirmClickListener { sDialog ->
                    //colocar url en getInstance()
                    val database = FirebaseDatabase.getInstance()
                    val myRef =
                        database.getReference("Paciente").child("$sharedDUI").child("nd_medic_temp")
                            .child(User.idMedicamento!!)
                    myRef.removeValue()
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


        //Edita un elemento del recycler
        holder.btnEdit.setOnClickListener {

            val mDialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.view_alert_medica, null)
            val mBuilder = AlertDialog.Builder(holder.itemView.context)
                .setView(mDialogView)
                .setTitle("Modificar Medicamento")
            val mAlertDialog = mBuilder.show()





            //spinner para diaHora
            var opcionSeleccionada = ""
            val diaHora = mDialogView.findViewById<Spinner>(R.id.diaHoraAlert)
            // Crea un ArrayAdapter utilizando un arreglo de opciones
            val opciones = arrayOf("Dias", "Horas")
            val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, opciones)

            // Especifica el layout para usar cuando el spinner despliega las opciones
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Aplica el adapter al spinner
            diaHora.adapter = adapter

            // Opcional: Agrega un listener para detectar la opción seleccionada
            diaHora.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    opcionSeleccionada = opciones[position]
                    Toast.makeText(holder.itemView.context, "Opción seleccionada: $opcionSeleccionada", Toast.LENGTH_SHORT).show()
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
            val adapter2 = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, opciones2)

            // Especifica el layout para usar cuando el spinner despliega las opciones
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Aplica el adapter al spinner
            diaHora2.adapter = adapter2

            // Opcional: Agrega un listener para detectar la opción seleccionada
            diaHora2.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    opcionSeleccionada2 = opciones2[position]
                    Toast.makeText(holder.itemView.context, "Opción seleccionada: $opcionSeleccionada2", Toast.LENGTH_SHORT).show()
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
            val adapter3 = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, opciones3)

            // Especifica el layout para usar cuando el spinner despliega las opciones
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Aplica el adapter al spinner
            formaFarmaceutica.adapter = adapter3

            // Opcional: Agrega un listener para detectar la opción seleccionada
            formaFarmaceutica.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    formaFarmaceuticaOp = opciones3[position]
                    Toast.makeText(holder.itemView.context, "Opción seleccionada: $formaFarmaceuticaOp", Toast.LENGTH_SHORT).show()
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
            val adapter4 = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, opciones4)

            // Especifica el layout para usar cuando el spinner despliega las opciones
            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Aplica el adapter al spinner
            viaAdministracion.adapter = adapter4

            // Opcional: Agrega un listener para detectar la opción seleccionada
            viaAdministracion.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    viaAdministracionOp = opciones4[position]
                    Toast.makeText(holder.itemView.context, "Opción seleccionada: $viaAdministracionOp", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Implementación opcional si no se selecciona ninguna opción
                }
            })


            val nombreMedicamento = mDialogView.findViewById<EditText>(R.id.nombreMedicamentoAlert)
            val Und = mDialogView.findViewById<EditText>(R.id.undAlert)
            val cadaCuanto = mDialogView.findViewById<EditText>(R.id.cadaCuantoAlert)
            val numero = mDialogView.findViewById<EditText>(R.id.numeroAlert)
            val instrucciones = mDialogView.findViewById<EditText>(R.id.instruccionesAlert)

            nombreMedicamento.setText(User.nombreMedicamentoData)
            Und.setText(User.unidadData)
            cadaCuanto.setText(User.cadaCuantoData)
            numero.setText(User.numeroData)
            instrucciones.setText(User.instruccionesData)




            mDialogView.findViewById<Button>(R.id.btnAdd).setOnClickListener {

                val nombreMedicamento = mDialogView.findViewById<EditText>(R.id.nombreMedicamentoAlert).text.toString()
                val Und = mDialogView.findViewById<EditText>(R.id.undAlert).text.toString()
                val cadaCuanto = mDialogView.findViewById<EditText>(R.id.cadaCuantoAlert).text.toString()
                val numero = mDialogView.findViewById<EditText>(R.id.numeroAlert).text.toString()
                val instrucciones = mDialogView.findViewById<EditText>(R.id.instruccionesAlert).text.toString()


                val sharedPreferences = holder.itemView.context.getSharedPreferences("SharedDuiPacientes", Context.MODE_PRIVATE)

                val sharedDUI = sharedPreferences?.getString("DUIPaciente", "")
                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("Paciente").child("$sharedDUI").child("nd_medic_temp")
                    .child(User.idMedicamento!!)
                val ActuMedicamentos = MedicamentosData(
                    User.idMedicamento,
                    nombreMedicamento, Und, cadaCuanto,
                    opcionSeleccionada, formaFarmaceuticaOp,viaAdministracionOp,
                    numero,opcionSeleccionada2,instrucciones
                )
                myRef.setValue(ActuMedicamentos)
                mAlertDialog.dismiss()
                val builder = AlertDialog.Builder(holder.itemView.context)
                builder.setTitle("Medicamento Guardado correctamente")
                builder.setMessage("Los datos han sido enviado con exito hacia Firebase.")
                builder.setPositiveButton("OK") { _, _ -> }
                val dialog = builder.create()
                dialog.show()
            }
            mDialogView.findViewById<Button>(R.id.btnDismiss).setOnClickListener {
                mAlertDialog.dismiss()
            }

        }



    }
}