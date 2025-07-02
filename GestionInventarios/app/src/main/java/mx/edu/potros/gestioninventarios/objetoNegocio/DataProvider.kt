package mx.edu.potros.gestioninventarios.objetoNegocio

object DataProvider {

    var listaArticulos : ArrayList<Articulo> = arrayListOf()
    var listaCategorias : ArrayList<Categoria> = arrayListOf()
    var listaEntradasSalidas : ArrayList<EntradasSalidas> = arrayListOf()

    public var cargado : Boolean = false


}