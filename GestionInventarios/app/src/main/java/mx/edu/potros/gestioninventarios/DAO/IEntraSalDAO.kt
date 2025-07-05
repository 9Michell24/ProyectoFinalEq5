package mx.edu.potros.gestioninventarios.DAO

import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.EntradasSalidas

interface IEntraSalDAO {


    fun guardarEntraSal(
        entradasSalidas: EntradasSalidas,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun editarEntraSal(
        entradasSalidas: EntradasSalidas,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun eliminarEntraSal(
        idEntradasSalidas: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun obtenerEntraSalPorId(
        idEs: String,
        onSuccess: (EntradasSalidas?) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun obtenerTodosLasEntraSal(
        onSuccess: (ArrayList<EntradasSalidas>) -> Unit,
        onFailure: (Exception) -> Unit
    )
}