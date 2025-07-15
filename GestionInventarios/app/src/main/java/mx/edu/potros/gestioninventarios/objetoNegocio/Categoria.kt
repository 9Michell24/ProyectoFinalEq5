package mx.edu.potros.gestioninventarios.objetoNegocio

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Categoria ( var idCategoria: String = "",
                       var nombre: String = "",
                       var color: String = ""
) : Parcelable
