package mx.edu.potros.gestioninventarios.ui.config

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import mx.edu.potros.gestioninventarios.DAO.SubirImagenDAOCloudinary
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.activities.ContraseniaActivity1
import mx.edu.potros.gestioninventarios.activities.LoginActivity
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import mx.edu.potros.gestioninventarios.objetoNegocio.Usuario
import java.util.*

class ConfigFragment : Fragment() {

    lateinit var nombreUsuario: EditText
    lateinit var fechaNacimiento: EditText
    lateinit var spinnerConfig: Spinner
    lateinit var btnGuardar: Button

    var editable: Boolean = false

    companion object {
        fun newInstance() = ConfigFragment()
    }

    private val viewModel: ConfigViewModel by viewModels()
    private val PICK_IMAGE_REQUEST = 1001
    private var imagenSeleccionadaUri: Uri? = null
    private lateinit var profileIcon: ImageView
    private var currentImageUrl: String? = null
    private var imagenRecienSeleccionada = false  // 👈 Nueva bandera

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivVolver: ImageView = view.findViewById(R.id.regresar)
        nombreUsuario = view.findViewById(R.id.nombreUsuario)
        fechaNacimiento = view.findViewById(R.id.fechaNacimiento)
        spinnerConfig = view.findViewById(R.id.spinnerConfig)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        val btnCerrarSesion: Button = view.findViewById(R.id.btnLogout)
        val textClick: TextView = view.findViewById(R.id.textClick)
        profileIcon = view.findViewById(R.id.profileIcon)

        profileIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        isEditable(editable)

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.opciones_spinner2,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerConfig.adapter = adapter

        fechaNacimiento.setOnClickListener {
            val partesFecha = fechaNacimiento.text.toString().split("/")
            val dia = partesFecha.getOrNull(0)?.toIntOrNull() ?: 1
            val mes = partesFecha.getOrNull(1)?.toIntOrNull()?.minus(1) ?: 0
            val anio = partesFecha.getOrNull(2)?.toIntOrNull() ?: 2000

            val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)

                val edad = calcularEdad(fechaFormateada)
                when {
                    edad < 13 -> Toast.makeText(requireContext(), "Debes tener al menos 13 años", Toast.LENGTH_SHORT).show()
                    edad > 99 -> Toast.makeText(requireContext(), "La edad máxima permitida es 99 años", Toast.LENGTH_SHORT).show()
                    else -> fechaNacimiento.setText(fechaFormateada)
                }
            }, anio, mes, dia)

            datePicker.datePicker.maxDate = Calendar.getInstance().timeInMillis
            datePicker.show()
        }

        DataProvider.obtenerDatosUsuario(
            onSuccess = { usuario ->
                if (usuario != null) {
                    nombreUsuario.setText(usuario.nombre)
                    fechaNacimiento.setText(usuario.nacimiento)
                    currentImageUrl = usuario.direccion_foto

                    val index = adapter.getPosition(usuario.genero)
                    if (index >= 0) {
                        spinnerConfig.setSelection(index)
                    }

                    if (!currentImageUrl.isNullOrBlank() && !imagenRecienSeleccionada) {
                        Glide.with(this)
                            .load(currentImageUrl)
                            .placeholder(R.drawable.profilepic)
                            .into(profileIcon)
                    }
                } else {
                    Toast.makeText(requireContext(), "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { e ->
                Toast.makeText(requireContext(), "Error al cargar datos del usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )

        btnGuardar.setOnClickListener {

            if (!editable) {
                isEditable(true)
            } else {

                val nombre = nombreUsuario.text.toString().trim()
                val fecha = fechaNacimiento.text.toString().trim()
                val genero = spinnerConfig.selectedItem.toString()
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val email = FirebaseAuth.getInstance().currentUser?.email ?: ""

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

                fun guardarDatos(url: String? = null) {
                    val usuarioActualizado = Usuario(
                        idUsuario = uid,
                        nombre = nombre,
                        correo = email,
                        nacimiento = fecha,
                        genero = genero,
                        contra = null,
                        direccion_foto = url ?: currentImageUrl,
                        listaCategoria = arrayListOf(),
                        ListaArticulo = arrayListOf(),
                        listaEntradasSalidas = arrayListOf()
                    )

                    DataProvider.editarDatosUsuario(
                        usuarioActualizado,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                            imagenRecienSeleccionada = false  // 👈 Resetear bandera después de guardar
                        },
                        onFailure = { e ->
                            Toast.makeText(requireContext(), "Error al actualizar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                val uri = imagenSeleccionadaUri
                if (uri != null) {
                    SubirImagenDAOCloudinary.subirImagen(uri, requireContext()) { url ->
                        if (url != null) {
                            guardarDatos(url)
                        } else {
                            Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    guardarDatos()
                }

                editable = false
                isEditable(editable)
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
            DataProvider.limpiarDatos()
        }

        textClick.setOnClickListener {
            val intent = Intent(requireContext(), ContraseniaActivity1::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data?.data != null) {
            imagenSeleccionadaUri = data.data
            imagenRecienSeleccionada = true  // 👈 Marcamos que ya seleccionó una imagen
            profileIcon.setImageURI(imagenSeleccionadaUri)
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

    private fun isEditable(accion: Boolean) {
        if (accion) {
            spinnerConfig.isEnabled = true
            nombreUsuario.isEnabled = true
            fechaNacimiento.isEnabled = true
            editable = true
            btnGuardar.text = "Guardar"
        } else {
            spinnerConfig.isEnabled = false
            nombreUsuario.isEnabled = false
            fechaNacimiento.isEnabled = false
            btnGuardar.text = "Actualizar Datos"
        }
    }
}
