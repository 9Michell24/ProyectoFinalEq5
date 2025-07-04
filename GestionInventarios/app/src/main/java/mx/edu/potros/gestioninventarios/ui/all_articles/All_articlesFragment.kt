package mx.edu.potros.gestioninventarios.ui.all_articles

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider

class All_articlesFragment : Fragment() {

    private val viewModel: AllArticlesViewModel by viewModels()

    private var adaptador: AdaptadorListAllArticles? = null
    private val categoriasSeleccionadas = ArrayList<String>()

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
        val ivAgregarArticulo: ImageView = view.findViewById(R.id.iv_add_article)
        val ivSearchReport: ImageView = view.findViewById(R.id.iv_search_report)
        val ivFilter: ImageView = view.findViewById(R.id.iv_filter_all_articles)
        val gridView: GridView = view.findViewById(R.id.list_all_articles)

        adaptador = AdaptadorListAllArticles(view.context, ArrayList())
        gridView.adapter = adaptador

        ivVolver.setOnClickListener { findNavController().popBackStack() }
        tvAgregarArticulo.setOnClickListener { findNavController().navigate(R.id.newArticulo) }
        ivAgregarArticulo.setOnClickListener { findNavController().navigate(R.id.newArticulo) }
        ivSearchReport.setOnClickListener { buscar() }
        ivFilter.setOnClickListener { mostrarDialogoCategorias() }

        cargarArticulosDesdeFirestore()
    }

    private fun cargarArticulosDesdeFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("articulos").get()
            .addOnSuccessListener { result ->
                val lista = ArrayList<Articulo>()
                val categoriasUnicas = mutableSetOf<String>()

                for (document in result) {
                    val categoriaNombre = document.get("categoria.nombre") as? String ?: ""
                    val categoriaColor = document.get("categoria.color") as? String ?: "#000000"

                    categoriasUnicas.add(categoriaNombre)

                    val articulo = Articulo(
                        nombre = document.getString("nombre") ?: "",
                        cantidad = document.getLong("cantidad")?.toInt() ?: 0,
                        descripcion = document.getString("descripcion") ?: "",
                        costo = document.getDouble("costo")?.toFloat() ?: 0f,
                        categoria = Categoria(
                            nombre = categoriaNombre,
                            color = categoriaColor
                        ),
                        imagenUrl = document.getString("imagenUrl") ?: "" // opcional
                    )
                    lista.add(articulo)
                }

                DataProvider.listaArticulos = lista
                DataProvider.listaCategorias = categoriasUnicas.map { Categoria(it, "#000000") }
                    .toCollection(ArrayList())

                adaptador?.actualizarLista(lista)

                if (lista.isEmpty()) {
                    view?.findViewById<TextView>(R.id.texto_vacio)?.visibility = View.VISIBLE
                } else {
                    view?.findViewById<TextView>(R.id.texto_vacio)?.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar artículos", Toast.LENGTH_SHORT).show()
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

    private fun buscar() {
        val texto = view?.findViewById<EditText>(R.id.et_search_report)?.text.toString()
        val nuevaLista = ArrayList<Articulo>()

        val base = if (categoriasSeleccionadas.isEmpty()) {
            DataProvider.listaArticulos
        } else {
            DataProvider.listaArticulos.filter {
                categoriasSeleccionadas.contains(it.categoria.nombre)
            }
        }

        if (texto.isBlank()) {
            nuevaLista.addAll(base)
        } else {
            nuevaLista.addAll(
                base.filter { it.nombre.contains(texto, ignoreCase = true) }
            )
        }

        if (nuevaLista.isEmpty()) {
            view?.findViewById<TextView>(R.id.texto_vacio)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<TextView>(R.id.texto_vacio)?.visibility = View.GONE
        }

        adaptador?.actualizarLista(ArrayList(nuevaLista))
    }

    private class AdaptadorListAllArticles(
        private val contexto: Context,
        private var articulos: ArrayList<Articulo>
    ) : BaseAdapter() {

        override fun getCount(): Int = articulos.size

        override fun getItem(position: Int): Any = articulos[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val articulo = articulos[position]
            val inflador = LayoutInflater.from(contexto)
            val vista = inflador.inflate(R.layout.design_articles_list, null)

            val tvTitle: TextView = vista.findViewById(R.id.title_article_list_all_articles)
            val tvNumber: TextView = vista.findViewById(R.id.number_list_all_articles)
            val tvCategory: TextView = vista.findViewById(R.id.title_category_list_all_articles)
            val ivImagen: ImageView = vista.findViewById(R.id.imagen_list_all_articles)

            tvTitle.text = articulo.nombre
            tvNumber.text = articulo.cantidad.toString()
            tvCategory.text = articulo.categoria.nombre
            tvCategory.setTextColor(Color.parseColor(articulo.categoria.color))

            Glide.with(contexto)
                .load(articulo.imagenUrl)
                .placeholder(R.drawable.profileicon)
                .into(ivImagen)

            vista.setOnClickListener { view ->
                val bundle = Bundle().apply {
                    putString("nombre", articulo.nombre)
                    putString("categoria", articulo.categoria.nombre)
                    putInt("cantidad", articulo.cantidad)
                    putString("descripcion", articulo.descripcion)
                    putInt("position", position)
                }
                Navigation.findNavController(view).navigate(R.id.detailProduct, bundle)
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
