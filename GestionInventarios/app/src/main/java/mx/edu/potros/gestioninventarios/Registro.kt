package mx.edu.potros.gestioninventarios

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
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


        val ivRegresar : ImageView = findViewById(R.id.regresar)
        ivRegresar.setOnClickListener {

            var intento = Intent(this, LoginActivity::class.java)
            startActivity(intento)
            finish()

        }
        val btnRegistrar : Button = findViewById(R.id.btnRegistro)
        btnRegistrar.setOnClickListener {

            var intento = Intent(this, LoginActivity::class.java)
            startActivity(intento)

        }

    }




}