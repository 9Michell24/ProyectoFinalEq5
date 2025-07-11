package mx.edu.potros.gestioninventarios.activities

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val email = intent.extras!!.getString("user")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView


        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_add_item, R.id.navigation_movement,
                R.id.navigation_report
            )
        )

        navView.setupWithNavController(navController)





        val rootView = findViewById<View>(android.R.id.content)

        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)

            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val isKeyboardOpen = keypadHeight > screenHeight * 0.15

            val currentDestinationId = navController.currentDestination?.id
            val ocultarPorPantalla = currentDestinationId in listOf(
                R.id.detailProduct,
                R.id.newArticulo,
                R.id.configFragment,
                R.id.newCategories
            )

            if (isKeyboardOpen || ocultarPorPantalla) {
                binding.navView.visibility = View.GONE
            } else {
                binding.navView.visibility = View.VISIBLE
            }
        }



    }


}