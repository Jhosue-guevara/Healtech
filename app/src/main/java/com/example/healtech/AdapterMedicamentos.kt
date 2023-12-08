package com.example.healtech

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.util.Random



import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar


class AdapterMedicamentos (


    private var Medicamentos:List<MedicamentosData>): RecyclerView.Adapter<AdapterMedicamentos.ViewHolder>()
{

    lateinit var alarmManager: AlarmManager
    lateinit var formatoDosis: String
    lateinit var formatoDuracion: String
    lateinit var alarmHandler: Handler


    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val linearMedica = view.findViewById<LinearLayout>(R.id.linarMedicamentos)
        val btnEstado = view.findViewById<ImageButton>(R.id.btnEstado)
        val txtnombre = view.findViewById<TextView>(R.id.txtnombreMedicamento_adapter)
        val dosis = view.findViewById<TextView>(R.id.txtDosis_adapter)
        val duracion = view.findViewById<TextView>(R.id.txtduracion_adapter)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMedicamentos.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_adaptermedicamentos,parent,false)
        return AdapterMedicamentos.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Medicamentos.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AdapterMedicamentos.ViewHolder, position: Int) {
        val show = Medicamentos[position]
        holder.txtnombre.text = show.nombreMedicamentoData
        val unid = show.unidadData
        val cadaS = show.cadaCuantoData
        val formatoD = show.diaHoraData
        val formaFarma = show.formaFarmaceuticaData
        holder.dosis.text = "$unid $formaFarma cada $cadaS $formatoD"
        val duracion = show.numeroData
        val formatoDura = show.diaHora2Data
        holder.duracion.text = "Duración $duracion $formatoDura"


        holder.linearMedica.setOnClickListener {
            val mDialog = AlertDialog.Builder(holder.itemView.context)
            val inflater = LayoutInflater.from(holder.itemView.context)
            val mDialogView = inflater.inflate(R.layout.view_detallesmedicamento, null)
            mDialog.setView(mDialogView)
            val alertDialog = mDialog.create()
            alertDialog.show()

            val nombre = mDialogView.findViewById<TextView>(R.id.txtnombreMedicamento_view)
            val dosis = mDialogView.findViewById<TextView>(R.id.txtdosis_view)
            val viaAdminis = mDialogView.findViewById<TextView>(R.id.viaAdminis_view)
            val notas = mDialogView.findViewById<TextView>(R.id.txtnotas_view)
            val duracion = mDialogView.findViewById<TextView>(R.id.txtduracion_view)
            val btnok = mDialogView.findViewById<Button>(R.id.btnok)

            val unid = show.unidadData
            val cada = show.cadaCuantoData
            val formatoD = show.diaHoraData
            val formaFarma = show.formaFarmaceuticaData

            nombre.setText(show.nombreMedicamentoData)
            dosis.setText("Dosis: $unid $formaFarma cada $cada $formatoD")
            viaAdminis.setText(show.viaAdministracionData)

            notas.setText(show.instruccionesData)

            val duracionM = show.numeroData
            val formatoDura = show.diaHora2Data
            duracion.setText("Duración $duracionM $formatoDura")

            btnok.setOnClickListener{

                alertDialog.dismiss()
            }



        }
        holder.btnEstado.setOnClickListener {
            if (formatoD == "Horas"){
                formatoDosis =(cadaS.toString().toInt() * 3600000).toString()
            }else{
                formatoDosis = (cadaS.toString().toInt() * 86400000).toString()
            }

            if (formatoDura == "Horas"){
                formatoDuracion = (duracion.toString().toInt() * 3600000).toString()
            }else{
                formatoDuracion = (duracion.toString().toInt() * 86400000).toString()
            }

            programAlarm(show,holder.itemView.context, formatoDosis.toLong(),formatoDuracion.toLong())

        }




    }
    fun programAlarm(alarm: MedicamentosData, context: Context, cada: Long, duracion: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val random = Random()
        val minId = 1
        val maxId = 100000
        val generatedIds = mutableSetOf<Int>()

        fun generarIdUnico(): Int {
            var medicamentoId = random.nextInt(maxId - minId + 1) + minId
            while (generatedIds.contains(medicamentoId)) {
                medicamentoId = random.nextInt(maxId - minId + 1) + minId
            }
            generatedIds.add(medicamentoId)
            return medicamentoId
        }

        val alarmId = generarIdUnico()

        val alarmHandler = Handler(Looper.getMainLooper())
        alarmHandler.postDelayed({
            stopAlarm(context,alarmId)
        }, duracion)

        // Inicia la alarma
        startAlarm(alarm, context, cada,alarmId)
    }

    fun startAlarm(alarm: MedicamentosData, context: Context, intervalox: Long, alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Obtiene el tiempo actual
        val currentTime = Calendar.getInstance().timeInMillis

        // Calcula el tiempo para la primera alarma
        val firstAlarmTime = currentTime + intervalox

        val intent = Intent(context, AlarmReceiver::class.java)



        intent.putExtra("alarmId", alarmId)
        intent.putExtra("nombreM", alarm.nombreMedicamentoData)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Configura la alarma para que se repita en intervalos regulares
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            firstAlarmTime,
            intervalox,
            pendingIntent
        )

        Toast.makeText(context, "Alarma iniciada", Toast.LENGTH_SHORT).show()
    }

    fun stopAlarm(context: Context, alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Detiene la alarma
        alarmManager.cancel(pendingIntent)

        Toast.makeText(context, "Alarma detenida", Toast.LENGTH_SHORT).show()
    }







    }






