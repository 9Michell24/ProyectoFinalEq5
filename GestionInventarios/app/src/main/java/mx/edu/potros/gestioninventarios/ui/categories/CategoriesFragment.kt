package mx.edu.potros.gestioninventarios.ui.categories

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider.listaEntradasSalidas
import mx.edu.potros.gestioninventarios.objetoNegocio.EntradasSalidas
import yuku.ambilwarna.AmbilWarnaDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CategoriesFragment : Fragment() {

    lateinit var txtCategoria : TextView
    lateinit var lineCategoria: View

    lateinit var listaEntradas : ArrayList<EntradasSalidas>

    var totalArticles : Int? = 0


    companion object {
        fun newInstance() = CategoriesFragment()
    }

    private val viewModel: CategoriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View {

        return inflater.inflate(R.layout.fragment_categories, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pos = arguments?.getInt("position")
        totalArticles = arguments?.getInt("totalArticles")

        txtCategoria = view.findViewById(R.id.by_category_category)
        lineCategoria = view.findViewById(R.id.by_category_line)

        val ivVolver: ImageView = view.findViewById(R.id.iv_back_categories)
        val imEditar: ImageView = view.findViewById(R.id.iv_category_edit)
        val imBorrar: ImageView = view.findViewById(R.id.iv_erase_category)



        imEditar.setOnClickListener {

            if(pos != null){
                mostrarDialogoAgregarCategoria(pos)
            }

        }

        imBorrar.setOnClickListener{

            for(e in DataProvider.listaArticulos){
                if(e.categoria.idCategoria.equals(DataProvider.listaCategorias[pos!!].idCategoria)){
                    Toast.makeText(requireContext(), "La categoria tiene productos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }


            mostrarDialogoConfirmacionEliminar {

                DataProvider.categoriaDAO.eliminarCategoria(
                    DataProvider.listaCategorias[pos!!].idCategoria,
                    onSuccess = {

                        Toast.makeText(requireContext(), "Se elimino", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "No se elimino :c", Toast.LENGTH_SHORT)
                            .show()
                    }
                )

            }


        }




        ivVolver.setOnClickListener {

            //popBackStack es para volver al fragment anterior
            findNavController().popBackStack()

        }


        listaEntradas = arguments?.getParcelableArrayList<EntradasSalidas>("listaEntradas") as ArrayList<EntradasSalidas>

        var datos: ArrayList<String> = arrayListOf()


        if (pos != null) {

            txtCategoria.setTextColor(Color.parseColor(DataProvider.listaCategorias[pos].color))
            txtCategoria.setText(DataProvider.listaCategorias[pos].nombre + " ( ${totalArticles} )")
            lineCategoria.setBackgroundColor(Color.parseColor(DataProvider.listaCategorias[pos].color))


            for (e in DataProvider.listaEntradasSalidas) {
                if (e.articulo.categoria.nombre.equals(DataProvider.listaCategorias[pos].nombre)) {
                    if (e.isEntrada) {
                        datos.add("+ " + e.cantidad + " " + e.articulo.nombre + " - " + e.fecha)
                    } else {
                        datos.add("- " + e.cantidad + " " + e.articulo.nombre + " - " + e.fecha)
                    }
                }
            }
        }

        val adaptador = object : BaseAdapter() {

            val formato = SimpleDateFormat("dd-MM-yyyy:HH-mm", Locale.getDefault())

            val listaOrdenada = listaEntradas.sortedBy {
                try {
                    formato.parse(it.fecha)
                } catch (e: Exception) {
                    Date(0) // Fecha antigua si no se puede parsear
                }
            }


            override fun getCount(): Int = listaOrdenada?.size ?: 0

            override fun getItem(position: Int): EntradasSalidas = listaOrdenada?.get(position) ?: EntradasSalidas()

            override fun getItemId(position: Int): Long = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(requireContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false)

                val textView = view.findViewById<TextView>(android.R.id.text1)
                val entrada = getItem(position)



                val texto = if (entrada.isEntrada) {
                    "+ ${entrada.cantidad} ${entrada.articulo.nombre}" + " - " + entrada.fecha


                } else {
                    "- ${entrada.cantidad} ${entrada.articulo.nombre}" + " - " + entrada.fecha
                }

                textView.text = texto
                textView.textSize = 22f
                textView.setTypeface(null, Typeface.BOLD)

                val color = if (entrada.isEntrada)
                    ContextCompat.getColor(requireContext(), R.color.entrada_texto)
                else
                    ContextCompat.getColor(requireContext(), R.color.salida_texto)

                textView.setTextColor(color)

                var listaIdArticulos = ArrayList<String>()

                for(e in DataProvider.listaArticulos){
                    listaIdArticulos.add(e.idArticulo)
                }


                view.setOnClickListener {
                    if(!listaIdArticulos.contains(entrada.articulo.idArticulo)){
                        Toast.makeText(requireContext(), "Este articulo fue eliminado", Toast.LENGTH_SHORT).show()
                    }
                    else{

                    var cantidadFinal = 0

                    for (e in DataProvider.listaEntradasSalidas) {
                        if (e.articulo.idArticulo.equals(entrada.articulo.idArticulo)) {
                            if (e.isEntrada) {
                                cantidadFinal += e.cantidad
                            } else {
                                cantidadFinal -= e.cantidad
                            }
                        }
                    }


                    val bundle = Bundle().apply {
                        putString("nombre", entrada.articulo.nombre)
                        putString("categoria", entrada.articulo.categoria.nombre)
                        putInt("cantidad", cantidadFinal)
                        putString("descripcion", entrada.articulo.descripcion)
                        putString("color", entrada.articulo.categoria.color)
                        putString("imagenUrl", entrada.articulo.imagenUrl)
                        putFloat("costo", entrada.articulo.costo)
                        putString("idArticulo", entrada.articulo.idArticulo)
                    }

                    findNavController().navigate(R.id.detailProduct, bundle)
                }
                    }

                return view
            }
        }


        val gridView = view.findViewById<GridView>(R.id.grid_view_by_categories)
        gridView.adapter = adaptador

    }


    private fun mostrarDialogoAgregarCategoria(pos : Int) {
        val context = requireContext()

        val dialogLayout = ScrollView(context)
        val innerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val inputNombre = EditText(context).apply {
            hint = "Nombre de la categoría"
            setText(DataProvider.listaCategorias[pos].nombre)
        }

        val colorSeleccionado = arrayOf(DataProvider.listaCategorias[pos].color)

        val btnColor = Button(context) // Primero lo declaras

        btnColor.apply {
            text = "Seleccionar color"
            setBackgroundColor(Color.parseColor(colorSeleccionado[0]))
            setOnClickListener {
                val initialColor = Color.parseColor(colorSeleccionado[0])
                AmbilWarnaDialog(
                    context,
                    initialColor,
                    object : AmbilWarnaDialog.OnAmbilWarnaListener {
                        override fun onCancel(dialog: AmbilWarnaDialog?) {}

                        override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                            val nuevoColor = String.format("#%06X", 0xFFFFFF and color)
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
            .setTitle("Editar Categoría")
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

                DataProvider.listaCategorias.forEach { categoria ->
                    if (categoria.nombre.equals(nombre)) {
                        Toast.makeText(context, "ese nombre ya esta en uso", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                }


                val categoria = Categoria(DataProvider.listaCategorias[pos].idCategoria, nombre, colorSeleccionado[0])

                Log.d("Usuario", DataProvider.usuarioId)

                DataProvider.categoriaDAO.editarCategoria(
                    categoria,
                    onSuccess = {
                        DataProvider.cargarDatos()

                        Handler(Looper.getMainLooper()).postDelayed({
                            Toast.makeText(
                                context,
                                "Categoría Editada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            txtCategoria.setText(categoria.nombre + " ( ${totalArticles} )")
                            txtCategoria.setTextColor(Color.parseColor(categoria.color))
                            lineCategoria.setBackgroundColor(Color.parseColor(categoria.color))

                            alerta.dismiss()
                        }, 500)

                    },
                    onFailure = {
                        Toast.makeText(context, "Error al editar la categoría", Toast.LENGTH_SHORT).show()

                    }
                )


            }
        }

        alerta.show()

    }



    private fun eliminar(pos : Int){

        DataProvider.categoriaDAO.eliminarCategoria(DataProvider.listaCategorias[pos].idCategoria,
            onSuccess = {Toast.makeText(context, "Categoria eliminada", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        },
            onFailure = {Toast.makeText(context, "Error al eliminar la categoría", Toast.LENGTH_SHORT).show() }
        )

    }



    private fun mostrarDialogoConfirmacionEliminar(onConfirmar: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("¿Eliminar elemento?")
            .setMessage("¿Estás seguro de que deseas eliminar ${txtCategoria.text}? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { dialog, _ ->
                onConfirmar()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }





}