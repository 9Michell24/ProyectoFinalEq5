package mx.edu.potros.gestioninventarios.objetoNegocio

import android.os.Parcelable
import kotlinx.parcelize.Parcelize



@Parcelize
data class EntradasSalidas (
        var idES : String = "",
        var articulo : Articulo = Articulo(),
        var cantidad : Int = 0,
        var fecha : String = "",
        var motivo : String = "",
        var isEntrada : Boolean = true
) : Parcelable