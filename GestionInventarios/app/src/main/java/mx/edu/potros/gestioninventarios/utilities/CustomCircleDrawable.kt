package mx.edu.potros.gestioninventarios.utilities

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import mx.edu.potros.gestioninventarios.R
import mx.edu.potros.gestioninventarios.objetoNegocio.Categoria
import mx.edu.potros.gestioninventarios.objetoNegocio.DataProvider

class CustomCircleDrawable(
    private val context: Context,
    private val categorias: List<Categoria>
) : Drawable() {

    private var coordenadas: RectF? = null
    private val grosorMetrica: Int
    private val grosorFondo: Int

    init {
        grosorMetrica = context.resources.getDimensionPixelSize(R.dimen.graphWidth)
        grosorFondo = context.resources.getDimensionPixelSize(R.dimen.graphBackground)
    }

    override fun draw(canvas: Canvas) {
        val fondo = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = grosorFondo.toFloat()
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            color = ContextCompat.getColor(context, R.color.gray)
        }

        val ancho = (canvas.width - 25).toFloat()
        val alto = (canvas.height - 25).toFloat()
        coordenadas = RectF(25f, 25f, ancho, alto)

        // Fondo gris completo
        canvas.drawArc(coordenadas!!, 0f, 360f, false, fondo)

        // calcular cantidad actual de artículos por categoría
        val cantidadPorCategoria = mutableMapOf<Categoria, Int>()
        var totalArticulos = 0f

        for (categoria in categorias) {
            var suma = 0
            for (e in DataProvider.listaEntradasSalidas) {
                if (e.articulo.categoria.nombre == categoria.nombre) {
                    if (e.isEntrada) suma += e.cantidad else suma -= e.cantidad
                }
            }
            if (suma < 0) suma = 0 // no permitir negativos
            cantidadPorCategoria[categoria] = suma
            totalArticulos += suma
        }

        if (totalArticulos == 0f) return

        var anguloInicio = 0f

        for ((categoria, cantidad) in cantidadPorCategoria) {
            if (cantidad <= 0) continue
            val porcentaje = cantidad / totalArticulos
            val anguloBarrido = porcentaje * 360f

            val seccion = Paint().apply {
                style = Paint.Style.STROKE
                isAntiAlias = true
                strokeWidth = grosorMetrica.toFloat()
                strokeCap = Paint.Cap.BUTT
                color = Color.parseColor(categoria.color)
            }

            canvas.drawArc(coordenadas!!, anguloInicio, anguloBarrido, false, seccion)

            anguloInicio += anguloBarrido
        }
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int = PixelFormat.OPAQUE
}
