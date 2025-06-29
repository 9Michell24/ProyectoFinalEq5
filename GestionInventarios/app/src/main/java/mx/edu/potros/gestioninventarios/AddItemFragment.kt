package mx.edu.potros.gestioninventarios

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast

class AddItemFragment : Fragment() {

    companion object {
        fun newInstance() = AddItemFragment()
    }

    private val viewModel: AddItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_item2, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    // Spinner
        val spinnerCategoríaArtículo = view.findViewById<Spinner>(R.id.spinner)
    val adapter = ArrayAdapter.createFromResource(
        requireContext(),
        R.array.opciones_spinner,
        android.R.layout.simple_spinner_item
    )
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoríaArtículo.adapter = adapter

        spinnerCategoríaArtículo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>, view: View?, position: Int, id: Long
        ) {
            if (position != 0) {
                val opcionSeleccionada = parent.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Seleccionaste: $opcionSeleccionada", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}
}