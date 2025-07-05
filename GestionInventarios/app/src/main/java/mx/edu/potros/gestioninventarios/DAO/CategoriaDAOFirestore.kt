package mx.edu.potros.gestioninventarios.DAO

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria

class CategoriaDAOFirestore(private val usuarioId: String) : ICategoriaDAO {

    private val collection = Firebase.firestore
        .collection("usuarios")
        .document(usuarioId)
        .collection("categorias")


    override fun guardarCategoria(
        categoria: Categoria,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = collection.document()
        val idGenerado = docRef.id
        val categoriaConId = categoria.copy(idCategoria = idGenerado)

        docRef.set(categoriaConId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun editarCategoria(
        categoria: Categoria,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (categoria.idCategoria.isBlank()) {
            onFailure(Exception("ID de la categoria vacío"))
            return
        }

        collection.document(categoria.idCategoria)
            .set(categoria)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun eliminarCategoria(
        idCategoria: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.document(idCategoria)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun obtenerCategoriaPorId(
        idCategoria: String,
        onSuccess: (Categoria?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.document(idCategoria)
            .get()
            .addOnSuccessListener { snapshot ->
                val categoria = snapshot.toObject(Categoria::class.java)
                onSuccess(categoria)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun obtenerTodosLasCategorias(
        onSuccess: (ArrayList<Categoria>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Categoria::class.java)
                }
                onSuccess(ArrayList(lista))
            }
            .addOnFailureListener { e -> onFailure(e) }
    }


}