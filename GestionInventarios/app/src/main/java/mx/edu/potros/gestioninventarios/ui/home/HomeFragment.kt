package mx.edu.potros.gestioninventarios.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentHomeBinding
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import mx.edu.potros.gestioninventarios.objetoNegocio.EntradasSalidas

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private var adaptador: AdaptadorCategoriasHome? = null
    private lateinit var homeViewModel: HomeViewModel


    private var cargado : Boolean = false


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?




    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val txtCategoria : TextView = root.findViewById(R.id.category_home)
        val txtSeeArticles : TextView = root.findViewById(R.id.see_datail_article)
        val imConfig : ImageView = root.findViewById(R.id.iv_config)


        txtCategoria.setOnClickListener {

            findNavController().navigate(R.id.categoriesFragment)
        }

        txtSeeArticles.setOnClickListener {

            findNavController().navigate(R.id.allProdutsFragment)
        }

        imConfig.setOnClickListener {

            findNavController().navigate(R.id.configFragment)
        }


        if(!cargado) {
            cargarArticulo()
            cargado = true
        }


        adaptador = AdaptadorCategoriasHome(root.context, DataProvider.listaCategorias)

        val gridView : GridView = root.findViewById(R.id.lista_categorias_home)

        gridView.adapter = adaptador

        var cantidad : Int = 0

        for(e in DataProvider.listaEntradasSalidas){
            if (e.isEntrada){
                cantidad = cantidad + e.cantidad
            }
        }

        root.findViewById<TextView>(R.id.number_article).setText(cantidad.toString())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun cargarArticulo(){


        DataProvider.listaCategorias.add(Categoria("Electrónica", "#0AB5FE"))
        DataProvider.listaCategorias.add(Categoria("Calzado", "#EA4D4F"))
        DataProvider.listaCategorias.add(Categoria("Jardinería", "#29AF2A"))


        DataProvider.listaArticulos.add(Articulo("Refrigerador", 9, "noup", 500.23f, DataProvider.listaCategorias[0]))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[0], 9, "24/06/2025", "nuevoArticulo", true))


        DataProvider.listaArticulos.add(Articulo("Vans", 20, "noup", 300.23f, DataProvider.listaCategorias[1]))
        DataProvider.listaArticulos.add(Articulo("Vans2", 1, "noup", 300.23f, DataProvider.listaCategorias[1]))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[1], 20, "24/06/2025", "nuevoArticulo", true))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[2], 1, "24/06/2025", "nuevoArticulo", true))


        DataProvider.listaArticulos.add(Articulo("Pala", 20, "noup", 50f, DataProvider.listaCategorias[2]))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[3], 20, "24/06/2025", "nuevoArticulo", true))



    }




    private class AdaptadorCategoriasHome : BaseAdapter {

        var categorias = ArrayList<Categoria>()
        var contexto: Context? = null

        constructor(contexto: Context, categorias : ArrayList<Categoria>){
            this.contexto = contexto
            this.categorias = categorias

        }

        override fun getCount(): Int {
            return categorias.size
        }

        override fun getItem(position: Int): Any {
            return categorias[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var categoria = categorias[position]
            var inflador = LayoutInflater.from(contexto)
            var vista = inflador.inflate(R.layout.design_category_home_list, null)

            var tv_title: TextView = vista.findViewById(R.id.tv_category_name_home)
            var tv_number: TextView = vista.findViewById(R.id.tv_category_number_home)
            val fondo: LinearLayout = vista.findViewById(R.id.fondo_lista_categorias_home)

            var contador : Int = 0

            for (e in DataProvider.listaEntradasSalidas){
                if(e.articulo.categoria.nombre.equals(categoria.nombre)){
                    if(e.isEntrada){
                        contador = contador + e.cantidad
                    }
                    else{
                        contador = contador - e.cantidad
                    }

                    }
            }


            tv_title.setText(categoria.nombre)
            tv_number.setText(contador.toString())

            DataProvider.listaCategorias[0].color

                val color = Color.parseColor(categoria.color)

                val drawable = GradientDrawable()
                drawable.cornerRadius = 40f
                drawable.setColor(color)

                fondo.background = drawable

            vista.setOnClickListener{
                val bundle = Bundle().apply {
                    putInt("position", position)

                }
                Navigation.findNavController(vista).navigate(R.id.categoriesFragment, bundle)

            }

            return vista


        }
    }



}