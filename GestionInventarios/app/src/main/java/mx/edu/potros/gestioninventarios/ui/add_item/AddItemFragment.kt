package mx.edu.potros.gestioninventarios.ui.add_item

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
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
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider

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
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val seleccionActual = categoriasSeleccionadas.firstOrNull()
        var categoriaSeleccionada: String? = seleccionActual
        var vistaSeleccionada: TextView? = null

        DataProvider.listaCategorias.forEach { categoria ->
            val textView = TextView(requireContext()).apply {
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

            layout.addView(textView)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona una Categoría")
            .setView(ScrollView(requireContext()).apply { addView(layout) })
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
            .show()
    }
}
