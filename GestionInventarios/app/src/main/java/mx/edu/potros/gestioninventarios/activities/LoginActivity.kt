package mx.edu.potros.gestioninventarios.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import mx.edu.potros.gestioninventarios.DAO.UsuarioDAO
import mx.edu.potros.gestioninventarios.R

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    val usuarioDao = UsuarioDAO()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val correo: EditText = findViewById(R.id.correoIni)
        val contraseña: EditText = findViewById(R.id.contraIni)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnRegister: Button = findViewById(R.id.btnRegistrar)
        val textClick: TextView = findViewById(R.id.textClick)

        btnLogin.setOnClickListener {
            val correoTexto = correo.text.toString().trim()
            val contraTexto = contraseña.text.toString().trim()

            // Validaciones
            if (!correoTexto.contains("@")) {
                Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contraTexto.isEmpty()) {
                Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            login(correoTexto, contraTexto)

        }



        btnRegister.setOnClickListener {
            val intento = Intent(this, Registro::class.java)
            startActivity(intento)
        }

        textClick.setOnClickListener {
            val intento = Intent(this, ContraseniaActivity1::class.java)
            startActivity(intento)
        }


    }



    public override fun onStart() {
        super.onStart()


        val currentUser = usuarioDao.obtenerUsuarioActual()
        if(currentUser != null){
            goToMain(currentUser)
        }
    }



    fun login(email: String, password: String){
        usuarioDao.iniciarSesion(email, password,
            onSuccess = { user ->
                goToMain(user)
            },
            onFailure = { e ->
                Toast.makeText(this, "Credenciales incorrectas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }




    fun goToMain(user: FirebaseUser){
        val intent= Intent(this, MainActivity::class.java)
        intent.putExtra("user", user.email)
        startActivity(intent)
        finish()
    }



}