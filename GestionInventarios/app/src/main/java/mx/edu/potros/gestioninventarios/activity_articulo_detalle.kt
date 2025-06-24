package mx.edu.potros.gestioninventarios

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class activity_articulo_detalle : Fragment() {

    companion object {
        fun newInstance() = activity_articulo_detalle()
    }

    private val viewModel: ActivityArticuloDetalleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_activity_articulo_detalle, container, false)
    }
}