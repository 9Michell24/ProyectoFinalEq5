package mx.edu.potros.gestioninventarios.ui.report

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentReportBinding
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null

    private var adaptador : AdaptadorListaReport? = null


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

        var tvDatesText = view.findViewById<TextView>(R.id.dates_text_report)


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
            // Aquí puedes usar startDate y endDate


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


        var cantidad : Int = 0

        for(e in DataProvider.listaEntradasSalidas){
            if (e.isEntrada){
                cantidad = cantidad + e.cantidad
            }
        }

        view.findViewById<TextView>(R.id.tv_all_articles_report).setText(cantidad.toString())


        tvSeeAllProducts.setOnClickListener {

            //popBackStack es para volver al fragment anterior
            findNavController().navigate(R.id.allProdutsFragment)

        }


        adaptador = AdaptadorListaReport(view.context, ArrayList(DataProvider.listaCategorias))

        val gridView : GridView = view.findViewById(R.id.list_all_movements)

        gridView.adapter = adaptador





    }



    private class AdaptadorListaReport : BaseAdapter{

        var categorias = ArrayList<Categoria>()
        var context : Context? = null

        constructor(context: Context, categorias: ArrayList<Categoria>){
            this.context = context
            this.categorias = categorias
        }


        override fun getCount(): Int {
            return categorias.size
        }

        override fun getItem(position: Int): Any {
            return categorias[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var categoria = categorias[position]
            var inflador = LayoutInflater.from(context)
            var vista = inflador.inflate(R.layout.design_category_report_list, null)


            var tvTitle : TextView = vista.findViewById(R.id.tv_category_name_report)
            var tvNumberUp : TextView = vista.findViewById(R.id.tv_category_number_up_report)
            var tvNumberDown : TextView = vista.findViewById(R.id.tv_category_number_down_report)

            val fondoUp: LinearLayout = vista.findViewById(R.id.report_list_category_up)
            val fondoDown: LinearLayout = vista.findViewById(R.id.report_list_category_down)


            var up = 0
            var down = 0

            for (e in DataProvider.listaEntradasSalidas){
                if(e.articulo.categoria.nombre.equals(categoria.nombre)){
                    if(e.isEntrada){
                        up = up + e.cantidad
                    }
                    else{
                        down = down + e.cantidad
                    }

                }
            }

            tvTitle.setText(categoria.nombre)
            tvNumberUp.setText(up.toString())
           // tvNumberDown.setText(down.toString())


            val color = Color.parseColor(categoria.color)

            val drawableUp = GradientDrawable()
            drawableUp.cornerRadius = 40f
            drawableUp.setColor(color)

            val drawableDown = GradientDrawable()
            drawableDown.cornerRadius = 40f
            drawableDown.setColor(Color.parseColor("#8A8A8A"))


            fondoUp.background = drawableUp
            fondoDown.background = drawableDown

            vista.setOnClickListener{
                val bundle = Bundle().apply {
                    putInt("position", position)

                }
                //Navigation.findNavController(vista).navigate(R.id.categoriesFragment, bundle)
                Toast.makeText(context, "No implementado aun", Toast.LENGTH_SHORT).show()

            }

            return vista

        }

    }


}


