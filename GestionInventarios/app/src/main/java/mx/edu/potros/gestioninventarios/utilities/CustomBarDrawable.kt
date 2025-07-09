package mx.edu.potros.gestioninventarios.utilities

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider

class CustomBarDrawable(
    private val context: Context,
    private val categoria: Categoria
) : Drawable() {

    private val salidaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.gray) // para salidas
    }

    private val entradaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(categoria.color) // color de la categoría para entradas
    }

    override fun draw(canvas: Canvas) {
        val ancho = (canvas.width - 10).toFloat()
        val alto = (canvas.height - 10).toFloat()
        val fondoRect = RectF(0f, 0f, ancho, alto)

        // Pintar fondo completo en gris por defecto
        canvas.drawRect(fondoRect, salidaPaint)

        var entradas = 0
        var salidas = 0

        // Contar entradas y salidas de esta categoría
        for (e in DataProvider.listaEntradasSalidas) {
            if (e.articulo.categoria.nombre == categoria.nombre) {
                if (e.isEntrada) {
                    entradas += e.cantidad
                } else {
                    salidas += e.cantidad
                }
            }
        }

        val total = entradas + salidas

        if (total > 0) {
            val anchoEntradas = (entradas.toFloat() / total) * ancho
            val entradaRect = RectF(0f, 0f, anchoEntradas, alto)

            // Pintar la sección de entradas encima
            canvas.drawRect(entradaRect, entradaPaint)
        }
    }

    override fun setAlpha(alpha: Int) {
        entradaPaint.alpha = alpha
        salidaPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        entradaPaint.colorFilter = colorFilter
        salidaPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE
}
