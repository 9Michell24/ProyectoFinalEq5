package mx.edu.potros.gestioninventarios.ui.report

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentReportBinding
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import mx.edu.potros.gestioninventarios.utilities.CustomBarDrawable
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this).get(ReportViewModel::class.java)
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvSeeAllProducts: TextView = view.findViewById(R.id.see_articles_reports)
        val ivFilterDates: ImageView = view.findViewById(R.id.iv_dates_report)
        val tvDatesText: TextView = view.findViewById(R.id.dates_text_report)

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Selecciona un rango de fechas")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        ivFilterDates.setOnClickListener {
            dateRangePicker.show(parentFragmentManager, "DATE_RANGE_PICKER")
        }

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            if (startDate != null && endDate != null) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val start = sdf.format(Date(startDate))
                val end = sdf.format(Date(endDate))

                tvDatesText.text = "Desde: $start hasta $end"
                tvDatesText.visibility = View.VISIBLE
            }
        }

        dateRangePicker.addOnNegativeButtonClickListener {
            tvDatesText.visibility = View.INVISIBLE
        }

        dateRangePicker.addOnCancelListener {
            tvDatesText.visibility = View.INVISIBLE
        }

        var cantidad = 0
        for (e in DataProvider.listaEntradasSalidas) {
            if (e.isEntrada) {
                cantidad += e.cantidad
            }
        }
        view.findViewById<TextView>(R.id.tv_all_articles_report).text = cantidad.toString()

        tvSeeAllProducts.setOnClickListener {
            findNavController().navigate(R.id.allProdutsFragment)
        }

        // 👇 Aquí llenamos las barras dinámicamente en lugar del GridView
        val contenedorBarras: LinearLayout = view.findViewById(R.id.list_all_movements)
        contenedorBarras.removeAllViews()

        for (categoria in DataProvider.listaCategorias) {
            val barra = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    50
                ).also {
                    it.topMargin = 8
                    it.bottomMargin = 8
                }
                background = CustomBarDrawable(requireContext(), categoria)
            }

            val texto = TextView(requireContext()).apply {
                text = categoria.nombre
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary1))
            }

            val wrapper = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.bottomMargin = 16
                }
                addView(texto)
                addView(barra)
            }

            contenedorBarras.addView(wrapper)
        }
    }
}



