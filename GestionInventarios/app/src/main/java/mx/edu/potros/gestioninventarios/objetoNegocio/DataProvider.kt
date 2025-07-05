package mx.edu.potros.gestioninventarios.objetoNegocio

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import mx.edu.potros.gestioninventarios.DAO.ArticuloDAOFirestore
import mx.edu.potros.gestioninventarios.DAO.CategoriaDAOFirestore
import mx.edu.potros.gestioninventarios.DAO.EntraSalDAOFirestore
import mx.edu.potros.gestioninventarios.R

object DataProvider {

    var listaArticulos : ArrayList<Articulo> = arrayListOf()
    var listaCategorias : ArrayList<Categoria> = arrayListOf()
    var listaEntradasSalidas : ArrayList<EntradasSalidas> = arrayListOf()

    val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val categoriaDAO = CategoriaDAOFirestore(usuarioId)
    val articuloDAO = ArticuloDAOFirestore(usuarioId)
    val entradasSalidasDAO = EntraSalDAOFirestore(usuarioId)

    var articulosActuales = 0




    fun cargarDatos(
        adaptadorCategorias: BaseAdapter? = null,
        adaptadorArticulos: BaseAdapter? = null,
        adaptadorEntraSal: BaseAdapter? = null,

        alFinalizarEntradas: (() -> Unit)? = null

    ){

        listaCategorias.clear()
        listaArticulos.clear()
        listaEntradasSalidas.clear()


        categoriaDAO.obtenerTodosLasCategorias(
            onSuccess = { lista ->
                listaCategorias.clear()
                listaCategorias.addAll(lista)
                adaptadorCategorias?.notifyDataSetChanged()
                Log.d("SIze cate", listaCategorias.size.toString())

            },
            onFailure = { error ->
                Log.d("Error en obtener todas las categorias", "checa")
            }
        )

        articuloDAO.obtenerTodosLosArticulos(
            onSuccess = { lista ->
                listaArticulos.clear()
                listaArticulos.addAll(lista)
                adaptadorArticulos?.notifyDataSetChanged()
                Log.d("SIze arti", listaArticulos.size.toString())

            },
            onFailure = {
                Log.d("Error en obtener todas los articulos", "checa")
            }
        )

        entradasSalidasDAO.obtenerTodosLasEntraSal(
            onSuccess = { lista ->
                listaEntradasSalidas.clear()
                listaEntradasSalidas.addAll(lista)
                adaptadorEntraSal?.notifyDataSetChanged()
                Log.d("SIze entraSal", listaEntradasSalidas.size.toString())

                articulosActuales = 0
                for (e in listaEntradasSalidas) {
                    if (e.isEntrada) {
                        articulosActuales += e.cantidad
                    } else {
                        articulosActuales -= e.cantidad
                    }
                }

                // ✅ Ejecutar callback cuando termine
                alFinalizarEntradas?.invoke()
            },
            onFailure = { error ->
                Log.d("Error en obtener todas las entradasSalidas", "checa")
            }
        )


    }



//    fun obtenerCategoriaPorNombre(nombre: String): Categoria {
//        return listaCategorias.find { it.nombre == nombre }
//            ?: Categoria(nombre, "#000000") // por si no la encuentra, crea una por defecto
//    }
}