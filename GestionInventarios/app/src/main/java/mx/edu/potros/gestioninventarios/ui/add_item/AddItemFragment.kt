package mx.edu.potros.gestioninventarios.ui.add_item

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentAddItemBinding
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import yuku.ambilwarna.AmbilWarnaDialog

class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val categoriasSeleccionadas = ArrayList<String>()
    private lateinit var textoCategoria: TextView

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

        val ivVolver: ImageView = view.findViewById(R.id.regresar)
        val seleccionarCategoria: LinearLayout = view.findViewById(R.id.ll_select_category_register_article)
        textoCategoria = view.findViewById(R.id.spinnerCategoríaArtículo)

        seleccionarCategoria.setOnClickListener {
            mostrarDialogoCategorias()
        }

        ivVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnGuardarArt.setOnClickListener {
            guardarArticuloEnFirestore()
        }
    }

    private fun guardarArticuloEnFirestore() {
        val nombre = binding.nombreArticulo.text.toString().trim()
        val cantidad = binding.cantidadArticulo.text.toString().toIntOrNull() ?: 0
        val descripcion = binding.descripciNArticulo.text.toString().trim()
        val costo = binding.costo.text.toString().toFloatOrNull() ?: 0f
        val nombreCategoria = textoCategoria.text.toString()

        val categoria = DataProvider.listaCategorias.find { it.nombre == nombreCategoria }

        if (nombre.isBlank() || nombreCategoria.isBlank() || cantidad == 0 || descripcion.isBlank() || costo == 0f || categoria == null) {
            Toast.makeText(context, "Por favor llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val articulo = Articulo(
            nombre = nombre,
            cantidad = cantidad,
            categoria = categoria,
            descripcion = descripcion,
            costo = costo
        )

        val db = Firebase.firestore
        db.collection("articulos")
            .add(articulo)
            .addOnSuccessListener {
                Toast.makeText(context, "Artículo guardado correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.detailProduct)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar el artículo", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoCategorias() {
        val context = requireContext()

        // Contenedor limitado en altura
        val contenedor = FrameLayout(context).apply {
            val maxHeight = dpToPx(100)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                maxHeight
            )
            setPadding(20, 20, 20, 20)
        }

        val layoutInterno = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val seleccionActual = categoriasSeleccionadas.firstOrNull()
        var categoriaSeleccionada: String? = seleccionActual
        var vistaSeleccionada: TextView? = null

        DataProvider.listaCategorias.forEach { categoria ->
            val textView = TextView(context).apply {
                text = categoria.nombre
                textSize = 18f
                setPadding(20, 20, 20, 20)
                setTextColor(Color.parseColor(categoria.color))
                setTypeface(null, Typeface.BOLD)
                setBackgroundColor(
                    if (categoria.nombre == seleccionActual) Color.LTGRAY else Color.TRANSPARENT
                )

                if (categoria.nombre == seleccionActual) {
                    vistaSeleccionada = this
                }

                setOnClickListener {
                    vistaSeleccionada?.setBackgroundColor(Color.TRANSPARENT)
                    setBackgroundColor(Color.LTGRAY)
                    categoriaSeleccionada = categoria.nombre
                    vistaSeleccionada = this
                }
            }

            layoutInterno.addView(textView)
        }

        val scrollView = ScrollView(context).apply {
            isFillViewport = true
            addView(layoutInterno)
        }

        contenedor.addView(scrollView)

        AlertDialog.Builder(context)
            .setTitle("Selecciona una Categoría")
            .setView(contenedor)
            .setPositiveButton("Aplicar") { _, _ ->
                categoriaSeleccionada?.let {
                    categoriasSeleccionadas.clear()
                    categoriasSeleccionadas.add(it)
                    textoCategoria.text = it

                    val categoriaColor = DataProvider.listaCategorias.find { cat -> cat.nombre == it }?.color
                    textoCategoria.setTextColor(Color.parseColor(categoriaColor ?: "#000000"))
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

        val dialogLayout = ScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            isFillViewport = true
        }

        val innerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val inputNombre = EditText(context).apply {
            hint = "Nombre de la categoría"
        }

        val btnColor = Button(context).apply {
            text = "Seleccionar color"
            setBackgroundColor(Color.LTGRAY) // Color por defecto
        }

        val colorSeleccionado = arrayOf("#FF0000") // valor real
        var colorCambiado = false // Para saber si el usuario cambió el color

        btnColor.setOnClickListener {
            val initialColor = Color.parseColor(colorSeleccionado[0])
            AmbilWarnaDialog(context, initialColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    // nada
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    val nuevoColor = String.format("#%06X", 0xFFFFFF and color)
                    if (nuevoColor != "#FF0000") colorCambiado = true
                    colorSeleccionado[0] = nuevoColor
                    btnColor.setBackgroundColor(color)
                }
            }).show()
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
            .setPositiveButton("Guardar", null) // Lo configuramos manualmente después
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

                DataProvider.listaCategorias.add(
                    Categoria(nombre = nombre, color = colorSeleccionado[0])
                )
                alerta.dismiss()
                mostrarDialogoCategorias() // Vuelve al diálogo principal
            }
        }

        alerta.show()
    }


    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }


}
