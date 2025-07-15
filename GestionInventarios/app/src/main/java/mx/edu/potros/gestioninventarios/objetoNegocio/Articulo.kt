package mx.edu.potros.gestioninventarios.objetoNegocio

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Articulo (var idArticulo : String = "",
                     var nombre: String = "",
                     var cantidad: Int = 0,
                     var descripcion: String = "",
                     var costo : Float = 0f,
                     var categoria: Categoria = Categoria(),
                     var imagenUrl : String = ""
    ): Parcelable