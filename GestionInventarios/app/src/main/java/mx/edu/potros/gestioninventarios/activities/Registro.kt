package mx.edu.potros.gestioninventarios.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import mx.edu.potros.gestioninventarios.DAO.IUsuarioDAO
import mx.edu.potros.gestioninventarios.DAO.UsuarioDAO
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Usuario
import java.util.Calendar
import java.util.Locale

class Registro : AppCompatActivity() {


    private var usuarioDAO : IUsuarioDAO = UsuarioDAO()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val nombre: EditText = findViewById(R.id.nombreRegistro)
        val correo: EditText = findViewById(R.id.correoRegistro)
        val contra: EditText = findViewById(R.id.contrasena)
        val confContra: EditText = findViewById(R.id.confirmContrasena)
        val etFecha: EditText = findViewById(R.id.fechaNacimientoRegistro)
        val spinnerGenero: Spinner = findViewById(R.id.spinnerRegistro)
        val ivRegresar: ImageView = findViewById(R.id.regresar)
        val btnRegistrar: Button = findViewById(R.id.btnRegistro)



        // Filtro para no permitir espacios en las contraseñas
        val sinEspacios = InputFilter { source, _, _, _, _, _ ->
            if (source.toString().contains(" ")) "" else null
        }
        contra.filters = arrayOf(sinEspacios)
        confContra.filters = arrayOf(sinEspacios)

        ivRegresar.setOnClickListener {
            val intento = Intent(this, LoginActivity::class.java)
            startActivity(intento)
            finish()
        }

        etFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val año = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val fecha = String.format("%02d/%02d/%04d", d, m + 1, y)
                etFecha.setText(fecha)
            }, año, mes, dia)

            datePicker.show()
        }

        btnRegistrar.setOnClickListener {
            val nombreTexto = nombre.text.toString().trim()
            val correoTexto = correo.text.toString().trim()
            val contraTexto = contra.text.toString().trim()
            val confContraTexto = confContra.text.toString().trim()
            val generoSeleccionado = spinnerGenero.selectedItem.toString()
            val fechaNacimientoTexto = etFecha.text.toString().trim()
            val edad = calcularEdad(fechaNacimientoTexto)

            if (nombreTexto.isEmpty() || correoTexto.isEmpty() ||
                contraTexto.isEmpty() || confContraTexto.isEmpty() || fechaNacimientoTexto.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (!correoTexto.contains("@")) {
                Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (generoSeleccionado == "Género") {
                Toast.makeText(this, "Selecciona un género válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (edad < 14) {
                Toast.makeText(this, "Debes tener al menos 13 años", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (edad > 99) {
                Toast.makeText(this, "La edad máxima permitida es 99 años", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (contraTexto != confContraTexto) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{


                var usuario = Usuario("", nombreTexto, correoTexto, fechaNacimientoTexto, generoSeleccionado, contraTexto, confContraTexto)

                usuarioDAO.registrarConFirebaseAuthYGuardarFirestore(
                    email = correoTexto,
                    password = contraTexto,
                    usuario = usuario,
                    onSuccess = {
                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    },
                    onFailure = {
                        Toast.makeText(this, "Error al registrar: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun calcularEdad(fechaStr: String): Int {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val fechaNacimiento = formato.parse(fechaStr)
            val hoy = Calendar.getInstance()
            val nacimiento = Calendar.getInstance()
            nacimiento.time = fechaNacimiento!!

            var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
                edad--
            }
            edad
        } catch (e: Exception) {
            0
        }
    }


}
