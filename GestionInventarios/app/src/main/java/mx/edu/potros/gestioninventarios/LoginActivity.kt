package mx.edu.potros.gestioninventarios

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



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

            // Si pasa validación, iniciar sesión
            val intento = Intent(this, MainActivity::class.java)
            startActivity(intento)
            finish()
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
}