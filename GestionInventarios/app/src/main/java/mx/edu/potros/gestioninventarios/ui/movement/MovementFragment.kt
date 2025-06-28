package mx.edu.potros.gestioninventarios.ui.movement

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentMovementBinding
import java.util.Calendar

class MovementFragment : Fragment() {

    private var _binding: FragmentMovementBinding? = null
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
        val btnGuar: Button = view.findViewById(R.id.btnGuardarEntradasSalidas)
        val spinner = view.findViewById<android.widget.Spinner>(R.id.spinner)

        val adapter = android.widget.ArrayAdapter.createFromResource(
            requireContext(),
            R.array.opciones_spinner,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val opcionSeleccionada = parent.getItemAtPosition(position).toString()
                if (position != 0) {
                    Toast.makeText(
                        requireContext(),
                        "Seleccionaste: $opcionSeleccionada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Nada seleccionado
            }
        }

        val editTextFecha = view.findViewById<EditText>(R.id.fechaArticulos)
        editTextFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val año = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(), { _, y, m, d ->
                val fecha = String.format("%02d/%02d/%04d", d, m + 1, y)
                editTextFecha.setText(fecha)
            }, año, mes, dia)

            datePicker.show()
        }

        btnGuar.setOnClickListener {
            if (spinner.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "Por favor selecciona una artículo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.categoriesFragment)
        }

        ivVolver.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
