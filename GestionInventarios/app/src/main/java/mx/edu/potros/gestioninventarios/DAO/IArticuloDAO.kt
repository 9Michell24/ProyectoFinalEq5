package mx.edu.potros.gestioninventarios.DAO

import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo

interface IArticuloDAO {

    fun guardarArticulo(
        articulo: Articulo,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun editarArticulo(
        articulo: Articulo,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun eliminarArticulo(
        idArticulo: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun obtenerArticuloPorId(
        idArticulo: String,
        onSuccess: (Articulo?) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun obtenerTodosLosArticulos(
        onSuccess: (List<Articulo>) -> Unit,
        onFailure: (Exception) -> Unit
    )
}
