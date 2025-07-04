package mx.edu.potros.gestioninventarios.objetoNegocio

object DataProvider {

    var listaArticulos : ArrayList<Articulo> = arrayListOf()
    var listaCategorias : ArrayList<Categoria> = arrayListOf()
    var listaEntradasSalidas : ArrayList<EntradasSalidas> = arrayListOf()

    public var cargado : Boolean = false

    fun obtenerCategoriaPorNombre(nombre: String): Categoria {
        return listaCategorias.find { it.nombre == nombre }
            ?: Categoria(nombre, "#000000") // por si no la encuentra, crea una por defecto
    }
}