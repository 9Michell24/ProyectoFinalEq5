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
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider.articulosActuales
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider.listaCategorias
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider.listaEntradasSalidas
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
                tvDatesText.text = "Desde: ${sdf.format(Date(startDate))} hasta ${sdf.format(Date(endDate))}"
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
        var listaCategoriasStrings = mutableListOf<String>()

        for(e in listaCategorias){
            listaCategoriasStrings.add(e.nombre)
        }

        for (e in listaEntradasSalidas) {
            if (listaCategoriasStrings.contains(e.articulo.categoria.nombre)) {
                if (e.isEntrada) {
                    cantidad += e.cantidad
                } else {
                    cantidad -= e.cantidad
                }
            }

        }

        view.findViewById<TextView>(R.id.tv_all_articles_report).text = cantidad.toString()

        tvSeeAllProducts.setOnClickListener {
            findNavController().navigate(R.id.allProdutsFragment)
        }

        adaptador = AdaptadorListaReport(view.context, ArrayList(DataProvider.listaCategorias))
        val gridView: GridView = view.findViewById(R.id.list_all_movements)
        gridView.adapter = adaptador

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
                    if (e.isEntrada) up += e.cantidad else down += e.cantidad
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

            // 🚀 Calcula pesos para las barras proporcionales
            val total = up + down
            val upWeight = if (total > 0) up.toFloat() / total else 0f
            val downWeight = if (total > 0) down.toFloat() / total else 0f

            val paramsUp = fondoUp.layoutParams as LinearLayout.LayoutParams
            paramsUp.weight = upWeight
            fondoUp.layoutParams = paramsUp

            val paramsDown = fondoDown.layoutParams as LinearLayout.LayoutParams
            paramsDown.weight = downWeight
            fondoDown.layoutParams = paramsDown

            vista.setOnClickListener {
                Toast.makeText(context, "No implementado aún", Toast.LENGTH_SHORT).show()
            }

            return vista
        }
    }
}


