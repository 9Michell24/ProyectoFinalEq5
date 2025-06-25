package mx.edu.potros.gestioninventarios

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContraseniaActivity2: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contra2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val contra: EditText = findViewById(R.id.contraRest)
        val contraConf: EditText = findViewById(R.id.contraConf)
        val btnConfContra: Button = findViewById(R.id.btnConfContra)

        btnConfContra.setOnClickListener {
            val contraTexto = contra.text.toString().trim()
            val contraConfTexto = contraConf.text.toString().trim()

            // Validaciones
            if (contraTexto.isEmpty() || contraConfTexto.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contraTexto != contraConfTexto) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Si pasa validación, continuar
            val intento = Intent(this, LoginActivity::class.java)
            startActivity(intento)
            finish()
        }
    }
}