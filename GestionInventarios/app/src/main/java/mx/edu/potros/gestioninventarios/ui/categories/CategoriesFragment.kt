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