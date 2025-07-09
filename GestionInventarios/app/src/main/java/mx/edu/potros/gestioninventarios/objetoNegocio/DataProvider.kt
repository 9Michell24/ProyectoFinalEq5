package mx.edu.potros.gestioninventarios.objetoNegocio

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.BaseAdapter
import com.google.firebase.auth.FirebaseAuth
import mx.edu.potros.gestioninventarios.DAO.ArticuloDAOFirestore
import mx.edu.potros.gestioninventarios.DAO.CategoriaDAOFirestore
import mx.edu.potros.gestioninventarios.DAO.EntraSalDAOFirestore
import mx.edu.potros.gestioninventarios.DAO.UsuarioDAO // Importa tu UsuarioDAO

object DataProvider {

    var listaArticulos : ArrayList<Articulo> = arrayListOf()
    var listaCategorias : ArrayList<Categoria> = arrayListOf()
    var listaEntradasSalidas : ArrayList<EntradasSalidas> = arrayListOf()

    val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val categoriaDAO = CategoriaDAOFirestore(usuarioId)
    val articuloDAO = ArticuloDAOFirestore(usuarioId)
    val entradasSalidasDAO = EntraSalDAOFirestore(usuarioId)
    val usuarioDAO = UsuarioDAO() // Instancia de tu UsuarioDAO aquí

    var articulosActuales = 0

    fun cargarDatos(
        adaptadorCategorias: BaseAdapter? = null,
        adaptadorArticulos: BaseAdapter? = null,
        adaptadorEntraSal: BaseAdapter? = null,
        alFinalizarEntradas: (() -> Unit)? = null
    ){

        limpiarDatos()

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
                alFinalizarEntradas?.invoke()
            },
            onFailure = { error ->
                Log.d("Error en obtener todas las entradasSalidas", "checa")
            }
        )
    }

    // Nuevo método en DataProvider para obtener los datos del usuario
    fun obtenerDatosUsuario(
        onSuccess: (Usuario?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            usuarioDAO.obtenerUsuarioPorId(uid,
                onSuccess = { usuario ->
                    onSuccess(usuario)
                },
                onFailure = { e ->
                    onFailure(e)
                }
            )
        } else {
            onFailure(Exception("Usuario no autenticado"))
        }
    }
//     Puedes añadir un método similar para editar, si también quieren centralizarlo aquí
    fun editarDatosUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usuarioDAO.editarUsuario(usuario, onSuccess, onFailure)
    }



    fun limpiarDatos(){

        listaCategorias.clear()
        listaArticulos.clear()
        listaEntradasSalidas.clear()

    }


}