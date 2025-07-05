package mx.edu.potros.gestioninventarios.DAO

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo

class ArticuloDAOFirestore(private val usuarioId: String) : IArticuloDAO {


    private val collection = Firebase.firestore
        .collection("usuarios")
        .document(usuarioId)
        .collection("articulos")


    override fun guardarArticulo(
        articulo: Articulo,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = collection.document()
        val idGenerado = docRef.id
        val articuloConId = articulo.copy(idArticulo = idGenerado)

        docRef.set(articuloConId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun editarArticulo(
        articulo: Articulo,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (articulo.idArticulo.isBlank()) {
            onFailure(Exception("ID del artículo vacío"))
            return
        }

        collection.document(articulo.idArticulo)
            .set(articulo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun eliminarArticulo(
        idArticulo: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.document(idArticulo)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun obtenerArticuloPorId(
        idArticulo: String,
        onSuccess: (Articulo?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.document(idArticulo)
            .get()
            .addOnSuccessListener { snapshot ->
                val articulo = snapshot.toObject(Articulo::class.java)
                onSuccess(articulo)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun obtenerTodosLosArticulos(
        onSuccess: (List<Articulo>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Articulo::class.java)
                }
                onSuccess(lista)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }




}
