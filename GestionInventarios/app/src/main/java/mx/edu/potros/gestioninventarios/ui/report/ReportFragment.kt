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
import mx.edu.potros.gestioninventarios.utilities.CustomCircleDrawable
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private var adaptador: AdaptadorListaReport? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val reportViewModel =
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
        val ivFilterDates: ImageView = view.findViewById(R.id.iv_dates_report)
        val tvDatesText: TextView = view.findViewById(R.id.dates_text_report)
        val graphicHome: ImageView = view.findViewById(R.id.graphic_home)

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

        // calcular total de artículos actuales
        var cantidad = 0
        for (e in DataProvider.listaEntradasSalidas) {
            cantidad += if (e.isEntrada) e.cantidad else -e.cantidad
        }

        view.findViewById<TextView>(R.id.tv_all_articles_report).text = cantidad.toString()

        tvSeeAllProducts.setOnClickListener {
            findNavController().navigate(R.id.allProdutsFragment)
        }

        adaptador = AdaptadorListaReport(view.context, ArrayList(DataProvider.listaCategorias))
        val gridView: GridView = view.findViewById(R.id.list_all_movements)
        gridView.adapter = adaptador

        // ✅ Aquí asignamos la gráfica circular
        graphicHome.background = CustomCircleDrawable(requireContext(), DataProvider.listaCategorias)
    }

    private class AdaptadorListaReport(
        var context: Context,
        var categorias: ArrayList<Categoria>
    ) : BaseAdapter() {

        override fun getCount(): Int = categorias.size

        override fun getItem(position: Int): Any = categorias[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val categoria = categorias[position]
            val inflador = LayoutInflater.from(context)
            val vista = inflador.inflate(R.layout.design_category_report_list, null)

            val tvTitle: TextView = vista.findViewById(R.id.tv_category_name_report)
            val tvNumberUp: TextView = vista.findViewById(R.id.tv_category_number_up_report)
            val tvNumberDown: TextView = vista.findViewById(R.id.tv_category_number_down_report)
            val fondoUp: LinearLayout = vista.findViewById(R.id.report_list_category_up)
            val fondoDown: LinearLayout = vista.findViewById(R.id.report_list_category_down)

            var up = 0
            var down = 0

            for (e in DataProvider.listaEntradasSalidas) {
                if (e.articulo.categoria.nombre == categoria.nombre) {
                    if (e.isEntrada) {
                        up += e.cantidad
                    } else {
                        down += e.cantidad
                    }
                }
            }

            tvTitle.text = categoria.nombre
            tvNumberUp.text = up.toString()
            tvNumberDown.text = down.toString()

            val color = Color.parseColor(categoria.color)

            val drawableUp = GradientDrawable().apply {
                cornerRadius = 40f
                setColor(color)
            }

            val drawableDown = GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.parseColor("#8A8A8A"))
            }

            fondoUp.background = drawableUp
            fondoDown.background = drawableDown

            vista.setOnClickListener {
                Toast.makeText(context, "No implementado aún", Toast.LENGTH_SHORT).show()
            }

            return vista
        }
    }
}



