package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

fun parseComposeColor(hex: String, fallback: Color = Color(0xFF075E54)): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        fallback
    }
}

/**
 * Custom Compose Canvas that renders an authentic WhatsApp Sticker Badge with a white die-cut
 * border, geometric Arewa shape (Starburst, Shield, Hexagon, Circle, Banner), central emoji,
 * and strict single-line top and bottom captions.
 */
@Composable
fun StickerBadgeCanvas(
    topText: String,
    bottomText: String,
    centerEmoji: String,
    shapeStyle: String,
    bgColorHex: String,
    borderColorHex: String,
    modifier: Modifier = Modifier,
    emojiFontSizeSp: Float = 36f,
    showCaptions: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val bgColor = parseComposeColor(bgColorHex, Color(0xFF075E54))
    val borderColor = parseComposeColor(borderColorHex, Color(0xFFFFD54F))

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val r = min(w, h) * 0.43f

            val path = Path().apply {
                when (shapeStyle) {
                    "STARBURST" -> {
                        val points = 16
                        val innerR = r * 0.85f
                        for (i in 0 until points * 2) {
                            val rad = if (i % 2 == 0) r else innerR
                            val angle = Math.toRadians((i * 180.0 / points) - 90.0)
                            val x = cx + (rad * cos(angle)).toFloat()
                            val y = cy + (rad * sin(angle)).toFloat()
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                        close()
                    }
                    "HEXAGON" -> {
                        for (i in 0 until 6) {
                            val angle = Math.toRadians((60.0 * i) - 30.0)
                            val x = cx + (r * cos(angle)).toFloat()
                            val y = cy + (r * sin(angle)).toFloat()
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                        close()
                    }
                    "SHIELD" -> {
                        moveTo(cx, cy - r)
                        lineTo(cx + r * 0.9f, cy - r * 0.65f)
                        lineTo(cx + r * 0.8f, cy + r * 0.25f)
                        quadraticTo(cx + r * 0.4f, cy + r * 0.85f, cx, cy + r)
                        quadraticTo(cx - r * 0.4f, cy + r * 0.85f, cx - r * 0.8f, cy + r * 0.25f)
                        lineTo(cx - r * 0.9f, cy - r * 0.65f)
                        close()
                    }
                    "BANNER" -> {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                left = cx - r * 0.98f,
                                top = cy - r * 0.80f,
                                right = cx + r * 0.98f,
                                bottom = cy + r * 0.80f,
                                cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
                            )
                        )
                    }
                    else -> {
                        addOval(
                            androidx.compose.ui.geometry.Rect(
                                left = cx - r,
                                top = cy - r,
                                right = cx + r,
                                bottom = cy + r
                            )
                        )
                    }
                }
            }

            // White die-cut outer sticker border
            drawPath(
                path = path,
                color = Color.White,
                style = Stroke(
                    width = 7.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Main rich gradient fill
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(bgColor, bgColor.copy(alpha = 0.86f)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            )

            // Inner accent border
            drawPath(
                path = path,
                color = borderColor,
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showCaptions && topText.isNotBlank()) {
                Surface(
                    color = Color(0xCC0B141A),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Text(
                        text = topText.uppercase(),
                        color = borderColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }

            Text(
                text = centerEmoji,
                fontSize = emojiFontSizeSp.sp,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )

            if (showCaptions && bottomText.isNotBlank()) {
                Surface(
                    color = borderColor,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = bottomText,
                        color = Color(0xFF0B141A),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

/**
 * Custom decorative Arewa Hero Banner with Northern Nigerian geometric knot artwork and lively sticker previews.
 */
@Composable
fun ArewaHeroBanner(
    onExploreClick: () -> Unit,
    onCreateStudioClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_banner_card"),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 6.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF075E54),
                            Color(0xFF128C7E),
                            Color(0xFF0A3D36)
                        )
                    )
                )
        ) {
            // Subtle decorative geometric pattern in background
            Canvas(modifier = Modifier.matchParentSize()) {
                val step = 64.dp.toPx()
                val strokeColor = Color(0x18FFD54F)
                var x = 0f
                while (x < size.width + step) {
                    drawCircle(
                        color = strokeColor,
                        radius = 28.dp.toPx(),
                        center = Offset(x, size.height * 0.25f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = strokeColor,
                        radius = 20.dp.toPx(),
                        center = Offset(x + step / 2f, size.height * 0.8f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    x += step
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = Color(0x33FFD54F),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "✨ SABON SALO • HAUSA & AREWA",
                            color = Color(0xFFFFD54F),
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 11.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Lambobin WhatsApp Na Musamman",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Zabi fakitoci masu ban sha'awa ko ka ƙirƙiri naka cikin sakan 5!",
                        color = Color(0xFFE0F2F1),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFFFD54F),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .clickable { onCreateStudioClick() }
                                .testTag("hero_create_sticker_button")
                        ) {
                            Text(
                                text = "🎨 Ƙirƙiri Lamba",
                                color = Color(0xFF00221C),
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }

                        Surface(
                            color = Color(0x26FFFFFF),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(50))
                                .clickable { onExploreClick() }
                                .testTag("hero_whatsapp_guide_button")
                        ) {
                            Text(
                                text = "💬 Yadda Ake Sakawa",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    StickerBadgeCanvas(
                        topText = "SANNU!",
                        bottomText = "Arewa",
                        centerEmoji = "👑",
                        shapeStyle = "STARBURST",
                        bgColorHex = "#128C7E",
                        borderColorHex = "#FFD54F",
                        emojiFontSizeSp = 28f,
                        showCaptions = true
                    )
                }
            }
        }
    }
}
