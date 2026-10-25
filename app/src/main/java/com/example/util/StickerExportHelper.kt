package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object StickerExportHelper {

    fun parseColorSafe(hex: String, defaultColor: Int = Color.parseColor("#075E54")): Int {
        return try {
            Color.parseColor(hex)
        } catch (e: Exception) {
            defaultColor
        }
    }

    /**
     * Renders a crisp 512x512 WhatsApp-compliant sticker bitmap with transparent background,
     * custom badge geometry, white sticker die-cut outline, emoji centerpiece, and Hausa caption.
     */
    fun renderStickerBitmap(
        topText: String,
        bottomText: String,
        centerEmoji: String,
        shapeStyle: String,
        bgColorHex: String,
        borderColorHex: String
    ): Bitmap {
        val size = 512
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgColor = parseColorSafe(bgColorHex, Color.parseColor("#075E54"))
        val borderColor = parseColorSafe(borderColorHex, Color.parseColor("#FFD54F"))

        val cx = size / 2f
        val cy = size / 2f
        val radius = size * 0.40f

        val badgePath = createShapePath(shapeStyle, cx, cy, radius)

        // 1. White Sticker Die-Cut Outer Stroke (classic WhatsApp sticker look)
        val dieCutPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 28f
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(12f, 0f, 6f, Color.argb(90, 0, 0, 0))
        }
        canvas.drawPath(badgePath, dieCutPaint)

        // 2. Main Badge Fill
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
            style = Paint.Style.FILL
        }
        canvas.drawPath(badgePath, fillPaint)

        // 3. Inner Decorative Accent Border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = 10f
            strokeJoin = Paint.Join.ROUND
        }
        canvas.drawPath(badgePath, borderPaint)

        // 4. Center Emoji
        val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            textSize = 136f
        }
        canvas.drawText(centerEmoji, cx, cy + 36f, emojiPaint)

        // 5. Top Ribbon / Text with strong outline
        if (topText.isNotBlank()) {
            drawOutlinedStickerText(
                canvas = canvas,
                text = topText.uppercase(),
                x = cx,
                y = cy - radius * 0.56f,
                textSize = 44f,
                fillColor = borderColor,
                strokeColor = Color.parseColor("#0B141A")
            )
        }

        // 6. Bottom Pill / Subtitle Text
        if (bottomText.isNotBlank()) {
            val pillRect = RectF(cx - radius * 0.85f, cy + radius * 0.45f, cx + radius * 0.85f, cy + radius * 0.78f)
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#0B141A")
                style = Paint.Style.FILL
            }
            val pillStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = borderColor
                style = Paint.Style.STROKE
                strokeWidth = 5f
            }
            canvas.drawRoundRect(pillRect, 32f, 32f, pillPaint)
            canvas.drawRoundRect(pillRect, 32f, 32f, pillStrokePaint)

            drawOutlinedStickerText(
                canvas = canvas,
                text = bottomText,
                x = cx,
                y = pillRect.centerY() + 12f,
                textSize = 32f,
                fillColor = Color.WHITE,
                strokeColor = Color.TRANSPARENT
            )
        }

        return bitmap
    }

    private fun drawOutlinedStickerText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        textSize: Float,
        fillColor: Int,
        strokeColor: Int
    ) {
        val safeText = if (text.length > 22) text.take(20) + "…" else text
        if (strokeColor != Color.TRANSPARENT) {
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = strokeColor
                textAlign = Paint.Align.CENTER
                this.textSize = textSize
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                style = Paint.Style.STROKE
                strokeWidth = 12f
                strokeJoin = Paint.Join.ROUND
            }
            canvas.drawText(safeText, x, y, strokePaint)
        }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = fillColor
            textAlign = Paint.Align.CENTER
            this.textSize = textSize
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style = Paint.Style.FILL
        }
        canvas.drawText(safeText, x, y, textPaint)
    }

    private fun createShapePath(shapeStyle: String, cx: Float, cy: Float, radius: Float): Path {
        val path = Path()
        when (shapeStyle) {
            "STARBURST" -> {
                val points = 16
                val innerRadius = radius * 0.84f
                for (i in 0 until points * 2) {
                    val r = if (i % 2 == 0) radius else innerRadius
                    val angle = Math.toRadians((i * 180.0 / points) - 90.0)
                    val x = cx + (r * cos(angle)).toFloat()
                    val y = cy + (r * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
            }
            "HEXAGON" -> {
                for (i in 0 until 6) {
                    val angle = Math.toRadians((60.0 * i) - 30.0)
                    val x = cx + (radius * cos(angle)).toFloat()
                    val y = cy + (radius * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
            }
            "SHIELD" -> {
                path.moveTo(cx, cy - radius)
                path.lineTo(cx + radius * 0.9f, cy - radius * 0.65f)
                path.lineTo(cx + radius * 0.8f, cy + radius * 0.25f)
                path.quadTo(cx + radius * 0.4f, cy + radius * 0.85f, cx, cy + radius)
                path.quadTo(cx - radius * 0.4f, cy + radius * 0.85f, cx - radius * 0.8f, cy + radius * 0.25f)
                path.lineTo(cx - radius * 0.9f, cy - radius * 0.65f)
                path.close()
            }
            "BANNER" -> {
                val rect = RectF(cx - radius * 1.02f, cy - radius * 0.82f, cx + radius * 1.02f, cy + radius * 0.82f)
                path.addRoundRect(rect, 56f, 56f, Path.Direction.CW)
            }
            else -> {
                val r = min(cx, cy) * 0.80f
                path.addCircle(cx, cy, r, Path.Direction.CW)
            }
        }
        return path
    }

    /**
     * Shares a rendered 512x512 sticker image directly to WhatsApp (or system share sheet if WhatsApp isn't installed).
     */
    fun shareStickerToWhatsApp(
        context: Context,
        topText: String,
        bottomText: String,
        centerEmoji: String,
        shapeStyle: String,
        bgColorHex: String,
        borderColorHex: String
    ) {
        try {
            val bitmap = renderStickerBitmap(
                topText = topText,
                bottomText = bottomText,
                centerEmoji = centerEmoji,
                shapeStyle = shapeStyle,
                bgColorHex = bgColorHex,
                borderColorHex = borderColorHex
            )
            val cacheDir = File(context.cacheDir, "stickers").apply { mkdirs() }
            val file = File(cacheDir, "arewa_sticker_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "$topText - $bottomText (Daga ArewaStickers)")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Aika Lamba zuwa WhatsApp")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "An sami matsala wajen raba lamba: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Invokes the official WhatsApp Third-Party Sticker Pack intent (`com.whatsapp.intent.action.ENABLE_STICKER_PACK`),
     * with automatic fallback to sharing the pack's hero sticker if WhatsApp is not installed on the emulator.
     */
    fun addPackToWhatsApp(
        context: Context,
        packId: String,
        packTitle: String,
        fallbackTopText: String,
        fallbackBottomText: String,
        fallbackEmoji: String,
        fallbackShape: String,
        fallbackBgHex: String,
        fallbackBorderHex: String
    ) {
        val intent = Intent("com.whatsapp.intent.action.ENABLE_STICKER_PACK").apply {
            putExtra("sticker_pack_id", packId)
            putExtra("sticker_pack_authority", "${context.packageName}.fileprovider")
            putExtra("sticker_pack_name", packTitle)
        }

        val pm = context.packageManager
        if (intent.resolveActivity(pm) != null) {
            try {
                context.startActivity(intent)
                return
            } catch (ignored: Exception) {
                // Fall through to share sheet fallback
            }
        }

        Toast.makeText(
            context,
            "Ana bude hanyar raba fakitin '$packTitle' zuwa WhatsApp...",
            Toast.LENGTH_SHORT
        ).show()

        shareStickerToWhatsApp(
            context = context,
            topText = fallbackTopText,
            bottomText = fallbackBottomText,
            centerEmoji = fallbackEmoji,
            shapeStyle = fallbackShape,
            bgColorHex = fallbackBgHex,
            borderColorHex = fallbackBorderHex
        )
    }
}
