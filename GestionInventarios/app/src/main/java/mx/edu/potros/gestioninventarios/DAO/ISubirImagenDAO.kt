package mx.edu.potros.gestioninventarios.DAO

import android.content.Context
import android.net.Uri

interface ISubirImagenDAO {

    fun subirImagen(
        uri: Uri,
        context: Context,
        callback: (String?) -> Unit
    )

}