package mx.edu.potros.gestioninventarios.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import mx.edu.potros.gestioninventarios.R

class ContraseniaActivity1 : AppCompatActivity() {

    private lateinit var fireBaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contra1)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fireBaseAuth = Firebase.auth

        val restCorreo: EditText = findViewById(R.id.correoRest)
      //  val codigo: EditText = findViewById(R.id.codigoRest)
        val btnRestContra: Button = findViewById(R.id.btnRestContra)

        btnRestContra.setOnClickListener {
            val correoTexto = restCorreo.text.toString().trim()
       //     val codigoTexto = codigo.text.toString().trim()

            // Validaciones
//            if (correoTexto.isEmpty() || codigoTexto.isEmpty()) {
//                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            if (!correoTexto.contains("@")) {
                Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Si pasa validación, continuar
//            val intento = Intent(this, ContraseniaActivity2::class.java)
//            startActivity(intento)
//            finish()



            sendPasswordReset(correoTexto)


        }
    }

    private fun sendPasswordReset(email: String){

        fireBaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(){ task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Correo de restablecer contraseña enviado", Toast.LENGTH_SHORT).show()



                    Handler(Looper.getMainLooper()).postDelayed({
                        val intento = Intent(this, LoginActivity::class.java)
                        startActivity(intento)
                        finish()
                    }, 3000)


                }
                else{
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }


            }
    }


}
