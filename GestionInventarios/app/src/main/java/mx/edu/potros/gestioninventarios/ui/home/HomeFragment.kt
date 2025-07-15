package mx.edu.potros.gestioninventarios.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentHomeBinding
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider.articulosActuales
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider.listaCategorias
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider.listaEntradasSalidas
import mx.edu.potros.gestioninventarios.objetoNegocio.EntradasSalidas
import mx.edu.potros.gestioninventarios.utilities.CustomCircleDrawable
import java.text.SimpleDateFormat
import java.util.Locale

var cont = 0
lateinit var txtAll : TextView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adaptador: AdaptadorCategoriasHome? = null
    private lateinit var homeViewModel: HomeViewModel

    // Declarar graphicHome y number_article como propiedades de la clase
    // para poder acceder a ellas en onResume
    private lateinit var graphicHome: ImageView
    private lateinit var numberArticleTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val txtCategoria: TextView = root.findViewById(R.id.category_home)
        val imConfig: ImageView = root.findViewById(R.id.iv_config)
        val llAllArticles: LinearLayout = root.findViewById(R.id.ll_home_section_all_articles)

        graphicHome = root.findViewById(R.id.graphic_home)
        numberArticleTextView = root.findViewById(R.id.number_article)


        txtAll = root.findViewById(R.id.number_article)


        txtCategoria.setOnClickListener {
            findNavController().navigate(R.id.categoriesFragment)
        }

        imConfig.setOnClickListener {
            findNavController().navigate(R.id.configFragment)
        }

        llAllArticles.setOnClickListener {
            findNavController().navigate(R.id.allProdutsFragment)
        }

        adaptador = AdaptadorCategoriasHome(root.context, DataProvider.listaCategorias)
        val gridView: GridView = root.findViewById(R.id.lista_categorias_home)
        gridView.adapter = adaptador

        DataProvider.cargarDatos(
            adaptadorCategorias = adaptador,
            alFinalizarEntradas = {
                graphicHome.background = CustomCircleDrawable(requireContext(), DataProvider.listaCategorias)
                adaptador?.notifyDataSetChanged()
                adaptador?.cargarTotalArticles()

            }
        )

        return root
    }


    override fun onResume() {
        super.onResume()

        DataProvider.cargarDatos(
            adaptadorCategorias = adaptador, // Pasa el adaptador para que DataProvider lo notifique
            alFinalizarEntradas = {
                // Esto se ejecuta cuando DataProvider.cargarDatos() ha terminado
                // Es crucial para actualizar la UI con los nuevos datos
              //  numberArticleTextView.text = DataProvider.articulosActuales.toString()
                graphicHome.background = CustomCircleDrawable(requireContext(), DataProvider.listaCategorias)
                adaptador?.notifyDataSetChanged() // Asegúrate de que el GridView se refresque
                adaptador?.cargarTotalArticles()
            }
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class AdaptadorCategoriasHome(
        var contexto: Context,
        var categorias: ArrayList<Categoria>
    ) : BaseAdapter() {

        override fun getCount(): Int = categorias.size

        override fun getItem(position: Int): Any = categorias[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val categoria = categorias[position]
            val inflador = LayoutInflater.from(contexto)
            val vista = inflador.inflate(R.layout.design_category_home_list, null)

            val tv_title: TextView = vista.findViewById(R.id.tv_category_name_home)
            val tv_number: TextView = vista.findViewById(R.id.tv_category_number_home)
            val fondo: LinearLayout = vista.findViewById(R.id.fondo_lista_categorias_home)

            var listaCategoriasStrings = mutableListOf<String>()

            for (e in DataProvider.listaCategorias) {
                listaCategoriasStrings.add(e.nombre)
            }

            var listaEntradas = ArrayList<EntradasSalidas>()


            var contador = 0
            for (e in DataProvider.listaEntradasSalidas) {
                if (e.articulo.categoria.nombre.equals(categoria.nombre)) {
                    if (e.isEntrada) {
                        contador += e.cantidad
                    } else {
                        contador -= e.cantidad
                    }
                    listaEntradas.add(e)
                }
            }


                tv_title.text = categoria.nombre

                var tiposArticulos = 0
                for (e in DataProvider.listaArticulos){
                    if (e.categoria.nombre.equals(categoria.nombre)){
                        tiposArticulos++
                    }
                }


                tv_number.text = tiposArticulos.toString()

                val color = Color.parseColor(categoria.color)
                val drawable = GradientDrawable().apply {
                    cornerRadius = 40f
                    setColor(color)
                }
                fondo.background = drawable



                var art = 0
                for (e in DataProvider.listaEntradasSalidas) {
                    if (listaCategoriasStrings.contains(e.articulo.categoria.nombre)) {
                        if (e.isEntrada) {
                            art += e.cantidad
                        } else {
                            art -= e.cantidad
                        }
                    }
                }

                vista.setOnClickListener {
                    val bundle = Bundle().apply {
                        putInt("position", position)
                        putInt("totalArticles", tiposArticulos)
                        putString("categoryName", categoria.nombre)
                        putParcelableArrayList("listaEntradas", listaEntradas)
                    }

                    Navigation.findNavController(vista).navigate(R.id.categoriesFragment, bundle)
                }


                return vista
            }

            fun actualizarLista(nuevaLista: ArrayList<Categoria>) {
                categorias.clear()
                categorias.addAll(nuevaLista)
                notifyDataSetChanged()
            }

            fun cargarTotalArticles(){
                var listaIdArticulos = ArrayList<String>()

                for (e in DataProvider.listaArticulos){
                    listaIdArticulos.add(e.idArticulo)
                }

                var totalArticles = 0
                for(e in DataProvider.listaEntradasSalidas){
                    if(listaIdArticulos.contains(e.articulo.idArticulo)){
                        if (e.isEntrada){
                            totalArticles += e.cantidad
                        }
                        else{
                            totalArticles -= e.cantidad
                        }
                    }
                }
                txtAll.setText(totalArticles.toString())

            }
        }
}