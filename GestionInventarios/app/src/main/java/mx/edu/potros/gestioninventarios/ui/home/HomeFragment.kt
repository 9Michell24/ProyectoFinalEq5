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
import mx.edu.potros.gestioninventarios.utilities.CustomCircleDrawable

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adaptador: AdaptadorCategoriasHome? = null
    private lateinit var homeViewModel: HomeViewModel

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

        // ✅ Mostrar número total de artículos
        DataProvider.cargarDatos(
            adaptadorCategorias = adaptador,
            alFinalizarEntradas = {
                root.findViewById<TextView>(R.id.number_article)
                    .text = DataProvider.articulosActuales.toString()
            }
        )

        // ✅ Asignar gráfico circular como fondo
        val graphicHome: ImageView = root.findViewById(R.id.graphic_home)
        graphicHome.background = CustomCircleDrawable(requireContext(), DataProvider.listaCategorias)

        return root
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
                if (e.articulo.categoria.nombre == categoria.nombre) {
                    if (e.isEntrada) contador += e.cantidad
                    else contador -= e.cantidad
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
                    putInt("position", position)
                    putInt("totalArticles", contador)
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
