package mx.edu.potros.gestioninventarios.ui.detail_article

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import mx.edu.potros.gestioninventarios.R

class ActivityArticuloDetalle : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_activity_articulo_detalle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivVolver: ImageView = view.findViewById(R.id.regresar)
        val btnEntSal: Button = view.findViewById(R.id.btnEntSal)
        val btnEditarArt: Button = view.findViewById(R.id.btnEditarArt)

        val name: TextView = view.findViewById(R.id.detail_product_name)
        val category: TextView = view.findViewById(R.id.detail_product_category)
        val stock: TextView = view.findViewById(R.id.detail_product_stock)
        val description: TextView = view.findViewById(R.id.detail_product_description)
        val imageView: ImageView = view.findViewById(R.id.detail_product_image)

        ivVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        btnEntSal.setOnClickListener {
            findNavController().navigate(R.id.navigation_movement)
        }

        btnEditarArt.setOnClickListener {
            // Aquí podrías abrir la pantalla de edición si la tienes implementada
            Toast.makeText(requireContext(), "Editar artículo no implementado", Toast.LENGTH_SHORT).show()
        }

        // Recibir los argumentos del bundle
        val nombre = arguments?.getString("nombre") ?: "(sin nombre)"
        val categoriaNombre = arguments?.getString("categoria") ?: "(sin categoría)"
        val cantidad = arguments?.getInt("cantidad") ?: 0
        val descripcion = arguments?.getString("descripcion") ?: "(sin descripción)"
        val colorCategoria = arguments?.getString("color") ?: "#000000"
        val imagenUrl = arguments?.getString("imagenUrl") ?: ""

        name.text = nombre
        category.text = categoriaNombre
        stock.text = "Cantidad en stock: $cantidad"
        description.text = descripcion

        try {
            category.setTextColor(Color.parseColor(colorCategoria))
        } catch (e: Exception) {
            category.setTextColor(Color.BLACK)
        }

        if (imagenUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .placeholder(R.drawable.profileicon)
                .error(R.drawable.profileicon)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.profileicon)
        }
    }
}
