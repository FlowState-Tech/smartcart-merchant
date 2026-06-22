package com.smartcart_merchant.core.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

object QrCodeGenerator {

    fun generateBitmap(content: String, size: Int = 512, logoLabel: String = "SC"): Bitmap {
        val matrix: BitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return overlayLogo(bitmap, logoLabel)
    }

    private fun overlayLogo(qr: Bitmap, label: String): Bitmap {
        val canvas = Canvas(qr)
        val boxSize = qr.width / 5f
        val left = (qr.width - boxSize) / 2f
        val top = (qr.height - boxSize) / 2f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(left, top, left + boxSize, top + boxSize), 16f, 16f, paint)
        paint.color = Color.parseColor("#16A34A")
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = boxSize * 0.35f
        canvas.drawText(label, qr.width / 2f, qr.height / 2f + boxSize * 0.12f, paint)
        return qr
    }
}
