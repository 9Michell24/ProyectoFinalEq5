package mx.edu.potros.gestioninventarios.objetoNegocio

data class Usuario (var idUsuario : String = "",
                    var nombre : String,
                    var correo : String,
                    var nacimiento : String,
                    var genero : String,
                    var contra : String,
                    var direccion_foto : String,
                    var listaCategoria: ArrayList<Categoria> = arrayListOf(),
                    var ListaArticulo : ArrayList<Articulo> = arrayListOf(),
                    var listaEntradasSalidas : ArrayList<EntradasSalidas> = arrayListOf()

)