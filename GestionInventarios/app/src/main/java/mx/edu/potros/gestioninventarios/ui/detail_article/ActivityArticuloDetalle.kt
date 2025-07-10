package mx.edu.potros.gestioninventarios.ui.detail_article

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider

class ActivityArticuloDetalle : Fragment() {

    lateinit var nombre : String


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
        val eraseArticle : ImageView = view.findViewById(R.id.iv_erase_article)

        ivVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        btnEntSal.setOnClickListener {
            findNavController().navigate(R.id.navigation_movement)
        }


        // Recibir los argumentos del bundle
        val id = arguments?.getString("idArticulo") ?: ""
        nombre = arguments?.getString("nombre") ?: "(sin nombre)"
        val categoriaNombre = arguments?.getString("categoria") ?: "(sin categoría)"
        val cantidad = arguments?.getInt("cantidad") ?: 0
        val descripcion = arguments?.getString("descripcion") ?: "(sin descripción)"
        val colorCategoria = arguments?.getString("color") ?: "#000000"
        val costo = arguments?.getFloat("costo") ?: 0f
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
                .load(imagenUrl.replace("http://", "https://"))
                .placeholder(R.drawable.profileicon)
                .error(R.drawable.profileicon)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.profileicon)
        }




        btnEditarArt.setOnClickListener {



            //Validaciones






            var idCategoria = ""

            for ((e,i) in DataProvider.listaArticulos.withIndex()){
                if(i.categoria.nombre.equals(categoriaNombre)){
                    idCategoria = i.categoria.idCategoria
                }
            }

            DataProvider.categoriaDAO.obtenerCategoriaPorId(idCategoria,
                onSuccess = { categoria ->
                    if (categoria != null) {
                        val articuloEditado = Articulo(
                            idArticulo = id,
                            nombre = "nombre editado",
                            cantidad = cantidad,
                            descripcion = descripcion,
                            costo = costo,
                            categoria = categoria,
                            imagenUrl = imagenUrl
                        )

                        DataProvider.articuloDAO.editarArticulo(articuloEditado,
                            onSuccess = {
                                Toast.makeText(requireContext(), "Se edito", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { error ->
                                // Error al editar
                            })
                    } else {
                        // No se encontró la categoría con ese id
                    }
                },
                onFailure = { error ->
                    // Error al obtener categoría
                }
            )




            // Aquí podrías abrir la pantalla de edición si la tienes implementada
           // Toast.makeText(requireContext(), "Editar artículo no implementado", Toast.LENGTH_SHORT).show()
        }





        eraseArticle.setOnClickListener {

            mostrarDialogoConfirmacionEliminar {

                DataProvider.articuloDAO.eliminarArticulo(
                    idArticulo = id,
                    onSuccess = {

                        Toast.makeText(requireContext(), "Se elimino", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "No se elimino :c", Toast.LENGTH_SHORT).show()
                    }
                    )

            }

        }





    }



    private fun mostrarDialogoConfirmacionEliminar(onConfirmar: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("¿Eliminar elemento?")
            .setMessage("¿Estás seguro de que deseas eliminar ${nombre}? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { dialog, _ ->
                onConfirmar()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
