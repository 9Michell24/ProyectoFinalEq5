package mx.edu.potros.gestioninventarios.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
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
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider.listaEntradasSalidas
import mx.edu.potros.gestioninventarios.utilities.CustomCircleDrawable

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
        val txtSeeArticles: TextView = root.findViewById(R.id.see_datail_article)
        val imConfig: ImageView = root.findViewById(R.id.iv_config)
        val llAllArticles: LinearLayout = root.findViewById(R.id.ll_home_section_all_articles)

        // Asignar a las propiedades de la clase
        graphicHome = root.findViewById(R.id.graphic_home)
        numberArticleTextView = root.findViewById(R.id.number_article)

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
                // Actualiza el texto del número de artículos
                numberArticleTextView.text = DataProvider.articulosActuales.toString()
                // Asigna la gráfica solo cuando los datos ya están cargados
                graphicHome.background = CustomCircleDrawable(requireContext(), DataProvider.listaCategorias)
                // Asegúrate de que el adaptador de categorías se actualice aquí también
                adaptador?.notifyDataSetChanged()
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
                numberArticleTextView.text = DataProvider.articulosActuales.toString()
                graphicHome.background = CustomCircleDrawable(requireContext(), DataProvider.listaCategorias)
                adaptador?.notifyDataSetChanged() // Asegúrate de que el GridView se refresque
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


            var contador = 0
            for (e in DataProvider.listaEntradasSalidas) {
                if (e.isEntrada) {
                    articulosActuales += e.cantidad
                } else {
                    articulosActuales -= e.cantidad
                }
            }


            tv_title.text = categoria.nombre
            tv_number.text = contador.toString()

            val color = Color.parseColor(categoria.color)
            val drawable = GradientDrawable().apply {
                cornerRadius = 40f
                setColor(color)
            }
            fondo.background = drawable

            vista.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("position", position) // Esto es la posición de la categoría en la lista
                    putInt("totalArticles", contador) // Esto es el total de artículos en esa categoría
                    // Si quieres pasar el nombre de la categoría para filtrar, también sería útil:
                    putString("categoryName", categoria.nombre)
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
    }
}