package mx.edu.potros.gestioninventarios.DAO

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.EntradasSalidas

class EntraSalDAOFirestore(private val usuarioId: String) : IEntraSalDAO {

    private val collection = Firebase.firestore
        .collection("usuarios")
        .document(usuarioId)
        .collection("entradas_salidas")


    override fun guardarEntraSal(
        entradasSalidas: EntradasSalidas,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = collection.document()
        val idGenerado = docRef.id
        val entradasSalidasConId = entradasSalidas.copy(idES = idGenerado)

        docRef.set(entradasSalidasConId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun editarEntraSal(
        entradasSalidas: EntradasSalidas,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (entradasSalidas.idES.isBlank()) {
            onFailure(Exception("ID de la EntraSal esta vacío"))
            return
        }

        collection.document(entradasSalidas.idES)
            .set(entradasSalidas)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun eliminarEntraSal(
        idES: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.document(idES)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun obtenerEntraSalPorId(
        idES: String,
        onSuccess: (EntradasSalidas?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.document(idES)
            .get()
            .addOnSuccessListener { snapshot ->
                val entradasSalidas = snapshot.toObject(EntradasSalidas::class.java)
                onSuccess(entradasSalidas)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun obtenerTodosLasEntraSal(
        onSuccess: (ArrayList<EntradasSalidas>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(EntradasSalidas::class.java)
                }
                onSuccess(ArrayList(lista))
            }
            .addOnFailureListener { e -> onFailure(e) }
    }


}