package mx.edu.potros.gestioninventarios.objetoNegocio

data class Articulo (var nombre: String,
                     var cantidad: Int,
                     var descripcion: String,
                     var costo : Float,
                     var categoria: Categoria,
                     var imagenUrl : String
    )