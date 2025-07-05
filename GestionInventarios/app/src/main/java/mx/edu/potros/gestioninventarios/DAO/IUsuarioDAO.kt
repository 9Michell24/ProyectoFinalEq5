package mx.edu.potros.gestioninventarios.DAO

import com.google.firebase.auth.FirebaseUser
import mx.edu.potros.gestioninventarios.objetoNegocio.Usuario

interface IUsuarioDAO {


    fun registrarConFirebaseAuthYGuardarFirestore(
        email: String,
        password: String,
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )


    fun iniciarSesion(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    )


    fun obtenerUsuarioActual(): FirebaseUser?


    fun cerrarSesion(){
    }






    fun editarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun eliminarUsuario(
        idUsuario: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun obtenerUsuarioPorId(
        idUsuario: String,
        onSuccess: (Usuario?) -> Unit,
        onFailure: (Exception) -> Unit
    )

}