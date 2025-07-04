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


        if(!DataProvider.cargado) {
            cargarArticulo()
            DataProvider.cargado = true
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


//        DataProvider.listaCategorias.add(Categoria("Electrónica", "#0AB5FE"))
//        DataProvider.listaCategorias.add(Categoria("Calzado", "#EA4D4F"))
//        DataProvider.listaCategorias.add(Categoria("Jardinería", "#29AF2A"))
//
//
//        DataProvider.listaArticulos.add(Articulo("Refrigerador", 9, "noup", 500.23f, DataProvider.listaCategorias[0]))
//        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[0], 9, "24/06/2025", "nuevoArticulo", true))
//
//
//        DataProvider.listaArticulos.add(Articulo("Vans", 20, "noup", 300.23f, DataProvider.listaCategorias[1]))
//        DataProvider.listaArticulos.add(Articulo("Vans2", 1, "noup", 300.23f, DataProvider.listaCategorias[1]))
//        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[1], 20, "24/06/2025", "nuevoArticulo", true))
//        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[2], 1, "24/06/2025", "nuevoArticulo", true))
//
//
//        DataProvider.listaArticulos.add(Articulo("Pala", 20, "noup", 50f, DataProvider.listaCategorias[2]))
//        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[3], 20, "24/06/2025", "nuevoArticulo", true))


        // Categorías
        DataProvider.listaCategorias.add(Categoria("Electrónica", "#0AB5FE"))
        DataProvider.listaCategorias.add(Categoria("Calzado", "#EA4D4F"))
        DataProvider.listaCategorias.add(Categoria("Jardinería", "#29AF2A"))
        DataProvider.listaCategorias.add(Categoria("Papelería", "#FFC107"))
        DataProvider.listaCategorias.add(Categoria("Deportes", "#9C27B0"))

// Artículos base
        DataProvider.listaArticulos.add(Articulo("Refrigerador", 9, "Refrigerador de dos puertas con sistema No-Frost", 500.23f, DataProvider.listaCategorias[0], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[0], 9, "24/06/2025", "Nuevo artículo", true))

        DataProvider.listaArticulos.add(Articulo("Vans", 20, "Tenis Vans clásicos modelo Old Skool", 300.23f, DataProvider.listaCategorias[1], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[1], 20, "24/06/2025", "Nuevo artículo", true))

        DataProvider.listaArticulos.add(Articulo("Vans2", 1, "Versión limitada Vans con diseño exclusivo", 300.23f, DataProvider.listaCategorias[1], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[2], 1, "24/06/2025", "Nuevo artículo", true))

        DataProvider.listaArticulos.add(Articulo("Pala", 20, "Pala de acero para jardinería pesada", 50f, DataProvider.listaCategorias[2], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[3], 20, "24/06/2025", "Nuevo artículo", true))

// Nuevos artículos (10)
        DataProvider.listaArticulos.add(Articulo("Cuaderno profesional", 100, "Cuaderno de 100 hojas rayadas", 25.5f, DataProvider.listaCategorias[3], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[4], 100, "25/06/2025", "Nuevo artículo", true))

        DataProvider.listaArticulos.add(Articulo("Balón de fútbol", 15, "Balón oficial tamaño 5", 320.0f, DataProvider.listaCategorias[4], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[5], 15, "25/06/2025", "Nuevo artículo", true))

        DataProvider.listaArticulos.add(Articulo("Laptop Lenovo", 5, "Laptop con procesador i5 y 8GB RAM", 12000f, DataProvider.listaCategorias[0], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[6], 5, "26/06/2025", "Reposición", true))

        DataProvider.listaArticulos.add(Articulo("Sandalias", 30, "Sandalias de descanso para verano", 120.0f, DataProvider.listaCategorias[1], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[7], 30, "26/06/2025", "Nuevo artículo", true))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[7], 10, "28/06/2025", "Venta parcial", false))

        DataProvider.listaArticulos.add(Articulo("Tijeras escolares", 50, "Tijeras punta redonda para niños", 15.0f, DataProvider.listaCategorias[3], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[8], 50, "26/06/2025", "Nuevo artículo", true))

        DataProvider.listaArticulos.add(Articulo("Pesas 5kg", 10, "Par de pesas de goma de 5kg", 700.0f, DataProvider.listaCategorias[4], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[9], 10, "26/06/2025", "Nuevo artículo", true))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[9], 4, "28/06/2025", "Salida por venta", false))

        DataProvider.listaArticulos.add(Articulo("Tablet Samsung", 7, "Tablet de 10.4 pulgadas, 64GB", 5200.0f, DataProvider.listaCategorias[0], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[10], 7, "27/06/2025", "Adición de inventario", true))

        DataProvider.listaArticulos.add(Articulo("Botas montaña", 12, "Botas resistentes para senderismo", 860.0f, DataProvider.listaCategorias[1], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[11], 12, "27/06/2025", "Nuevo artículo", true))

        DataProvider.listaArticulos.add(Articulo("Regadera", 18, "Regadera manual de 2 litros para jardín", 80.0f, DataProvider.listaCategorias[2], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[12], 18, "27/06/2025", "Nuevo artículo", true))

        DataProvider.listaArticulos.add(Articulo("Lapiceros negros", 200, "Paquete de 12 lapiceros tinta negra", 50.0f, DataProvider.listaCategorias[3], ""))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[13], 200, "28/06/2025", "Nuevo artículo", true))
        DataProvider.listaEntradasSalidas.add(EntradasSalidas(DataProvider.listaArticulos[13], 40, "30/06/2025", "Consumo interno", false))


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