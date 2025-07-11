package mx.edu.potros.gestioninventarios.ui.all_articles

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.potros.gestioninventarios.DAO.ArticuloDAOFirestore
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider.articulosActuales
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider.listaEntradasSalidas

class All_articlesFragment : Fragment() {


    private var adaptador: AdaptadorListAllArticles? = null

    var categoriasSeleccionadas = ArrayList<String>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_all_articles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivVolver: ImageView = view.findViewById(R.id.iv_back_all_articles)
        val tvAgregarArticulo: TextView = view.findViewById(R.id.agregarArticulo)
        val ivSearch : ImageView = view.findViewById(R.id.iv_search_article)
        val ivFiltros : ImageView = view.findViewById((R.id.iv_filter_all_articles))



        ivVolver.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        tvAgregarArticulo.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.newArticulo)
        }

        ivSearch.setOnClickListener{
            buscar()
        }

        ivFiltros.setOnClickListener{
            mostrarDialogoCategorias()
        }



        val gridView: GridView = view.findViewById(R.id.list_all_articles)
        adaptador = AdaptadorListAllArticles(view.context, ArrayList(DataProvider.listaArticulos))
        gridView.adapter = adaptador

        DataProvider.cargarDatos(
            adaptadorArticulos = adaptador
        )

        cargarArticulosFirebase()
    }

    private fun cargarArticulosFirebase() {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("articulos").get()
//            .addOnSuccessListener { result ->
//                listaArticulos.clear()
//                for (document in result) {
//                    val nombre = document.getString("nombre") ?: ""
//                    val cantidad = (document.getLong("cantidad") ?: 0L).toInt()
//                    val descripcion = document.getString("descripcion") ?: ""
//                    val costo = (document.getDouble("costo") ?: 0.0).toFloat()
//                    val imagenUrl = document.getString("imagenUrl") ?: ""
//
//                    val categoriaMap = document.get("categoria") as? Map<String, Any>
//                    val categoriaNombre = categoriaMap?.get("nombre") as? String ?: ""
//                    val categoriaColor = categoriaMap?.get("color") as? String ?: "#000000"
//
//                    val categoria = Categoria(categoriaNombre, categoriaColor)
//                    val articulo = Articulo(nombre, cantidad, descripcion, costo, categoria, imagenUrl)
//                    listaArticulos.add(articulo)
//                }
//                adaptador?.notifyDataSetChanged()
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireContext(), "Error cargando artículos", Toast.LENGTH_SHORT).show()
//            }

