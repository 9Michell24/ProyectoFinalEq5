package mx.edu.potros.gestioninventarios.ui.add_item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentAddItemBinding

class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        btnGuardar.setOnClickListener {

            findNavController().navigate(R.id.detailProduct)

        }
        ivVolver.setOnClickListener {

            //popBackStack es para volver al fragment anterior
            findNavController().popBackStack()

        }




    }



}