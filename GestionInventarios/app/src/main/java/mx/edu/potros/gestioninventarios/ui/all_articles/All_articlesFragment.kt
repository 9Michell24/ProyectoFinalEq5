package mx.edu.potros.gestioninventarios.ui.all_articles

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import mx.edu.potros.gestioninventarios.ui.home.HomeViewModel

class All_articlesFragment : Fragment() {

    companion object {
        fun newInstance() = All_articlesFragment()
    }

    private val viewModel: AllArticlesViewModel by viewModels()


    private var adaptador: AdaptadorListAllArticles? = null
    private lateinit var homeViewModel: HomeViewModel

    private val categoriasSeleccionadas = ArrayList<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {




        return inflater.inflate(R.layout.fragment_all_articles, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivVolver: ImageView = view.findViewById(R.id.iv_back_all_articles)

        val tvAgregarArticulo : TextView = view.findViewById(R.id.agregarArticulo)
        val ivAgregarArticulo : ImageView = view.findViewById(R.id.iv_add_article)


        val ivFilter : ImageView = view.findViewById(R.id.iv_filter_all_articles)


        ivFilter.setOnClickListener{
            mostrarDialogoCategorias()
        }


        tvAgregarArticulo.setOnClickListener {

            findNavController().navigate(R.id.newArticulo)
        }

        ivAgregarArticulo.setOnClickListener {

            findNavController().navigate(R.id.newArticulo)
        }


        ivVolver.setOnClickListener {

            //popBackStack es para volver al fragment anterior
            findNavController().popBackStack()

        }


        adaptador = AdaptadorListAllArticles(view.context, DataProvider.listaArticulos)

        val gridView : GridView = view.findViewById(R.id.list_all_articles)

        gridView.adapter = adaptador



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
                aplicarFiltro(categoriasSeleccionadas)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun aplicarFiltro(filtros: ArrayList<String>) {

    }



    private class AdaptadorListAllArticles : BaseAdapter {

        var articulos = ArrayList<Articulo>()
        var contexto: Context? = null

        constructor(contexto: Context, articulos : ArrayList<Articulo>){
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
            var articulo = articulos[position]
            var inflador = LayoutInflater.from(contexto)
            var vista = inflador.inflate(R.layout.design_articles_list, null)

            var tv_title: TextView = vista.findViewById(R.id.title_article_list_all_articles)
            var tv_number: TextView = vista.findViewById(R.id.number_list_all_articles)
            var tv_category: TextView = vista.findViewById(R.id.title_category_list_all_articles)
            var iv_imagen: ImageView = vista.findViewById(R.id.imagen_list_all_articles)


            var contador : Int = 0

            for (e in DataProvider.listaEntradasSalidas){
                if(e.articulo.nombre.equals(articulo.nombre)){
                    if(e.isEntrada){
                        contador = contador + e.cantidad
                    }
                    else{
                        contador = contador - e.cantidad
                    }

                }
            }



            tv_category.setTextColor(Color.parseColor(articulo.categoria.color))

            tv_title.setText(articulo.nombre)
            tv_number.setText(contador.toString())
            tv_category.setText(articulo.categoria.nombre)
          //  iv_imagen.setImageResource(R.drawable.profileicon)

            vista.setOnClickListener{
                val bundle = Bundle().apply {
                    putString("nombre", articulo.nombre)
                    putString("categoria", articulo.categoria.nombre)
                    putInt("cantidad", contador)
                    putString("descripcion", articulo.descripcion)
                    putInt("position", position)
                }

                Navigation.findNavController(vista).navigate(R.id.detailProduct, bundle)
            }


            return vista


        }
    }


}