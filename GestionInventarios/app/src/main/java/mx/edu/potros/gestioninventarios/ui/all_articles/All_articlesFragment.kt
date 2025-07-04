package mx.edu.potros.gestioninventarios.ui.all_articles

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria

class All_articlesFragment : Fragment() {

    private var adaptador: AdaptadorListAllArticles? = null
    private val listaArticulos: ArrayList<Articulo> = arrayListOf()

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

        ivVolver.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        tvAgregarArticulo.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.newArticulo)
        }

        ivAgregarArticulo.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.newArticulo)
        }

        val gridView: GridView = view.findViewById(R.id.list_all_articles)
        adaptador = AdaptadorListAllArticles(requireContext(), listaArticulos)
        gridView.adapter = adaptador

        cargarArticulosFirebase()
    }

    private fun cargarArticulosFirebase() {
        val db = FirebaseFirestore.getInstance()
        db.collection("articulos").get()
            .addOnSuccessListener { result ->
                listaArticulos.clear()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: ""
                    val cantidad = (document.getLong("cantidad") ?: 0L).toInt()
                    val descripcion = document.getString("descripcion") ?: ""
                    val costo = (document.getDouble("costo") ?: 0.0).toFloat()
                    val imagenUrl = document.getString("imagenUrl") ?: ""

                    val categoriaMap = document.get("categoria") as? Map<String, Any>
                    val categoriaNombre = categoriaMap?.get("nombre") as? String ?: ""
                    val categoriaColor = categoriaMap?.get("color") as? String ?: "#000000"

                    val categoria = Categoria(categoriaNombre, categoriaColor)
                    val articulo = Articulo(nombre, cantidad, descripcion, costo, categoria, imagenUrl)
                    listaArticulos.add(articulo)
                }
                adaptador?.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error cargando artículos", Toast.LENGTH_SHORT).show()
            }
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

            val contador = articulo.cantidad

            // Setear textos
            tv_category.setTextColor(Color.parseColor(articulo.categoria.color))
            tv_title.text = articulo.nombre
            tv_number.text = contador.toString()
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
                    putInt("cantidad", articulo.cantidad)
                    putString("descripcion", articulo.descripcion)
                    putString("color", articulo.categoria.color)
                    putString("imagenUrl", articulo.imagenUrl)
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
