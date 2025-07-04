package mx.edu.potros.gestioninventarios.ui.add_item

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentAddItemBinding
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider

class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    private val categoriasSeleccionadas = ArrayList<String>()


    lateinit var textoCategoria : TextView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(AddItemViewModel::class.java)

        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivVolver: ImageView = view.findViewById(R.id.regresar)
        val btnGuardar : Button = view.findViewById(R.id.btnGuardarArt)
        val seleccionarCatgoria : LinearLayout = view.findViewById(R.id.ll_select_category_register_article)
        textoCategoria = view.findViewById(R.id.spinnerCategoríaArtículo)



        seleccionarCatgoria.setOnClickListener{
            mostrarDialogoCategorias()
        }


        btnGuardar.setOnClickListener {

            findNavController().navigate(R.id.detailProduct)

        }
        ivVolver.setOnClickListener {

            //popBackStack es para volver al fragment anterior
            findNavController().popBackStack()

        }




    }





    private fun mostrarDialogoCategorias() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val seleccionActual = categoriasSeleccionadas.firstOrNull()
        var categoriaSeleccionada: String? = seleccionActual
        var vistaSeleccionada: TextView? = null

        DataProvider.listaCategorias.forEach { categoria ->
            val textView = TextView(requireContext()).apply {
                text = categoria.nombre
                textSize = 18f
                setPadding(20, 20, 20, 20)
                setTextColor(Color.parseColor(categoria.color))
                setTypeface(null, Typeface.BOLD)
                setBackgroundColor(
                    if (categoria.nombre == seleccionActual) Color.LTGRAY else Color.TRANSPARENT
                )

                if (categoria.nombre == seleccionActual) {
                    vistaSeleccionada = this
                }

                setOnClickListener {
                    vistaSeleccionada?.setBackgroundColor(Color.TRANSPARENT)

                    setBackgroundColor(Color.LTGRAY)
                    categoriaSeleccionada = categoria.nombre
                    vistaSeleccionada = this
                }
            }

            layout.addView(textView)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona una Categoría")
            .setView(ScrollView(requireContext()).apply { addView(layout) })
            .setPositiveButton("Aplicar") { _, _ ->
                categoriaSeleccionada?.let {
                    categoriasSeleccionadas.clear()
                    categoriasSeleccionadas.add(it)
                    textoCategoria.setText(it)

                    for (e in DataProvider.listaCategorias){
                        if(e.nombre.equals(it)){
                            textoCategoria.setTextColor(Color.parseColor(e.color))
                        }
                    }

                    textoCategoria.setTypeface(null, Typeface.BOLD)

                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }






}