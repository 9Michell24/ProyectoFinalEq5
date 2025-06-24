package mx.edu.potros.gestioninventarios.ui.movement

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
import mx.edu.potros.gestioninventarios.databinding.FragmentMovementBinding

class MovementFragment : Fragment() {

    private var _binding: FragmentMovementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(MovementViewModel::class.java)

        _binding = FragmentMovementBinding.inflate(inflater, container, false)
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

        val btnGuar : Button = view.findViewById(R.id.btnGuardarEntradasSalidas)
        btnGuar.setOnClickListener {

            findNavController().navigate(R.id.categoriesFragment)

        }

        ivVolver.setOnClickListener {

            //popBackStack es para volver al fragment anterior
            findNavController().popBackStack()

        }
    }
}