//        DataProvider.articuloDAO.obtenerTodosLosArticulos(
//            onSuccess = { lista ->
//                listaArticulos = lista
//                adaptador?.actualizarLista(lista)
//
//            },
//            onFailure = { error ->
//                Toast.makeText(context, "Error al obtener artículos", Toast.LENGTH_SHORT).show()
//            }
//        )


    }



    private fun buscar(){
        var nuevaLista = ArrayList<Articulo>()
        var nuevaListaConFiltro = ArrayList<Articulo>()
        var texto = view?.findViewById<EditText>(R.id.et_search_report)


        nuevaLista.clear()
        nuevaListaConFiltro.clear()

        if(!categoriasSeleccionadas.isEmpty()){
            for(e in DataProvider.listaArticulos){
                if(categoriasSeleccionadas.contains(e.categoria.nombre)){
                    nuevaListaConFiltro.add(e)
                }
            }

            if (texto?.text.toString().equals("")) {
                view?.findViewById<TextView>(R.id.texto_vacio)?.visibility = View.GONE
                adaptador?.actualizarLista(ArrayList(nuevaListaConFiltro))
                Log.d("size", categoriasSeleccionadas.size.toString())
            } else {

                for (e in nuevaListaConFiltro) {
                    if (e.nombre.contains(texto?.text.toString(), ignoreCase = true)) {
                        nuevaLista.add(e)
                    }

                }

                if (nuevaLista.isEmpty()) {
                    view?.findViewById<TextView>(R.id.texto_vacio)?.visibility = View.VISIBLE
                } else {
                    view?.findViewById<TextView>(R.id.texto_vacio)?.visibility = View.GONE
                }

                adaptador?.actualizarLista(nuevaLista)

            }
        }

        else {
            if (texto?.text.toString().equals("")) {
                view?.findViewById<TextView>(R.id.texto_vacio)?.visibility = View.GONE
                adaptador?.actualizarLista(ArrayList(DataProvider.listaArticulos))
                Log.d("size", categoriasSeleccionadas.size.toString())
            } else {

                for (e in DataProvider.listaArticulos) {
                    if (e.nombre.contains(texto?.text.toString(), ignoreCase = true)) {
                        nuevaLista.add(e)
                    }

                }

                if (nuevaLista.isEmpty()) {
                    view?.findViewById<TextView>(R.id.texto_vacio)?.visibility = View.VISIBLE
                } else {
                    view?.findViewById<TextView>(R.id.texto_vacio)?.visibility = View.GONE
                }

                adaptador?.actualizarLista(nuevaLista)
            }
        }
    }



    private fun mostrarDialogoCategorias() {
        val checkboxes = DataProvider.listaCategorias.map { categoria ->
            CheckBox(requireContext()).apply {
                text = categoria.nombre
                isChecked = categoria.nombre in categoriasSeleccionadas
            }
        }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            checkboxes.forEach { addView(it) }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona Categorías")
            .setView(layout)
            .setPositiveButton("Aplicar") { _, _ ->
                categoriasSeleccionadas.clear()
                categoriasSeleccionadas.addAll(
                    checkboxes.filter { it.isChecked }.map { it.text.toString() }
                )
                buscar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }








    private class AdaptadorListAllArticles : BaseAdapter {

        var articulos = ArrayList<Articulo>()
        var contexto: Context? = null

        constructor(contexto: Context, articulos: ArrayList<Articulo>) {
            this.contexto = contexto
            this.articulos = articulos
        }

        override fun getCount(): Int {
            return articulos.size
        }

        override fun getItem(position: Int): Any {
            return articulos[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val articulo = articulos[position]
            val inflador = LayoutInflater.from(contexto)
            val vista = inflador.inflate(R.layout.design_articles_list, null)

            val tv_title: TextView = vista.findViewById(R.id.title_article_list_all_articles)
            val tv_number: TextView = vista.findViewById(R.id.number_list_all_articles)
            val tv_category: TextView = vista.findViewById(R.id.title_category_list_all_articles)
            val iv_imagen: ImageView = vista.findViewById(R.id.imagen_list_all_articles)


            var disponibilidad = 0
            var listaCategoriasStrings = mutableListOf<String>()

            for(e in DataProvider.listaCategorias){
                listaCategoriasStrings.add(e.nombre)
            }

            for (e in DataProvider.listaEntradasSalidas) {
                if(listaCategoriasStrings.contains(e.articulo.categoria.nombre)) {
                    if (e.isEntrada) {
                        disponibilidad += e.cantidad
                    } else {
                        disponibilidad -= e.cantidad
                    }
                }
            }


            // Setear textos
            tv_category.setTextColor(Color.parseColor(articulo.categoria.color))
            tv_title.text = articulo.nombre
            tv_number.text = disponibilidad.toString()
            tv_category.text = articulo.categoria.nombre

            // Cargar imagen con Glide
            if (articulo.imagenUrl.isNotEmpty()) {
                Glide.with(contexto!!)
                    .load(articulo.imagenUrl.replace("http://", "https://"))
                    .placeholder(R.drawable.profileicon)
                    .error(R.drawable.profileicon)
                    .into(iv_imagen)
            } else {
                iv_imagen.setImageResource(R.drawable.profileicon)
            }



            // Click para detalle
            vista.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("nombre", articulo.nombre)
                    putString("categoria", articulo.categoria.nombre)
                    putInt("cantidad", disponibilidad)
                    putString("descripcion", articulo.descripcion)
                    putString("color", articulo.categoria.color)
                    putString("imagenUrl", articulo.imagenUrl)
                    putFloat("costo", articulo.costo)
                    putString("idArticulo", articulo.idArticulo)
                }

                Navigation.findNavController(vista).navigate(R.id.detailProduct, bundle)
            }

            return vista
        }

        fun actualizarLista(nuevaLista: ArrayList<Articulo>) {
            articulos.clear()
            articulos.addAll(nuevaLista)
            notifyDataSetChanged()
        }
    }

}
