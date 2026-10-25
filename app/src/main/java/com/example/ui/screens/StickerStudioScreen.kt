package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CuratedStickerCatalog
import com.example.ui.components.StickerBadgeCanvas
import com.example.ui.components.parseComposeColor
import com.example.util.StickerExportHelper
import kotlin.random.Random

@Composable
fun StickerStudioScreen(
    topText: String,
    bottomText: String,
    centerEmoji: String,
    shapeStyle: String,
    bgColorHex: String,
    borderColorHex: String,
    isExpandedScreen: Boolean,
    onTopTextChange: (String) -> Unit,
    onBottomTextChange: (String) -> Unit,
    onEmojiChange: (String) -> Unit,
    onShapeChange: (String) -> Unit,
    onColorChange: (String, String) -> Unit,
    onSaveToCloud: () -> Unit
) {
    val context = LocalContext.current

    val randomPresets = listOf(
        Triple("MADALLA!", "Aiki yayi kyau", "🌟"),
        Triple("SANNU FA!", "Ina kwana?", "☀️"),
        Triple("BARKA DA BIKI", "Muna taya murna", "🎉"),
        Triple("ALHAMDULILLAH", "Mun gode Allah", "🤲"),
        Triple("BA MATSALA", "Komai normal", "😎"),
        Triple("KASUWA TA BUDE", "Ku zo a saya", "💰")
    )

    val triggerRandomize = {
        val pick = randomPresets[Random.nextInt(randomPresets.size)]
        val palette = CuratedStickerCatalog.colorPalettes[Random.nextInt(CuratedStickerCatalog.colorPalettes.size)]
        val shape = CuratedStickerCatalog.shapeStyles[Random.nextInt(CuratedStickerCatalog.shapeStyles.size)].first
        onTopTextChange(pick.first)
        onBottomTextChange(pick.second)
        onEmojiChange(pick.third)
        onShapeChange(shape)
        onColorChange(palette.first, palette.second)
    }

    if (isExpandedScreen) {
        // Side-by-Side Live Preview + Controls on Tablets/Foldables
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                StudioPreviewCard(
                    topText = topText,
                    bottomText = bottomText,
                    centerEmoji = centerEmoji,
                    shapeStyle = shapeStyle,
                    bgColorHex = bgColorHex,
                    borderColorHex = borderColorHex,
                    onRandomize = triggerRandomize,
                    onSaveToCloud = onSaveToCloud,
                    onShareWhatsApp = {
                        StickerExportHelper.shareStickerToWhatsApp(
                            context = context,
                            topText = topText,
                            bottomText = bottomText,
                            centerEmoji = centerEmoji,
                            shapeStyle = shapeStyle,
                            bgColorHex = bgColorHex,
                            borderColorHex = borderColorHex
                        )
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                StudioControlsPanel(
                    topText = topText,
                    bottomText = bottomText,
                    centerEmoji = centerEmoji,
                    shapeStyle = shapeStyle,
                    bgColorHex = bgColorHex,
                    borderColorHex = borderColorHex,
                    onTopTextChange = onTopTextChange,
                    onBottomTextChange = onBottomTextChange,
                    onEmojiChange = onEmojiChange,
                    onShapeChange = onShapeChange,
                    onColorChange = onColorChange
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StudioPreviewCard(
                topText = topText,
                bottomText = bottomText,
                centerEmoji = centerEmoji,
                shapeStyle = shapeStyle,
                bgColorHex = bgColorHex,
                borderColorHex = borderColorHex,
                onRandomize = triggerRandomize,
                onSaveToCloud = onSaveToCloud,
                onShareWhatsApp = {
                    StickerExportHelper.shareStickerToWhatsApp(
                        context = context,
                        topText = topText,
                        bottomText = bottomText,
                        centerEmoji = centerEmoji,
                        shapeStyle = shapeStyle,
                        bgColorHex = bgColorHex,
                        borderColorHex = borderColorHex
                    )
                }
            )

            StudioControlsPanel(
                topText = topText,
                bottomText = bottomText,
                centerEmoji = centerEmoji,
                shapeStyle = shapeStyle,
                bgColorHex = bgColorHex,
                borderColorHex = borderColorHex,
                onTopTextChange = onTopTextChange,
                onBottomTextChange = onBottomTextChange,
                onEmojiChange = onEmojiChange,
                onShapeChange = onShapeChange,
                onColorChange = onColorChange
            )
        }
    }
}

@Composable
private fun StudioPreviewCard(
    topText: String,
    bottomText: String,
    centerEmoji: String,
    shapeStyle: String,
    bgColorHex: String,
    borderColorHex: String,
    onRandomize: () -> Unit,
    onSaveToCloud: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("studio_preview_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "🎨 512x512 WHATSAPP STICKER CANVAS",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                OutlinedButton(
                    onClick = onRandomize,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("studio_randomize_button")
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = "Sauya Salo", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sauya Salo",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Checkerboard-style subtle studio stage
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                StickerBadgeCanvas(
                    topText = topText,
                    bottomText = bottomText,
                    centerEmoji = centerEmoji,
                    shapeStyle = shapeStyle,
                    bgColorHex = bgColorHex,
                    borderColorHex = borderColorHex,
                    emojiFontSizeSp = 60f,
                    modifier = Modifier.size(190.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSaveToCloud,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("studio_save_cloud_button")
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Adana a Cloud", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Adana a Cloud",
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onShareWhatsApp,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF128C7E),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("studio_share_whatsapp_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Aika WhatsApp", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aika WhatsApp",
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun StudioControlsPanel(
    topText: String,
    bottomText: String,
    centerEmoji: String,
    shapeStyle: String,
    bgColorHex: String,
    borderColorHex: String,
    onTopTextChange: (String) -> Unit,
    onBottomTextChange: (String) -> Unit,
    onEmojiChange: (String) -> Unit,
    onShapeChange: (String) -> Unit,
    onColorChange: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "✍️ Rubutun Lamba (Sticker Caption)",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            OutlinedTextField(
                value = topText,
                onValueChange = onTopTextChange,
                label = {
                    Text(
                        text = "Rubutun Sama (Misali: SANNU DA ZUWA)",
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("studio_top_text_input")
            )

            OutlinedTextField(
                value = bottomText,
                onValueChange = onBottomTextChange,
                label = {
                    Text(
                        text = "Rubutun Kasa (Misali: Arewa Na Musamman)",
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("studio_bottom_text_input")
            )

            // Quick Hausa Phrase Chips
            Text(
                text = "⚡ Zantukan Hausa Na Gaggawa:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            val quickPhrases = listOf(
                "INA KWANA" to "Lafiya kalau?",
                "MADALLA!" to "Mun gode sosai",
                "BARKA DA SALLAH" to "Allah ya maimaita",
                "BA MATSALA" to "Komai dai-dai",
                "ALLAH SANYA ALHERI" to "Muna taya murna"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickPhrases) { pair ->
                    FilterChip(
                        selected = topText == pair.first,
                        onClick = {
                            onTopTextChange(pair.first)
                            onBottomTextChange(pair.second)
                        },
                        label = {
                            Text(
                                text = pair.first,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        shape = RoundedCornerShape(50)
                    )
                }
            }

            // Emoji Selector
            Text(
                text = "😊 Zabi Alama (Emoji):",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(CuratedStickerCatalog.featuredEmojis) { emoji ->
                    val isSelected = emoji == centerEmoji
                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .then(
                                if (isSelected) {
                                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { onEmojiChange(emoji) }
                            .testTag("studio_emoji_$emoji")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = emoji,
                                fontSize = 22.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // Shape Style Selector
            Text(
                text = "💠 Salon Taswirar Lamba (Badge Shape):",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CuratedStickerCatalog.shapeStyles) { (shapeCode, shapeLabel) ->
                    val isSelected = shapeCode == shapeStyle
                    FilterChip(
                        selected = isSelected,
                        onClick = { onShapeChange(shapeCode) },
                        label = {
                            Text(
                                text = shapeLabel,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("studio_shape_$shapeCode")
                    )
                }
            }

            // Color Palette Selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Launuka",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Launin Bango & Gefen Lamba:",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(CuratedStickerCatalog.colorPalettes) { (bgHex, borderHex) ->
                    val isSelected = bgHex.equals(bgColorHex, ignoreCase = true)
                    val bgCol = parseComposeColor(bgHex)
                    val borderCol = parseComposeColor(borderHex)

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(bgCol)
                            .border(
                                width = if (isSelected) 4.dp else 2.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else borderCol,
                                shape = CircleShape
                            )
                            .clickable { onColorChange(bgHex, borderHex) },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(borderCol)
                        )
                    }
                }
            }
        }
    }
}
