package mx.edu.potros.gestioninventarios.ui.add_item

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController // Mantener findNavController
import com.bumptech.glide.Glide // Importar Glide
import mx.edu.potros.gestioninventarios.DAO.SubirImagenDAOCloudinary
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentAddItemBinding
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import mx.edu.potros.gestioninventarios.objetoNegocio.EntradasSalidas
import yuku.ambilwarna.AmbilWarnaDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val categoriasSeleccionadas = ArrayList<String>()
    private lateinit var textoCategoria: TextView

    private val PICK_IMAGE_REQUEST = 1
    private var imagenSeleccionadaUri: Uri? = null

    // --- Nuevas propiedades para el modo de edición ---
    private var isEditMode: Boolean = false
    private var isCurrentlyEditable: Boolean = false
    private var articleToEditId: String? = null
    private var originalImageUrl: String? = null
    private var originalQuantity: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this).get(AddItemViewModel::class.java)
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textoCategoria = binding.spinnerCategorAArtCulo

        binding.regresar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.llSelectCategoryRegisterArticle.setOnClickListener {
            if (isCurrentlyEditable) {
                mostrarDialogoCategorias()
            } else {
                Toast.makeText(requireContext(), "Presiona 'Editar Artículo' para cambiar la categoría.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.profileIcon.setOnClickListener {
            // Solo permitir cambiar imagen si los campos son editables
            if (isCurrentlyEditable) {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            } else {
                Toast.makeText(requireContext(), "Presiona 'Editar Artículo' para cambiar la imagen.", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Lógica para manejar el modo de edición/adición ---
        arguments?.let { bundle ->
            isEditMode = bundle.getBoolean("isEditMode", false)
            if (isEditMode) {
                articleToEditId = bundle.getString("idArticulo")
                val nombre = bundle.getString("nombre")
                val categoriaNombre = bundle.getString("categoria")
                val cantidad = bundle.getInt("cantidad")
                val descripcion = bundle.getString("descripcion")
                val colorCategoria = bundle.getString("color")
                val costo = bundle.getFloat("costo")
                val imagenUrl = bundle.getString("imagenUrl")

                originalImageUrl = imagenUrl
                originalQuantity = cantidad

                // Precargar los campos con los datos del artículo
                binding.nombreArticulo.setText(nombre)
                binding.cantidadArticulo.setText(cantidad.toString())
                binding.descripciNArticulo.setText(descripcion) // Usa el ID exacto que tienes
                binding.costo.setText(costo.toString())

                textoCategoria.text = categoriaNombre
                try {
                    textoCategoria.setTextColor(Color.parseColor(colorCategoria))
                } catch (e: Exception) {
                    textoCategoria.setTextColor(Color.BLACK)
                }
                textoCategoria.setTypeface(null, Typeface.BOLD)
                categoriasSeleccionadas.clear()
                categoriasSeleccionadas.add(categoriaNombre ?: "")

                if (!imagenUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imagenUrl.replace("http://", "https://"))
                        .placeholder(R.drawable.profileicon)
                        .error(R.drawable.profileicon)
                        .into(binding.profileIcon)
                } else {
                    binding.profileIcon.setImageResource(R.drawable.profileicon)
                }

                // En modo edición, los campos NO son editables inicialmente
                isCurrentlyEditable = false
                isEditable(false)
            } else {

                isEditMode = false
                isCurrentlyEditable = true // Los campos son editables por defecto
                isEditable(true) // Habilita los campos y establece el botón en "Guardar"
                binding.btnGuardarArt.text = "Guardar" // Asegurarse de que el texto inicial sea "Guardar"
            }
        } ?: run {
            isEditMode = false
            isCurrentlyEditable = true
            isEditable(true)
            binding.btnGuardarArt.text = "Guardar"
        }

        binding.btnGuardarArt.setOnClickListener {
            if (isEditMode) {
                // En modo edición, el botón "Guardar Artículo" actúa como un toggle
                if (isCurrentlyEditable) {
                    // Si los campos están editables, al pulsar el botón, se guardan los cambios
                    handleEditArticle()
                    // El estado de isCurrentlyEditable y el texto del botón se actualizarán en onSuccess/onFailure
                } else {
                    // Si los campos NO están editables, al pulsar el botón, se habilitan para editar
                    isCurrentlyEditable = true
                    isEditable(true) // Habilita los campos
                    binding.btnGuardarArt.text = "Guardar Cambios" // Cambia el texto para indicar que se van a guardar cambios
                }
            } else {
                // En modo de adición, el botón siempre guarda el nuevo artículo
                handleSaveArticle()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data?.data != null) {
            imagenSeleccionadaUri = data.data
            binding.profileIcon.setImageURI(imagenSeleccionadaUri)
        }
    }

    private fun handleSaveArticle() {
        val uri = imagenSeleccionadaUri

        if (uri == null) {
            Toast.makeText(requireContext(), "Selecciona una imagen primero", Toast.LENGTH_SHORT).show()
            return
        }

        SubirImagenDAOCloudinary.subirImagen(uri, requireContext()) { url ->
            if (url != null) {
                guardarArticuloEnFirestore(url)
            } else {
                Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- Nueva función para manejar la edición del artículo ---
    private fun handleEditArticle() {
        val newNombre = binding.nombreArticulo.text.toString().trim()
        val newCantidad = binding.cantidadArticulo.text.toString().toIntOrNull() ?: 0
        val newDescripcion = binding.descripciNArticulo.text.toString().trim()
        val newCosto = binding.costo.text.toString().toFloatOrNull() ?: 0f
        val newNombreCategoria = textoCategoria.text.toString()
        val newCategoria = DataProvider.listaCategorias.find { it.nombre == newNombreCategoria }

        if (newNombre.isBlank() || newNombreCategoria.isBlank() || newCantidad <= 0 || newDescripcion.isBlank() || newCosto <= 0f || newCategoria == null) {
            Toast.makeText(context, "Por favor, llena todos los campos obligatorios y asegúrate de que cantidad y costo sean mayores a 0", Toast.LENGTH_SHORT).show()
            return
        }

        val articleId = articleToEditId
        if (articleId.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Error: ID de artículo no encontrado para editar.", Toast.LENGTH_SHORT).show()
            return
        }

        // Si se seleccionó una nueva imagen, subirla primero
        if (imagenSeleccionadaUri != null) {
            SubirImagenDAOCloudinary.subirImagen(imagenSeleccionadaUri!!, requireContext()) { imageUrl ->
                if (imageUrl != null) {
                    performArticleUpdate(articleId, newNombre, newCantidad, newDescripcion, newCosto, newCategoria, imageUrl)
                } else {
                    Toast.makeText(requireContext(), "Error al subir la nueva imagen", Toast.LENGTH_SHORT).show()
                    // Mantener campos editables si falla la subida de imagen
                    isCurrentlyEditable = true
                    isEditable(true)
                    binding.btnGuardarArt.text = "Guardar Cambios"
                }
            }
        } else {
            // Si no se seleccionó nueva imagen, usar la URL original
            val finalImageUrl = originalImageUrl ?: ""
            performArticleUpdate(articleId, newNombre, newCantidad, newDescripcion, newCosto, newCategoria, finalImageUrl)
        }
    }

    // --- Función que realiza la actualización final en Firestore ---
    private fun performArticleUpdate(
        articleId: String,
        nombre: String,
        cantidad: Int,
        descripcion: String,
        costo: Float,
        categoria: Categoria,
        imageUrl: String
    ) {
        val updatedArticulo = Articulo(
            idArticulo = articleId,
            imagenUrl = imageUrl,
            nombre = nombre,
            cantidad = cantidad,
            categoria = categoria,
            descripcion = descripcion,
            costo = costo
        )

        DataProvider.articuloDAO.editarArticulo(updatedArticulo,
            onSuccess = {
                Toast.makeText(requireContext(), "Artículo actualizado correctamente", Toast.LENGTH_SHORT).show()

                // Calcular y registrar movimiento de stock si la cantidad ha cambiado
                val difference = cantidad - originalQuantity
                if (difference != 0) {
                    val tipoMovimiento = if (difference > 0) "Entrada por Edición" else "Salida por Edición"
                    val esEntrada = difference > 0
                    val movimiento = EntradasSalidas(
                        "",
                        updatedArticulo,
                        Math.abs(difference), // Cantidad absoluta del movimiento
                        SimpleDateFormat("dd-MM-yyyy:HH-mm", Locale.getDefault()).format(Date()),
                        tipoMovimiento,
                        esEntrada
                    )
                    DataProvider.entradasSalidasDAO.guardarEntraSal(movimiento,
                        onSuccess = {
                            Log.d("Movement", "Movimiento de edición registrado correctamente.")
                        },
                        onFailure = { error ->
                            Log.e("Movement", "Error al registrar movimiento de edición: ${error.message}")
                        }
                    )
                }

                // Después de guardar exitosamente, deshabilitar campos y cambiar el botón
                isCurrentlyEditable = false
                isEditable(false) // Esto pondrá el texto del botón en "Editar Artículo"

                // Navegar de vuelta a la pantalla de detalle con los datos actualizados
                Handler(Looper.getMainLooper()).postDelayed({
                    val bundle = Bundle().apply {
                        putString("nombre", updatedArticulo.nombre)
                        putString("categoria", updatedArticulo.categoria.nombre)
                        putInt("cantidad", updatedArticulo.cantidad)
                        putString("descripcion", updatedArticulo.descripcion)
                        putString("color", updatedArticulo.categoria.color)
                        putString("imagenUrl", updatedArticulo.imagenUrl)
                        putFloat("costo", updatedArticulo.costo)
                        putString("idArticulo", updatedArticulo.idArticulo)
                    }
                    findNavController().navigate(R.id.detailProduct, bundle)
                    DataProvider.cargarDatos() // Recargar datos para mantenerlos actualizados en DataProvider
                }, 500)
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "Error al actualizar el artículo: ${error.message}", Toast.LENGTH_SHORT).show()
                // Si falla la actualización, mantener campos editables para que el usuario pueda corregir
                isCurrentlyEditable = true
                isEditable(true)
                binding.btnGuardarArt.text = "Guardar Cambios"
            }
        )
    }

    private fun guardarArticuloEnFirestore(imagenUrl: String) {
        val nombre = binding.nombreArticulo.text.toString().trim()
        val cantidad = binding.cantidadArticulo.text.toString().toIntOrNull() ?: 0
        val descripcion = binding.descripciNArticulo.text.toString().trim()
        val costo = binding.costo.text.toString().toFloatOrNull() ?: 0f
        val nombreCategoria = textoCategoria.text.toString()
        val categoria = DataProvider.listaCategorias.find { it.nombre == nombreCategoria }

        if (nombre.isBlank() || nombreCategoria.isBlank() || cantidad <= 0 || descripcion.isBlank() || costo <= 0f || categoria == null) {
            Toast.makeText(context, "Por favor, llena todos los campos obligatorios y asegúrate de que cantidad y costo sean mayores a 0", Toast.LENGTH_SHORT).show()
            return
        }

        val articulo = Articulo(
            imagenUrl = imagenUrl,
            nombre = nombre,
            cantidad = cantidad,
            categoria = categoria,
            descripcion = descripcion,
            costo = costo
        )

        DataProvider.articuloDAO.guardarArticulo(articulo,
            onSuccess = {
                val entrada = EntradasSalidas(
                    "",
                    articulo,
                    cantidad,
                    SimpleDateFormat("dd-MM-yyyy:HH-mm", Locale.getDefault()).format(Date()),
                    "Registro",
                    true
                )
                DataProvider.entradasSalidasDAO.guardarEntraSal(entrada,
                    onSuccess = {
                        Handler(Looper.getMainLooper()).postDelayed({
                            val bundle = Bundle().apply {
                                putString("nombre", articulo.nombre)
                                putString("categoria", articulo.categoria.nombre)
                                putInt("cantidad", articulo.cantidad)
                                putString("descripcion", articulo.descripcion)
                                putString("color", articulo.categoria.color)
                                putString("imagenUrl", articulo.imagenUrl)
                                putFloat("costo", articulo.costo)
                                putString("idArticulo", articulo.idArticulo)
                            }
                            findNavController().navigate(R.id.detailProduct, bundle)
                            DataProvider.cargarDatos()
                        }, 500)
                    },
                    onFailure = { error ->
                        Log.e("Entrada", "Fallo al guardar entrada de registro: ${error.message}")
                    })
                Toast.makeText(requireContext(), "Artículo guardado correctamente", Toast.LENGTH_SHORT).show()
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "Error al guardar el artículo: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // --- Implementación de la función isEditable para controlar los campos ---
    private fun isEditable(accion: Boolean) {
        // Asegúrate de que los IDs aquí coincidan con tus elementos en fragment_add_item.xml
        binding.llSelectCategoryRegisterArticle.isClickable = accion // Para el LinearLayout de categoría
        binding.nombreArticulo.isEnabled = accion
        binding.cantidadArticulo.isEnabled = accion
        binding.descripciNArticulo.isEnabled = accion // Usando el ID exacto que tienes
        binding.costo.isEnabled = accion
        binding.profileIcon.isClickable = accion // Para el ImageView de la imagen

        // Cambiar el texto del botón principal (btnGuardarArt) basado en si es editable o no
        if (isEditMode) { // Solo si estamos en el "modo general de edición" (no en modo de adición)
            if (accion) {
                binding.btnGuardarArt.text = "Guardar Cambios" // Si es editable, el botón guarda los cambios
            } else {
                binding.btnGuardarArt.text = "Editar Artículo" // Si no es editable, el botón habilita la edición
            }
        } else {
            binding.btnGuardarArt.text = "Guardar" // En modo de adición, siempre dice "Guardar"
        }
    }


    private fun mostrarDialogoCategorias() {
        val context = requireContext()
        val seleccionActual = categoriasSeleccionadas.firstOrNull()
        var categoriaSeleccionada: String? = seleccionActual // Usar nullable para el caso inicial

        val radioGroup = RadioGroup(context).apply {
            orientation = RadioGroup.VERTICAL
            setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20))
        }

        DataProvider.listaCategorias.forEach { categoria ->
            val radioButton = RadioButton(context).apply {
                text = categoria.nombre
                textSize = 18f
                setPadding(20, 20, 20, 20)
                setTextColor(Color.parseColor(categoria.color))
                setTypeface(null, Typeface.BOLD)
                isChecked = categoria.nombre == seleccionActual
            }
            radioGroup.addView(radioButton)
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio = group.findViewById<RadioButton>(checkedId)
            categoriaSeleccionada = radio.text.toString()
        }

        val scrollView = ScrollView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(250)
            )
            addView(radioGroup)
        }

        val contenedor = FrameLayout(context).apply {
            addView(scrollView)
        }

        AlertDialog.Builder(context)
            .setTitle("Selecciona una Categoría")
            .setView(contenedor)
            .setPositiveButton("Aplicar") { _, _ ->
                categoriaSeleccionada?.let {
                    categoriasSeleccionadas.clear()
                    categoriasSeleccionadas.add(it)
                    textoCategoria.text = it

                    val color = DataProvider.listaCategorias.find { cat -> cat.nombre == it }?.color
                    textoCategoria.setTextColor(Color.parseColor(color ?: "#000000"))
                    textoCategoria.setTypeface(null, Typeface.BOLD)
                }
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Agregar Categoría") { _, _ ->
                mostrarDialogoAgregarCategoria()
            }
            .show()
    }

    private fun mostrarDialogoAgregarCategoria() {
        val context = requireContext()

        val dialogLayout = ScrollView(context)
        val innerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val inputNombre = EditText(context).apply {
            hint = "Nombre de la categoría"
        }

        val colorSeleccionado = arrayOf("#FF0000")
        var colorCambiado = false

        val btnColor = Button(context)

        btnColor.apply {
            text = "Seleccionar color"
            setBackgroundColor(Color.parseColor(colorSeleccionado[0]))
            setOnClickListener {
                val initialColor = Color.parseColor(colorSeleccionado[0])
                AmbilWarnaDialog(context, initialColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onCancel(dialog: AmbilWarnaDialog?) {}

                    override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                        val nuevoColor = String.format("#%06X", 0xFFFFFF and color)
                        if (nuevoColor != "#FF0000") colorCambiado = true
                        colorSeleccionado[0] = nuevoColor
                        btnColor.setBackgroundColor(Color.parseColor(nuevoColor))
                    }
                }).show()
            }
        }

        innerLayout.addView(inputNombre)
        innerLayout.addView(TextView(context).apply {
            text = "Color de la categoría:"
            setPadding(0, 20, 0, 10)
        })
        innerLayout.addView(btnColor)
        dialogLayout.addView(innerLayout)

        val alerta = AlertDialog.Builder(context)
            .setTitle("Agregar Nueva Categoría")
            .setView(dialogLayout)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        alerta.setOnShowListener {
            val botonGuardar = alerta.getButton(AlertDialog.BUTTON_POSITIVE)
            botonGuardar.setOnClickListener {
                val nombre = inputNombre.text.toString().trim()

                if (nombre.isEmpty()) {
                    Toast.makeText(context, "Nombre no válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!colorCambiado) {
                    Toast.makeText(context, "Debes seleccionar un color distinto", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val categoria = Categoria("", nombre, colorSeleccionado[0])

                Log.d("Usuario" , DataProvider.usuarioId)

                DataProvider.categoriaDAO.guardarCategoria(
                    categoria,
                    onSuccess = {
                        DataProvider.cargarDatos()
                        Handler(Looper.getMainLooper()).postDelayed({
                            Toast.makeText(context, "Categoría guardada correctamente", Toast.LENGTH_SHORT).show()
                            alerta.dismiss()
                            mostrarDialogoCategorias()
                        }, 500)
                    },
                    onFailure = { error -> // Capturar el error para mostrar un mensaje más útil
                        Toast.makeText(context, "Error al guardar la categoría: ${error.message}", Toast.LENGTH_SHORT).show()
                        Log.e("GuardarCategoria", "Error: ${error.message}", error)
                    }
                )
            }
        }
        alerta.show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}