package mx.edu.potros.gestioninventarios.objetoNegocio

data class Usuario (var nombre : String,
                    var correo : String,
                    var nacimiento : String,
                    var genero : String,
                    var contra : String,
                    var direccion_foto : String,
                    var ListaArticulo : ArrayList<Articulo>,
                    var listaEntradasSalidas : ArrayList<EntradasSalidas>

)