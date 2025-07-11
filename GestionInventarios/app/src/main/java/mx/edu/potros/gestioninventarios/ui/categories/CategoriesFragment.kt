package mx.edu.potros.gestioninventarios.ui.categories

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import mx.edu.potros.gestioninventarios.objetoNegocio.EntradasSalidas

class CategoriesFragment : Fragment() {

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

        val ivVolver: ImageView = view.findViewById(R.id.iv_back_categories)

        ivVolver.setOnClickListener {

            //popBackStack es para volver al fragment anterior
            findNavController().popBackStack()

        }

        val pos = arguments?.getInt("position")

        var datos : ArrayList<String> = arrayListOf()


        if(pos != null) {

            view.findViewById<TextView>(R.id.by_category_category).setTextColor(Color.parseColor(DataProvider.listaCategorias[pos].color))
            view.findViewById<View>(R.id.by_category_line).setBackgroundColor(Color.parseColor(DataProvider.listaCategorias[pos].color))
            view.findViewById<TextView>(R.id.by_category_category).setText(DataProvider.listaCategorias[pos].nombre)


            for (e in DataProvider.listaEntradasSalidas) {
                if (e.articulo.categoria.nombre.equals(DataProvider.listaCategorias[pos].nombre)){
                    if(e.isEntrada){
                        datos.add("+ " + e.cantidad + " " + e.articulo.nombre)
                    }
                    else{
                        datos.add("- " + e.cantidad + " " + e.articulo.nombre)
                    }
                }
            }
        }

        val adaptador = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, datos) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)

                textView.textSize = 22f
                textView.setTypeface(null, Typeface.BOLD)

                val texto = getItem(position)
                if (texto != null) {
                    if (texto.startsWith("+")) {
                        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.entrada_texto))
                    } else if (texto.startsWith("-")) {
                        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.salida_texto))
                    }
                }

                return view
            }
        }


        val gridView = view.findViewById<GridView>(R.id.grid_view_by_categories)
        gridView.adapter = adaptador

    }


<<<<<<< Updated upstream
=======
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
                        Toast.makeText(context, "Ese nombre ya existe", Toast.LENGTH_SHORT).show()
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


>>>>>>> Stashed changes


//    private class AdaptadorByCategories : BaseAdapter {
//
//        var entradasSalidas = ArrayList<EntradasSalidas>()
//        var contexto: Context? = null
//
//        constructor(contexto: Context, entradasSalidas : ArrayList<EntradasSalidas>){
//            this.contexto = contexto
//            this.entradasSalidas = entradasSalidas
//
//        }
//
//        override fun getCount(): Int {
//            return entradasSalidas.size
//        }
//
//        override fun getItem(position: Int): Any {
//            return entradasSalidas[position]
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//            var entradasSalida = entradasSalidas[position]
//            var inflador = LayoutInflater.from(contexto)
////            var vista = inflador.inflate(R.layout.design_category_home_list, null)
////
////            var tv_title: TextView = vista.findViewById(R.id.tv_category_name_home)
////            var tv_number: TextView = vista.findViewById(R.id.tv_category_number_home)
////            val fondo: LinearLayout = vista.findViewById(R.id.fondo_lista_categorias_home)
//
//
//
//            tv_title.setText(categoria.nombre)
//            tv_number.setText(contador.toString())
//
//
//            val color = Color.parseColor(categoria.color)
//
//            val drawable = GradientDrawable()
//            drawable.cornerRadius = 40f
//            drawable.setColor(color)
//
//            fondo.background = drawable
//
//
//            return vista
//
//
//        }
//    }

}