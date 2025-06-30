package mx.edu.potros.gestioninventarios.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import mx.edu.potros.gestioninventarios.R

class ContraseniaActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contra1)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val restCorreo: EditText = findViewById(R.id.correoRest)
        val codigo: EditText = findViewById(R.id.codigoRest)
        val btnRestContra: Button = findViewById(R.id.btnRestContra)

        btnRestContra.setOnClickListener {
            val correoTexto = restCorreo.text.toString().trim()
            val codigoTexto = codigo.text.toString().trim()

            // Validaciones
            if (correoTexto.isEmpty() || codigoTexto.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!correoTexto.contains("@")) {
                Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Si pasa validación, continuar
            val intento = Intent(this, ContraseniaActivity2::class.java)
            startActivity(intento)
            finish()
        }
    }
}
