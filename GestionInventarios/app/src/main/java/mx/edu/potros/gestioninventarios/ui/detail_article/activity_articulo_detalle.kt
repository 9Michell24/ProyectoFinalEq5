package mx.edu.potros.gestioninventarios.ui.detail_article

import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider

class activity_articulo_detalle : Fragment() {

    companion object {
        fun newInstance() = activity_articulo_detalle()
    }

    private val viewModel: ActivityArticuloDetalleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_activity_articulo_detalle, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivVolver: ImageView = view.findViewById(R.id.regresar)

        val btnEntSal : Button = view.findViewById(R.id.btnEntSal)
        btnEntSal.setOnClickListener {

            findNavController().navigate(R.id.navigation_movement)

        }

        ivVolver.setOnClickListener {

            //popBackStack es para volver al fragment anterior
            findNavController().popBackStack()

        }



        val nombre = arguments?.getString("nombre")
        val categoria = arguments?.getString("categoria")
        val cantidad = arguments?.getInt("cantidad")
        val position = arguments?.getInt("position")
        val descripcion = arguments?.getString("descripcion")

        val name : TextView = view.findViewById(R.id.detail_product_name)
        val category : TextView = view.findViewById(R.id.detail_product_category)
        val stock : TextView = view.findViewById(R.id.detail_product_stock)
        val description : TextView = view.findViewById(R.id.detail_product_description)


        if(position != null) {
            category.setTextColor(Color.parseColor(DataProvider.listaArticulos[position].categoria.color))
        }

        name.setText(nombre)
        category.setText(categoria)
        stock.setText("Cantidad en stock: " + cantidad.toString())
        description.setText(descripcion)


    }


}