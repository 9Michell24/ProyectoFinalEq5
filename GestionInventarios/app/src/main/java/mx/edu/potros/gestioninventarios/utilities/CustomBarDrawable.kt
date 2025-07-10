package mx.edu.potros.gestioninventarios.utilities

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Articulo
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider

class CustomBarDrawable(
    private val context: Context,
    private val categoria: Categoria
) : Drawable() {

    override fun draw(canvas: Canvas) {
        val paintEntrada = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.parseColor(categoria.color)
        }

        val paintSalida = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.gray)
        }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 30f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        val totalWidth = canvas.width - 20f
        val height = canvas.height.toFloat()

        // calcular totales
        var entradas = 0
        var salidas = 0

        for (e in DataProvider.listaEntradasSalidas) {
            if (e.articulo.categoria.nombre == categoria.nombre) {
                if (e.isEntrada) entradas += e.cantidad else salidas += e.cantidad
            }
        }

        val total = entradas + salidas
        if (total == 0) return

        val anchoEntradas = (entradas.toFloat() / total) * totalWidth
        val anchoSalidas = (salidas.toFloat() / total) * totalWidth

        val rectEntrada = RectF(0f, 0f, anchoEntradas, height)
        val rectSalida = RectF(anchoEntradas, 0f, anchoEntradas + anchoSalidas, height)

        // dibujar las barras
        canvas.drawRect(rectEntrada, paintEntrada)
        canvas.drawRect(rectSalida, paintSalida)

        // dibujar los textos
        val centerY = height / 2 - ((textPaint.descent() + textPaint.ascent()) / 2)

        if (entradas > 0) {
            canvas.drawText(
                entradas.toString(),
                rectEntrada.centerX(),
                centerY,
                textPaint
            )
        }

        if (salidas > 0) {
            canvas.drawText(
                salidas.toString(),
                rectSalida.centerX(),
                centerY,
                textPaint
            )
        }
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter?) {}
    override fun getOpacity(): Int = PixelFormat.OPAQUE
}
