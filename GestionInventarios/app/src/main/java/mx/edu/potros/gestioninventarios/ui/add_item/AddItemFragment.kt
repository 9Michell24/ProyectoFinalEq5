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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
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
import kotlin.concurrent.thread


class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val categoriasSeleccionadas = ArrayList<String>()
    private lateinit var textoCategoria: TextView

    private val PICK_IMAGE_REQUEST = 1
    private var imagenSeleccionadaUri: Uri? = null




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





        textoCategoria = view.findViewById(R.id.spinnerCategoríaArtículo)

        view.findViewById<ImageView>(R.id.regresar).setOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<LinearLayout>(R.id.ll_select_category_register_article).setOnClickListener {
            mostrarDialogoCategorias()
        }

        binding.profileIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }




        binding.btnGuardarArt.setOnClickListener {

            val uri = imagenSeleccionadaUri

            if (uri == null) {
                Toast.makeText(requireContext(), "Selecciona una imagen primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            SubirImagenDAOCloudinary.subirImagen(uri, requireContext()) { url ->
                if (url != null) {
                    guardarArticuloEnFirestore(url)
                } else {
                    Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }
            }

        }




    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data?.data != null) {
            imagenSeleccionadaUri = data.data // 👈 Guardas el Uri
            binding.profileIcon.setImageURI(imagenSeleccionadaUri) // Muestras en ImageView
        }
    }




    private fun guardarArticuloEnFirestore(imagenUrl: String) {

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
            imagenUrl = imagenUrl,
            nombre = nombre,
            cantidad = cantidad,
            categoria = categoria,
            descripcion = descripcion,
            costo = costo
        )


        DataProvider.articuloDAO.guardarArticulo(articulo,
            onSuccess = {
                EntradasSalidas()
                var entrada = EntradasSalidas(
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
                    onFailure = {
                        Log.d("Entrada", "Fallo")
                    })
                Toast.makeText(requireContext(), "Artículo guardado correctamente", Toast.LENGTH_SHORT).show()
            },
            onFailure = {
                Toast.makeText(requireContext(), "Error al guardar el artículo", Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun mostrarDialogoCategorias() {
        val context = requireContext()
        val seleccionActual = categoriasSeleccionadas.firstOrNull()
        var categoriaSeleccionada = seleccionActual

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

        val btnColor = Button(context) // Primero lo declaras

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
                        btnColor.setBackgroundColor(Color.parseColor(nuevoColor)) // ✅ ACTUALIZA VISUALMENTE
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
                    onFailure = {
                        Toast.makeText(context, "Error al guardar la categoría", Toast.LENGTH_SHORT).show()
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
