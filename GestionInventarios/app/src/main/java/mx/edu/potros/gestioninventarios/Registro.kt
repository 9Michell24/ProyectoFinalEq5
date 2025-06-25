package mx.edu.potros.gestioninventarios

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nombre: EditText = findViewById(R.id.nombreRegistro)
        val correo: EditText = findViewById(R.id.correoRegistro)
        val contra: EditText = findViewById(R.id.contrasena)
        val confContra: EditText = findViewById(R.id.confirmContrasena)
        val ivRegresar: ImageView = findViewById(R.id.regresar)

        ivRegresar.setOnClickListener {
            val intento = Intent(this, LoginActivity::class.java)
            startActivity(intento)
            finish()
        }
        val btnRegistrar : Button = findViewById(R.id.btnRegistro)
        btnRegistrar.setOnClickListener {
            val nombreTexto = nombre.text.toString().trim()
            val correoTexto = correo.text.toString().trim()
            val contraTexto = contra.text.toString().trim()
            val confContraTexto = confContra.text.toString().trim()

            // Validaciones
            if (nombreTexto.isEmpty() || correoTexto.isEmpty() ||
                contraTexto.isEmpty() || confContraTexto.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!correoTexto.contains("@")) {
                Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contraTexto != confContraTexto) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Si todo es válido, ir a Login
            val intento = Intent(this, LoginActivity::class.java)
            startActivity(intento)
            finish()

        }

    }

}