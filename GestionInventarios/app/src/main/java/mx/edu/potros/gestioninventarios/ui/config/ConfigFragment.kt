package mx.edu.potros.gestioninventarios.ui.config

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.activities.LoginActivity
import java.util.*

class ConfigFragment : Fragment() {

    companion object {
        fun newInstance() = ConfigFragment()
    }

    private val viewModel: ConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivVolver: ImageView = view.findViewById(R.id.regresar)
        val nombreUsuario: EditText = view.findViewById(R.id.nombreUsuario)
        val fechaNacimiento: EditText = view.findViewById(R.id.fechaNacimiento)
        val spinnerConfig: Spinner = view.findViewById(R.id.spinnerConfig)
        val btnGuardar: Button = view.findViewById(R.id.btnGuardar)
        val btnCerrarSesion: Button = view.findViewById(R.id.btnLogout)

        // Configurar spinner de género
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.opciones_spinner2,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerConfig.adapter = adapter

        // Selector de fecha
        fechaNacimiento.setOnClickListener {
            val partesFecha = fechaNacimiento.text.toString().split("/")
            val dia = partesFecha.getOrNull(0)?.toIntOrNull() ?: 1
            val mes = partesFecha.getOrNull(1)?.toIntOrNull()?.minus(1) ?: 0
            val anio = partesFecha.getOrNull(2)?.toIntOrNull() ?: 2000

            val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)

                val edad = calcularEdad(fechaFormateada)
                if (edad < 13) {
                    Toast.makeText(requireContext(), "Debes tener al menos 13 años", Toast.LENGTH_SHORT).show()
                } else if (edad > 99) {
                    Toast.makeText(requireContext(), "La edad máxima permitida es 99 años", Toast.LENGTH_SHORT).show()
                } else {
                    fechaNacimiento.setText(fechaFormateada)
                }
            }, anio, mes, dia)

            datePicker.datePicker.maxDate = Calendar.getInstance().timeInMillis
            datePicker.show()
        }

        // Obtener UID del usuario actual
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        if (uid != null) {
            val docRef = db.collection("usuarios").document(uid)

            // Cargar datos del usuario desde Firestore
            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    val fecha = document.getString("fechaNacimiento")
                    val genero = document.getString("genero")

                    nombreUsuario.setText(nombre ?: "")
                    fechaNacimiento.setText(fecha ?: "")

                    val index = adapter.getPosition(genero ?: "Masculino")
                    if (index >= 0) {
                        spinnerConfig.setSelection(index)
                    }
                } else {
                    Toast.makeText(requireContext(), "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }

            // Guardar cambios al presionar el botón
            btnGuardar.setOnClickListener {
                val nombre = nombreUsuario.text.toString().trim()
                val fecha = fechaNacimiento.text.toString().trim()
                val genero = spinnerConfig.selectedItem.toString()

                if (nombre.isEmpty()) {
                    Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (fecha.isEmpty()) {
                    Toast.makeText(requireContext(), "Debes seleccionar una fecha válida", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val edad = calcularEdad(fecha)
                if (edad < 13 || edad > 99) {
                    Toast.makeText(requireContext(), "Edad fuera de rango permitido (13 - 99 años)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val nuevosDatos = mapOf(
                    "nombre" to nombre,
                    "fechaNacimiento" to fecha,
                    "genero" to genero
                )

                docRef.update(nuevosDatos).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al actualizar datos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ivVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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
