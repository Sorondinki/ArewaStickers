package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CuratedStickerCatalog
import com.example.data.model.CuratedStickerPack
import com.example.data.model.CustomStickerItem
import com.example.data.model.CustomStickerPack
import com.example.data.repository.StickerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class StickerViewModel(
    private val repository: StickerRepository,
    private val currentUserId: String
) : ViewModel() {

    val curatedPacks: List<CuratedStickerPack> = CuratedStickerCatalog.packs
    val categories: List<String> = CuratedStickerCatalog.categories

    private val _selectedCategory = MutableStateFlow("Duka")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCuratedPack = MutableStateFlow<CuratedStickerPack?>(CuratedStickerCatalog.packs.firstOrNull())
    val selectedCuratedPack: StateFlow<CuratedStickerPack?> = _selectedCuratedPack.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    // Studio Editor state
    private val _studioTopText = MutableStateFlow("BARKA DA ZUWA")
    val studioTopText: StateFlow<String> = _studioTopText.asStateFlow()

    private val _studioBottomText = MutableStateFlow("Arewa Na Musamman")
    val studioBottomText: StateFlow<String> = _studioBottomText.asStateFlow()

    private val _studioEmoji = MutableStateFlow("👑")
    val studioEmoji: StateFlow<String> = _studioEmoji.asStateFlow()

    private val _studioShape = MutableStateFlow("STARBURST")
    val studioShape: StateFlow<String> = _studioShape.asStateFlow()

    private val _studioBgHex = MutableStateFlow("#075E54")
    val studioBgHex: StateFlow<String> = _studioBgHex.asStateFlow()

    private val _studioBorderHex = MutableStateFlow("#FFD54F")
    val studioBorderHex: StateFlow<String> = _studioBorderHex.asStateFlow()

    // Photo & Sticker Editor State
    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()

    private val _imageScale = MutableStateFlow(1.0f)
    val imageScale: StateFlow<Float> = _imageScale.asStateFlow()

    private val _imageRotation = MutableStateFlow(0f)
    val imageRotation: StateFlow<Float> = _imageRotation.asStateFlow()

    private val _borderThickness = MutableStateFlow(7f)
    val borderThickness: StateFlow<Float> = _borderThickness.asStateFlow()

    private val _colorFilterMode = MutableStateFlow("NORMAL")
    val colorFilterMode: StateFlow<String> = _colorFilterMode.asStateFlow()

    // Cloud Firestore Streams with Two-Tier Error Handling
    val userPacksState: StateFlow<UiState<List<CustomStickerPack>>> = repository.observeUserPacks(currentUserId)
        .map<List<CustomStickerPack>, UiState<List<CustomStickerPack>>> { list ->
            val sorted = list.sortedByDescending { it.createdAt?.seconds ?: 0L }
            UiState.Success(sorted)
        }
        .catch { error ->
            Log.w(TAG, "Error observing user sticker packs", error)
            emit(UiState.Error(error.message ?: "An kasa loda fakitocin da aka adana"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val customStickersState: StateFlow<UiState<List<CustomStickerItem>>> = repository.observeCustomStickers(currentUserId)
        .map<List<CustomStickerItem>, UiState<List<CustomStickerItem>>> { list ->
            val sorted = list.sortedByDescending { it.createdAt?.seconds ?: 0L }
            UiState.Success(sorted)
        }
        .catch { error ->
            Log.w(TAG, "Error observing custom stickers", error)
            emit(UiState.Error(error.message ?: "An kasa loda lambobin da aka ƙirƙira"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCuratedPack(pack: CuratedStickerPack) {
        _selectedCuratedPack.value = pack
    }

    fun updateStudioTopText(value: String) {
        _studioTopText.value = value.take(32)
    }

    fun updateStudioBottomText(value: String) {
        _studioBottomText.value = value.take(32)
    }

    fun updateStudioEmoji(emoji: String) {
        _studioEmoji.value = emoji
    }

    fun updateStudioShape(shape: String) {
        _studioShape.value = shape
    }

    fun updateStudioColors(bgHex: String, borderHex: String) {
        _studioBgHex.value = bgHex
        _studioBorderHex.value = borderHex
    }

    fun updateSelectedImageUri(uri: String?) {
        _selectedImageUri.value = uri
        if (uri != null) {
            _statusBannerMessage.value = "📸 An dora hoto a Sticker Editor!"
        }
    }

    fun updateImageScale(scale: Float) {
        _imageScale.value = scale.coerceIn(0.5f, 2.5f)
    }

    fun updateImageRotation(rotation: Float) {
        _imageRotation.value = rotation.coerceIn(-180f, 180f)
    }

    fun updateBorderThickness(thickness: Float) {
        _borderThickness.value = thickness.coerceIn(2f, 16f)
    }

    fun updateColorFilterMode(mode: String) {
        _colorFilterMode.value = mode
    }

    fun resetStickerEditorTransform() {
        _imageScale.value = 1.0f
        _imageRotation.value = 0f
        _borderThickness.value = 7f
        _colorFilterMode.value = "NORMAL"
    }

    fun loadTemplateIntoStudio(
        topText: String,
        bottomText: String,
        emoji: String,
        shape: String,
        bgHex: String,
        borderHex: String
    ) {
        _studioTopText.value = topText
        _studioBottomText.value = bottomText
        _studioEmoji.value = emoji
        _studioShape.value = shape
        _studioBgHex.value = bgHex
        _studioBorderHex.value = borderHex
        _statusBannerMessage.value = "An loda lamba a Studio don gyarawa!"
    }

    fun clearStatusBanner() {
        _statusBannerMessage.value = null
    }

    fun saveStudioStickerToCloud() {
        viewModelScope.launch {
            val result = repository.createCustomSticker(
                packId = "studio_pack",
                topText = _studioTopText.value.ifBlank { "SANNU!" },
                bottomText = _studioBottomText.value,
                centerEmoji = _studioEmoji.value,
                shapeStyle = _studioShape.value,
                bgColorHex = _studioBgHex.value,
                borderColorHex = _studioBorderHex.value,
                isFavorite = true
            )
            if (result.isSuccess) {
                _statusBannerMessage.value = "✅ An adana lamba a Cloud cikin nasara!"
            } else {
                _statusBannerMessage.value = "⚠️ An kasa adana lamba: ${result.exceptionOrNull()?.localizedMessage ?: "Matsala"}"
            }
        }
    }

    fun bookmarkCuratedPackToCloud(pack: CuratedStickerPack) {
        viewModelScope.launch {
            val result = repository.createStickerPack(
                title = pack.title,
                author = pack.author,
                category = pack.category,
                trayEmoji = pack.trayEmoji,
                accentColorHex = pack.accentColorHex,
                isFavorite = true,
                stickersCount = pack.stickers.size
            )
            if (result.isSuccess) {
                _statusBannerMessage.value = "✅ An adana fakitin '${pack.title}' a Cloud!"
            } else {
                _statusBannerMessage.value = "⚠️ Kuskure wajen adana fakiti."
            }
        }
    }

    fun createNewCustomPack(title: String, author: String, category: String, emoji: String, colorHex: String) {
        viewModelScope.launch {
            val safeTitle = title.trim().ifEmpty { "Fakitina Na Musamman" }
            val safeAuthor = author.trim().ifEmpty { "Mai Ƙirƙira" }
            val result = repository.createStickerPack(
                title = safeTitle,
                author = safeAuthor,
                category = category,
                trayEmoji = emoji,
                accentColorHex = colorHex,
                isFavorite = true,
                stickersCount = 1
            )
            if (result.isSuccess) {
                _statusBannerMessage.value = "✅ An ƙirƙiri sabon fakiti '$safeTitle'!"
            } else {
                _statusBannerMessage.value = "⚠️ An kasa ƙirƙirar fakiti."
            }
        }
    }

    fun togglePackFavorite(pack: CustomStickerPack) {
        viewModelScope.launch {
            repository.togglePackFavorite(pack)
        }
    }

    fun deletePack(packId: String) {
        viewModelScope.launch {
            val res = repository.deleteStickerPack(packId)
            if (res.isSuccess) {
                _statusBannerMessage.value = "🗑️ An goge fakiti."
            }
        }
    }

    fun toggleCustomStickerFavorite(sticker: CustomStickerItem) {
        viewModelScope.launch {
            repository.toggleStickerFavorite(sticker)
        }
    }

    fun deleteCustomSticker(stickerId: String) {
        viewModelScope.launch {
            val res = repository.deleteCustomSticker(stickerId)
            if (res.isSuccess) {
                _statusBannerMessage.value = "🗑️ An goge lamba."
            }
        }
    }

    private companion object {
        const val TAG = "StickerViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
