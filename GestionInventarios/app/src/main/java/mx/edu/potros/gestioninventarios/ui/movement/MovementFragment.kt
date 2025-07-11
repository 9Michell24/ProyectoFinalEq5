package mx.edu.potros.gestioninventarios.ui.movement

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentMovementBinding
import java.util.*

class MovementFragment : Fragment() {

    private var _binding: FragmentMovementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivVolver: ImageView = view.findViewById(R.id.regresar)
        val btnGuar: Button = view.findViewById(R.id.btnGuardarEntradasSalidas)
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        val editTextFecha = view.findViewById<EditText>(R.id.fechaArticulos)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        val radioEntradas = view.findViewById<RadioButton>(R.id.radioEntradas)
        val radioSalidas = view.findViewById<RadioButton>(R.id.radioSalidas)

        // Toggle visual con RadioButtons
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioEntradas -> {
                    radioEntradas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary1))
                    radioEntradas.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                    radioSalidas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    radioSalidas.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary1))
                }

                R.id.radioSalidas -> {
                    radioSalidas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary1))
                    radioSalidas.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                    radioEntradas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    radioEntradas.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary1))
                }
            }
        }

        // Spinner
        val adapter = ArrayAdapter.createFromResource(
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
                if (position != 0) {
                    val opcionSeleccionada = parent.getItemAtPosition(position).toString()
                    Toast.makeText(requireContext(), "Seleccionaste: $opcionSeleccionada", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Calendario
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

        // Botón guardar
        btnGuar.setOnClickListener {
            if (spinner.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "Por favor selecciona un artículo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            findNavController().navigate(R.id.categoriesFragment)
        }

        // Botón regresar
        ivVolver.setOnClickListener {
            findNavController().popBackStack()
        }
    }
<<<<<<< Updated upstream
=======



    fun guardarEntradaSalida(){

        var articuloEntraSal = Articulo()
        val partes = articulo.split(" - ")
        val nombre = partes[0]
        val categoria = partes[1]
        var position = 0

        for ((i,e) in DataProvider.listaArticulos.withIndex()){
            if(e.nombre.equals(nombre) && e.categoria.nombre.equals(categoria)){
                articuloEntraSal = e
                position = i

            }
        }

        var disponibilidad = 0
        for (e in DataProvider.listaEntradasSalidas){
            if (e.articulo.idArticulo.equals(articuloEntraSal.idArticulo)){
                if (e.isEntrada){
                    disponibilidad += e.cantidad
                }
                else{
                    disponibilidad -= e.cantidad
                }
            }
        }

Log.d("dispo" ,disponibilidad.toString())
        if(entrada == false ) {
            if ((disponibilidad - cantidad) < 0) {
                Toast.makeText(requireContext(), "Stock insuficiente", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val entraSal = EntradasSalidas("", articuloEntraSal, cantidad, fecha, motivo, entrada)
        DataProvider.entradasSalidasDAO.guardarEntraSal(entraSal,
            onSuccess = {
                Toast.makeText(requireContext(), "Se guardo", Toast.LENGTH_SHORT).show()

                DataProvider.cargarDatos {
                    var contador = 0
                    for (e in DataProvider.listaEntradasSalidas) {
                        if (e.articulo.categoria.nombre.equals(articuloEntraSal.categoria.nombre)) {
                            if(e.isEntrada){
                                contador += e.cantidad
                            }
                            else{
                                contador -= e.cantidad
                            }
                        }
                    }

                    val bundle = Bundle().apply {
                        putInt("position", position) // Esto es la posición de la categoría en la lista
                        putInt("totalArticles", contador) // Esto es el total de artículos en esa categoría
                    }

                    findNavController().navigate(R.id.categoriesFragment, bundle)
                }

            },
            onFailure = {
                Toast.makeText(requireContext(), "Se guardo", Toast.LENGTH_SHORT).show()
            })



    }


>>>>>>> Stashed changes
}
