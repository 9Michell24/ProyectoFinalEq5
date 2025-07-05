package mx.edu.potros.gestioninventarios.DAO

import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria

interface ICategoriaDAO {


    fun guardarCategoria(
        categoria: Categoria,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun editarCategoria(
        categoria: Categoria,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun eliminarCategoria(
        idCategoria: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun obtenerCategoriaPorId(
        idArticulo: String,
        onSuccess: (Categoria?) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun obtenerTodosLasCategorias(
        onSuccess: (ArrayList<Categoria>) -> Unit,
        onFailure: (Exception) -> Unit
    )


}