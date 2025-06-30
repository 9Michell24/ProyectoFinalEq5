package mx.edu.potros.gestioninventarios.ui.report

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentReportBinding

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ReportViewModel::class.java)

        _binding = FragmentReportBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvSeeAllProducts: TextView = view.findViewById(R.id.see_articles_reports)

        val ivFilterDates : ImageView = view.findViewById(R.id.iv_dates_report)

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Selecciona un rango de fechas")
                .build()

        ivFilterDates.setOnClickListener {
            dateRangePicker.show(parentFragmentManager, "DATE_RANGE_PICKER")
        }

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second
            // Aquí puedes usar startDate y endDate
        }


        tvSeeAllProducts.setOnClickListener {

            //popBackStack es para volver al fragment anterior
            findNavController().navigate(R.id.allProdutsFragment)

        }
    }


}