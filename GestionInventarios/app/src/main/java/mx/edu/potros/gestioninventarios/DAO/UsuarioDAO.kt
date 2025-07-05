package mx.edu.potros.gestioninventarios.DAO

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import mx.edu.potros.gestioninventarios.objetoNegocio.Usuario

class UsuarioDAO : IUsuarioDAO {


    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val usuariosRef = db.collection("usuarios")


    override fun registrarConFirebaseAuthYGuardarFirestore(
        email: String,
        password: String,
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    usuario.idUsuario = uid
                    usuariosRef.document(uid)
                        .set(usuario)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e) }
                } else {
                    onFailure(Exception("UID nulo después del registro"))
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun iniciarSesion(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    onSuccess(user)
                } else {
                    onFailure(Exception("Usuario no encontrado"))
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun obtenerUsuarioPorId(
        idUsuario: String,
        onSuccess: (Usuario?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usuariosRef.document(idUsuario)
            .get()
            .addOnSuccessListener { doc ->
                val usuario = doc.toObject(Usuario::class.java)
                onSuccess(usuario)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun editarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usuariosRef.document(usuario.idUsuario)
            .set(usuario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun eliminarUsuario(
        idUsuario: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usuariosRef.document(idUsuario)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun obtenerUsuarioActual(): FirebaseUser? {
        return auth.currentUser
    }

    override fun cerrarSesion() {
        auth.signOut()
    }
}
