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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CuratedStickerItem
import com.example.data.model.CuratedStickerPack
import com.example.ui.components.ArewaHeroBanner
import com.example.ui.components.StickerBadgeCanvas
import com.example.ui.components.parseComposeColor
import com.example.util.StickerExportHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorePacksScreen(
    packs: List<CuratedStickerPack>,
    categories: List<String>,
    selectedCategory: String,
    searchQuery: String,
    selectedPack: CuratedStickerPack?,
    isExpandedScreen: Boolean,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectPack: (CuratedStickerPack) -> Unit,
    onBookmarkPack: (CuratedStickerPack) -> Unit,
    onCustomizeStickerInStudio: (CuratedStickerItem) -> Unit,
    onOpenStudioTab: () -> Unit,
    onShowWhatsAppGuide: () -> Unit
) {
    val context = LocalContext.current
    var showMobileDetailSheet by remember { mutableStateOf(false) }
    var activeInspectedSticker by remember { mutableStateOf<CuratedStickerItem?>(null) }

    val filteredPacks = remember(packs, selectedCategory, searchQuery) {
        packs.filter { pack ->
            val matchesCategory = selectedCategory == "Duka" || pack.category.equals(selectedCategory, ignoreCase = true)
            val q = searchQuery.trim()
            val matchesSearch = q.isEmpty() ||
                pack.title.contains(q, ignoreCase = true) ||
                pack.subtitle.contains(q, ignoreCase = true) ||
                pack.stickers.any { it.topText.contains(q, ignoreCase = true) || it.bottomText.contains(q, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
    }

    if (isExpandedScreen) {
        // Canonical List-Detail Split Pane for Tablet / Expanded Foldables
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxHeight()
                    .testTag("explore_packs_list"),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    ArewaHeroBanner(
                        onExploreClick = onShowWhatsAppGuide,
                        onCreateStudioClick = onOpenStudioTab
                    )
                }

                item {
                    SearchAndCategoryFilterBar(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        onSelectCategory = onSelectCategory,
                        onSearchQueryChange = onSearchQueryChange
                    )
                }

                items(filteredPacks, key = { it.id }) { pack ->
                    CuratedPackCard(
                        pack = pack,
                        isSelected = selectedPack?.id == pack.id,
                        onPackClick = { onSelectPack(pack) },
                        onAddToWhatsApp = {
                            val first = pack.stickers.first()
                            StickerExportHelper.addPackToWhatsApp(
                                context = context,
                                packId = pack.id,
                                packTitle = pack.title,
                                fallbackTopText = first.topText,
                                fallbackBottomText = first.bottomText,
                                fallbackEmoji = first.centerEmoji,
                                fallbackShape = first.shapeStyle,
                                fallbackBgHex = first.bgColorHex,
                                fallbackBorderHex = first.borderColorHex
                            )
                        },
                        onBookmark = { onBookmarkPack(pack) },
                        onStickerTap = { sticker ->
                            onSelectPack(pack)
                            activeInspectedSticker = sticker
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right Detail Pane
            Surface(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                val targetPack = selectedPack ?: filteredPacks.firstOrNull()
                if (targetPack != null) {
                    PackDetailContent(
                        pack = targetPack,
                        onBookmarkPack = { onBookmarkPack(targetPack) },
                        onShareSticker = { sticker ->
                            StickerExportHelper.shareStickerToWhatsApp(
                                context = context,
                                topText = sticker.topText,
                                bottomText = sticker.bottomText,
                                centerEmoji = sticker.centerEmoji,
                                shapeStyle = sticker.shapeStyle,
                                bgColorHex = sticker.bgColorHex,
                                borderColorHex = sticker.borderColorHex
                            )
                        },
                        onCustomizeSticker = onCustomizeStickerInStudio
                    )
                }
            }
        }
    } else {
        // Compact / Medium Responsive Single Column with Modal Detail Sheet
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("explore_packs_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ArewaHeroBanner(
                    onExploreClick = onShowWhatsAppGuide,
                    onCreateStudioClick = onOpenStudioTab
                )
            }

            item {
                SearchAndCategoryFilterBar(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    searchQuery = searchQuery,
                    onSelectCategory = onSelectCategory,
                    onSearchQueryChange = onSearchQueryChange
                )
            }

            if (filteredPacks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔍 Babu fakitin da ya dace da nemanka",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Gwada neman 'Sannu', 'Biki' ko ka danna Ƙirƙiri Lamba!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            } else {
                items(filteredPacks, key = { it.id }) { pack ->
                    CuratedPackCard(
                        pack = pack,
                        isSelected = false,
                        onPackClick = {
                            onSelectPack(pack)
                            showMobileDetailSheet = true
                        },
                        onAddToWhatsApp = {
                            val first = pack.stickers.first()
                            StickerExportHelper.addPackToWhatsApp(
                                context = context,
                                packId = pack.id,
                                packTitle = pack.title,
                                fallbackTopText = first.topText,
                                fallbackBottomText = first.bottomText,
                                fallbackEmoji = first.centerEmoji,
                                fallbackShape = first.shapeStyle,
                                fallbackBgHex = first.bgColorHex,
                                fallbackBorderHex = first.borderColorHex
                            )
                        },
                        onBookmark = { onBookmarkPack(pack) },
                        onStickerTap = { sticker ->
                            activeInspectedSticker = sticker
                        }
                    )
                }
            }
        }

        if (showMobileDetailSheet && selectedPack != null) {
            ModalBottomSheet(
                onDismissRequest = { showMobileDetailSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(540.dp)) {
                    PackDetailContent(
                        pack = selectedPack,
                        onBookmarkPack = { onBookmarkPack(selectedPack) },
                        onShareSticker = { sticker ->
                            StickerExportHelper.shareStickerToWhatsApp(
                                context = context,
                                topText = sticker.topText,
                                bottomText = sticker.bottomText,
                                centerEmoji = sticker.centerEmoji,
                                shapeStyle = sticker.shapeStyle,
                                bgColorHex = sticker.bgColorHex,
                                borderColorHex = sticker.borderColorHex
                            )
                        },
                        onCustomizeSticker = { sticker ->
                            showMobileDetailSheet = false
                            onCustomizeStickerInStudio(sticker)
                        }
                    )
                }
            }
        }
    }

    // Quick Single-Sticker Action Sheet when any individual sticker is tapped
    if (activeInspectedSticker != null) {
        val sticker = activeInspectedSticker!!
        ModalBottomSheet(
            onDismissRequest = { activeInspectedSticker = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(168.dp)) {
                    StickerBadgeCanvas(
                        topText = sticker.topText,
                        bottomText = sticker.bottomText,
                        centerEmoji = sticker.centerEmoji,
                        shapeStyle = sticker.shapeStyle,
                        bgColorHex = sticker.bgColorHex,
                        borderColorHex = sticker.borderColorHex,
                        emojiFontSizeSp = 54f
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = sticker.topText,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = sticker.bottomText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val target = sticker
                            activeInspectedSticker = null
                            onCustomizeStickerInStudio(target)
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("modal_edit_in_studio_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Gyara a Studio", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Gyara a Studio",
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = {
                            StickerExportHelper.shareStickerToWhatsApp(
                                context = context,
                                topText = sticker.topText,
                                bottomText = sticker.bottomText,
                                centerEmoji = sticker.centerEmoji,
                                shapeStyle = sticker.shapeStyle,
                                bgColorHex = sticker.bgColorHex,
                                borderColorHex = sticker.borderColorHex
                            )
                            activeInspectedSticker = null
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("modal_share_whatsapp_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Aika WhatsApp", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Aika WhatsApp",
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SearchAndCategoryFilterBar(
    categories: List<String>,
    selectedCategory: String,
    searchQuery: String,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Nemi lamba ko fakiti (Sannu, Biki, Kasuwa)...",
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Nema")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Goge")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_stickers_input")
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCategory(category) },
                    label = {
                        Text(
                            text = category,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }
    }
}

@Composable
private fun CuratedPackCard(
    pack: CuratedStickerPack,
    isSelected: Boolean,
    onPackClick: () -> Unit,
    onAddToWhatsApp: () -> Unit,
    onBookmark: () -> Unit,
    onStickerTap: (CuratedStickerItem) -> Unit
) {
    val accentColor = parseComposeColor(pack.accentColorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                } else {
                    Modifier
                }
            )
            .clickable { onPackClick() }
            .testTag("curated_pack_card_${pack.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = pack.trayEmoji,
                                fontSize = 24.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = pack.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "${pack.stickers.size} Lambobi",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "${pack.author} • ${pack.downloadsLabel} saukewa",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onBookmark,
                    modifier = Modifier.testTag("bookmark_pack_${pack.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "Adana Fakiti a Cloud",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Preview Strip of Stickers
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(pack.stickers, key = { it.id }) { sticker ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.size(96.dp)
                    ) {
                        StickerBadgeCanvas(
                            topText = sticker.topText,
                            bottomText = sticker.bottomText,
                            centerEmoji = sticker.centerEmoji,
                            shapeStyle = sticker.shapeStyle,
                            bgColorHex = sticker.bgColorHex,
                            borderColorHex = sticker.borderColorHex,
                            emojiFontSizeSp = 28f,
                            onClick = { onStickerTap(sticker) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pack.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                )

                Button(
                    onClick = onAddToWhatsApp,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF128C7E),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("add_whatsapp_btn_${pack.id}")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Saka a WhatsApp",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Saka a WhatsApp",
                        style = MaterialTheme.typography.labelLarge,
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
private fun PackDetailContent(
    pack: CuratedStickerPack,
    onBookmarkPack: () -> Unit,
    onShareSticker: (CuratedStickerItem) -> Unit,
    onCustomizeSticker: (CuratedStickerItem) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${pack.trayEmoji} ${pack.title}",
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${pack.author} • ${pack.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = onBookmarkPack,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.BookmarkAdd, contentDescription = "Adana", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Adana",
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Danna kowace lamba don aikawa kai tsaye ko gyarawa a Studio:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 115.dp),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pack.stickers, key = { it.id }) { sticker ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(96.dp)) {
                            StickerBadgeCanvas(
                                topText = sticker.topText,
                                bottomText = sticker.bottomText,
                                centerEmoji = sticker.centerEmoji,
                                shapeStyle = sticker.shapeStyle,
                                bgColorHex = sticker.bgColorHex,
                                borderColorHex = sticker.borderColorHex,
                                emojiFontSizeSp = 28f,
                                onClick = { onShareSticker(sticker) }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(
                                onClick = { onCustomizeSticker(sticker) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Gyara",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { onShareSticker(sticker) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Aika",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val first = pack.stickers.first()
                StickerExportHelper.addPackToWhatsApp(
                    context = context,
                    packId = pack.id,
                    packTitle = pack.title,
                    fallbackTopText = first.topText,
                    fallbackBottomText = first.bottomText,
                    fallbackEmoji = first.centerEmoji,
                    fallbackShape = first.shapeStyle,
                    fallbackBgHex = first.bgColorHex,
                    fallbackBorderHex = first.borderColorHex
                )
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF128C7E),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Saka Fakitin a WhatsApp")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Saka Fakitin '${pack.title}' a WhatsApp",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
