package mx.edu.potros.gestioninventarios

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Configuracion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        val fechaNacimiento = findViewById<EditText>(R.id.fechaNacimiento)

        fechaNacimiento.setText("20/03/1976")

        fechaNacimiento.setOnClickListener {
            // Obtener la fecha actual del campo (si hay)
            val partesFecha = fechaNacimiento.text.toString().split("/")
            val dia = partesFecha.getOrNull(0)?.toIntOrNull() ?: 1
            val mes = partesFecha.getOrNull(1)?.toIntOrNull()?.minus(1) ?: 0 // Mes comienza en 0
            val anio = partesFecha.getOrNull(2)?.toIntOrNull() ?: 2000

            // Crear DatePickerDialog con esa fecha
            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                fechaNacimiento.setText(fechaFormateada)
            }, anio, mes, dia)

            datePicker.show()

        }
    }
}