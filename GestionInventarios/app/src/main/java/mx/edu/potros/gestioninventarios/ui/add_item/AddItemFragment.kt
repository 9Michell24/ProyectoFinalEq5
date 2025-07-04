package mx.edu.potros.gestioninventarios.ui.add_item

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.databinding.FragmentAddItemBinding
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val categoriasSeleccionadas = ArrayList<String>()
    private lateinit var textoCategoria: TextView

    private val PICK_IMAGE_REQUEST = 1
    private var imagenSeleccionadaUri: Uri? = null

//    private val cloudinary = Cloudinary(ObjectUtils.asMap(
//        "cloud_name", "de7nyni5e",
//        "api_key", "839685883949533",
//        "upload_preset", "articulo-upload"
//    ))


    val CLOUD_NAME = "de7nyni5e"
    val UPLOAD_PRESET = "articulo-upload"

    var imageUri: Uri? = null







    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this).get(AddItemViewModel::class.java)
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initCloudinary()


        textoCategoria = view.findViewById(R.id.spinnerCategoríaArtículo)

        view.findViewById<ImageView>(R.id.regresar).setOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<LinearLayout>(R.id.ll_select_category_register_article).setOnClickListener {
            mostrarDialogoCategorias()
        }

        binding.profileIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.btnGuardarArt.setOnClickListener {
           // subirImagenYGuardarArticulo()



            if (imagenSeleccionadaUri != null) {
                uploadImagenDesdeUri(requireContext(), imagenSeleccionadaUri!!) { url ->
                    if (url != null) {
                        guardarArticuloEnFirestore(url)
                    } else {
                        Log.e("Upload", "Falló la subida de imagen")
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Selecciona una imagen primero", Toast.LENGTH_SHORT).show()
            }






            //Esto es para probar una imagen por defecto
//            crearImagenTemporalDesdeDrawable(requireContext(), R.drawable.grafica)
//
//            uploadImagen(requireContext()) { url ->
//                if (url != null) {
//                    guardarArticuloEnFirestore(url)
//                } else {
//                    Log.e("Upload", "Falló la subida de imagen")
//                }
//            }





        }










    }



    private fun initCloudinary(){
        val config: MutableMap<String, String> = HashMap<String, String>()
        config["cloud_name"] = CLOUD_NAME

        MediaManager.init(requireContext(), config)


    }





    fun uploadImagenDesdeUri(context: Context, uri: Uri, callback: (String?) -> Unit) {
        try {
            // Copiar el contenido del URI a un archivo temporal
            val inputStream = context.contentResolver.openInputStream(uri)
            val archivoTemp = File(context.cacheDir, "imagen_subida_desde_galeria.jpg")
            val outputStream = FileOutputStream(archivoTemp)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.flush()
            outputStream.close()

            // Subir el archivo temporal
            MediaManager.get().upload(archivoTemp.absolutePath)
                .unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d("Upload", "Iniciando subida desde galería...")
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d("Upload", "Progreso: $bytes/$totalBytes")
                    }

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val url = resultData?.get("url") as? String
                        Log.d("Upload", "Subida exitosa: $url")
                        callback(url)
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("Upload Error", "Error: ${error?.description}")
                        callback(null)
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.e("Upload Reintento", error?.description ?: "Sin detalles")
                    }
                }).dispatch()

        } catch (e: Exception) {
            Log.e("Upload", "Excepción al subir imagen desde Uri: ${e.message}")
            callback(null)
        }
    }





    fun uploadImagen(context: Context, callback: (String?) -> Unit) {
        // Usa directamente el archivo, no el Uri
        val archivoTemp = File(context.cacheDir, "imagen_prueba.png")

        if (archivoTemp.exists()) {
            MediaManager.get().upload(archivoTemp.absolutePath)
                .unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d("Upload", "Iniciando subida...")
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d("Upload", "Progreso: $bytes/$totalBytes")
                    }

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val url = resultData?.get("url") as? String
                        Log.d("Upload", "Subida exitosa: $url")
                        callback(url)
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("Upload Error", "Error: ${error?.description}")
                        callback(null)
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.e("Upload Reintento", error?.description ?: "Sin detalles")
                    }
                }).dispatch()
        } else {
            Log.e("Upload", "Archivo no existe")
            callback(null)
        }
    }





    fun crearImagenTemporalDesdeDrawable(context: Context, drawableId: Int) {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: throw Exception("Drawable no encontrado")
        val bitmap = (drawable as BitmapDrawable).bitmap

        val archivoTemp = File(context.cacheDir, "imagen_prueba.png")
        val outputStream = FileOutputStream(archivoTemp)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data?.data != null) {
            imagenSeleccionadaUri = data.data // 👈 Guardas el Uri
            binding.profileIcon.setImageURI(imagenSeleccionadaUri) // Muestras en ImageView
        }
    }


    private fun subirImagenYGuardarArticulo() {
        if (imagenSeleccionadaUri == null) {
            Toast.makeText(context, "Selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        MediaManager.get().upload(imagenSeleccionadaUri)
            .option("upload_preset", "articulo-upload")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    if (url != null) {
                        guardarArticuloEnFirestore(url)
                    } else {
                        Toast.makeText(context, "Error: URL no obtenida", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Toast.makeText(context, "Error al subir imagen :c", Toast.LENGTH_SHORT).show()
                    Log.e("Cloudinary", "Error: ${error?.description}")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }


    private fun guardarArticuloEnFirestore(imagenUrl: String) {
        val nombre = binding.nombreArticulo.text.toString().trim()
        val cantidad = binding.cantidadArticulo.text.toString().toIntOrNull() ?: 0
        val descripcion = binding.descripciNArticulo.text.toString().trim()
        val costo = binding.costo.text.toString().toFloatOrNull() ?: 0f
        val nombreCategoria = textoCategoria.text.toString()
        val categoria = DataProvider.listaCategorias.find { it.nombre == nombreCategoria }

        if (nombre.isBlank() || nombreCategoria.isBlank() || cantidad == 0 || descripcion.isBlank() || costo == 0f || categoria == null) {
            Toast.makeText(context, "Por favor llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val articulo = Articulo(
            imagenUrl = imagenUrl,
            nombre = nombre,
            cantidad = cantidad,
            categoria = categoria,
            descripcion = descripcion,
            costo = costo
        )

        Firebase.firestore.collection("articulos")
            .add(articulo)
            .addOnSuccessListener {
                Toast.makeText(context, "Artículo guardado correctamente", Toast.LENGTH_SHORT).show()
                binding.root.postDelayed({
                    findNavController().navigate(R.id.detailProduct)
                }, 1000)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar el artículo", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoCategorias() {
        val context = requireContext()
        val seleccionActual = categoriasSeleccionadas.firstOrNull()
        var categoriaSeleccionada = seleccionActual

        val radioGroup = RadioGroup(context).apply {
            orientation = RadioGroup.VERTICAL
            setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20))
        }

        DataProvider.listaCategorias.forEach { categoria ->
            val radioButton = RadioButton(context).apply {
                text = categoria.nombre
                textSize = 18f
                setPadding(20, 20, 20, 20)
                setTextColor(Color.parseColor(categoria.color))
                setTypeface(null, Typeface.BOLD)
                isChecked = categoria.nombre == seleccionActual
            }
            radioGroup.addView(radioButton)
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio = group.findViewById<RadioButton>(checkedId)
            categoriaSeleccionada = radio.text.toString()
        }

        val scrollView = ScrollView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(400)
            )
            addView(radioGroup)
        }

        val contenedor = FrameLayout(context).apply {
            addView(scrollView)
        }

        AlertDialog.Builder(context)
            .setTitle("Selecciona una Categoría")
            .setView(contenedor)
            .setPositiveButton("Aplicar") { _, _ ->
                categoriaSeleccionada?.let {
                    categoriasSeleccionadas.clear()
                    categoriasSeleccionadas.add(it)
                    textoCategoria.text = it

                    val color = DataProvider.listaCategorias.find { cat -> cat.nombre == it }?.color
                    textoCategoria.setTextColor(Color.parseColor(color ?: "#000000"))
                    textoCategoria.setTypeface(null, Typeface.BOLD)
                }
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Agregar Categoría") { _, _ ->
                mostrarDialogoAgregarCategoria()
            }
            .show()
    }

    private fun mostrarDialogoAgregarCategoria() {
        val context = requireContext()

        val dialogLayout = ScrollView(context)
        val innerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val inputNombre = EditText(context).apply {
            hint = "Nombre de la categoría"
        }

        val colorSeleccionado = arrayOf("#FF0000")
        var colorCambiado = false

        val btnColor = Button(context).apply {
            text = "Seleccionar color"
            setBackgroundColor(Color.parseColor(colorSeleccionado[0]))
            setOnClickListener {
                val initialColor = Color.parseColor(colorSeleccionado[0])
                AmbilWarnaDialog(context, initialColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onCancel(dialog: AmbilWarnaDialog?) {}
                    override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                        val nuevoColor = String.format("#%06X", 0xFFFFFF and color)
                        if (nuevoColor != "#FF0000") colorCambiado = true
                        colorSeleccionado[0] = nuevoColor
                        //btnColor.setBackgroundColor(color)
                    }
                }).show()
            }
        }

        innerLayout.addView(inputNombre)
        innerLayout.addView(TextView(context).apply {
            text = "Color de la categoría:"
            setPadding(0, 20, 0, 10)
        })
        innerLayout.addView(btnColor)
        dialogLayout.addView(innerLayout)

        val alerta = AlertDialog.Builder(context)
            .setTitle("Agregar Nueva Categoría")
            .setView(dialogLayout)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        alerta.setOnShowListener {
            val botonGuardar = alerta.getButton(AlertDialog.BUTTON_POSITIVE)
            botonGuardar.setOnClickListener {
                val nombre = inputNombre.text.toString().trim()

                if (nombre.isEmpty()) {
                    Toast.makeText(context, "Nombre no válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!colorCambiado) {
                    Toast.makeText(context, "Debes seleccionar un color distinto", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                DataProvider.listaCategorias.add(
                    Categoria(nombre = nombre, color = colorSeleccionado[0])
                )
                alerta.dismiss()
                mostrarDialogoCategorias()
            }
        }

        alerta.show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
