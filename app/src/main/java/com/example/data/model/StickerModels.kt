package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class CustomStickerPack(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val author: String = "",
    val category: String = "Na Musamman",
    val trayEmoji: String = "🌟",
    val accentColorHex: String = "#075E54",
    val isFavorite: Boolean = false,
    val stickersCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CustomStickerItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val packId: String = "default_pack",
    val topText: String = "",
    val bottomText: String = "",
    val centerEmoji: String = "😊",
    val shapeStyle: String = "CIRCLE",
    val bgColorHex: String = "#075E54",
    val borderColorHex: String = "#FFD54F",
    val isFavorite: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class CuratedStickerItem(
    val id: String,
    val packId: String,
    val topText: String,
    val bottomText: String,
    val centerEmoji: String,
    val shapeStyle: String,
    val bgColorHex: String,
    val borderColorHex: String
)

data class CuratedStickerPack(
    val id: String,
    val title: String,
    val subtitle: String,
    val author: String,
    val category: String,
    val trayEmoji: String,
    val accentColorHex: String,
    val downloadsLabel: String,
    val stickers: List<CuratedStickerItem>
)
