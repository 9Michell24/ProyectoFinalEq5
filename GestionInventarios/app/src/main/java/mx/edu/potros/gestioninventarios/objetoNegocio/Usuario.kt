package mx.edu.potros.gestioninventarios.objetoNegocio

data class Usuario (
    var idUsuario : String = "",
    var nombre : String = "", // ¡Añadido valor predeterminado!
    var correo : String = "", // ¡Añadido valor predeterminado!
    var nacimiento : String = "", // ¡Añadido valor predeterminado!
    var genero : String = "", // ¡Añadido valor predeterminado!
    var contra : String? = null,
    var direccion_foto : String? = null,
    var listaCategoria: ArrayList<Categoria> = arrayListOf(),
    var ListaArticulo : ArrayList<Articulo> = arrayListOf(),
    var listaEntradasSalidas : ArrayList<EntradasSalidas> = arrayListOf()
)