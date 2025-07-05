package mx.edu.potros.gestioninventarios.DAO

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import java.io.File
import java.io.FileOutputStream
import com.cloudinary.android.MediaManager

private const val UPLOAD_PRESET = "articulo-upload"
private const val CLOUD_NAME = "de7nyni5e"


object SubirImagenDAOCloudinary : ISubirImagenDAO {

    private var initialized = false

    private fun initCloudinary(context: Context) {
        if (!initialized) {
            val config = mapOf("cloud_name" to CLOUD_NAME)
            MediaManager.init(context.applicationContext, config)
            initialized = true
        }
    }

    override fun subirImagen(uri: Uri, context: Context, callback: (String?) -> Unit) {
        initCloudinary(context)

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val archivoTemp = File(context.cacheDir, "imagen_subida.jpg")
            val outputStream = FileOutputStream(archivoTemp)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.flush()
            outputStream.close()

            MediaManager.get().upload(archivoTemp.absolutePath)
                .unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d("Upload", "Subida iniciada")
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d("Upload", "Progreso: $bytes / $totalBytes")
                    }

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val url = resultData?.get("secure_url") as? String
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
            Log.e("Upload", "Excepción al subir imagen: ${e.message}")
            callback(null)
        }
    }




}