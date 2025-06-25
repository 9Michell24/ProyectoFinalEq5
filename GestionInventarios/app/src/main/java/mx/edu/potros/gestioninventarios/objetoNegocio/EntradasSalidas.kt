package mx.edu.potros.gestioninventarios.objetoNegocio

data class EntradasSalidas (
        var articulo : Articulo,
        var cantidad : Int,
        var fecha : String,
        var motivo : String,
        var isEntrada : Boolean
)