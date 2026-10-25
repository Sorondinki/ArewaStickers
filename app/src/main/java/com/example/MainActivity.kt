package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.repository.StickerRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOut
import com.example.ui.screens.ExplorePacksScreen
import com.example.ui.screens.SavedCollectionScreen
import com.example.ui.screens.StickerStudioScreen
import com.example.ui.theme.ArewaStickersTheme
import com.example.ui.viewmodel.StickerViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArewaStickersTheme {
                AppNavigation()
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(
            onAuthSuccess = { /* AuthStateListener automatically updates currentUser */ }
        )
    } else {
        ArewaStickersMainApp(
            currentUserId = user.uid,
            userDisplayName = user.displayName ?: user.email ?: "Arewa Creator"
        )
    }
}

enum class AppDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    EXPLORE("Fakitoci", Icons.Filled.Explore, Icons.Outlined.Explore, "nav_explore"),
    STUDIO("Studio", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_studio"),
    SAVED("Ajiya", Icons.Filled.CollectionsBookmark, Icons.Outlined.CollectionsBookmark, "nav_saved")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArewaStickersMainApp(
    currentUserId: String,
    userDisplayName: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val viewModel: StickerViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                StickerViewModel(StickerRepository(db), currentUserId)
            }
        }
    )

    var currentTab by rememberSaveable { mutableStateOf(AppDestination.EXPLORE) }
    var showWhatsAppGuideDialog by remember { mutableStateOf(false) }

    // Mandatory BackHandler for secondary tabs to return to EXPLORE home screen
    BackHandler(enabled = currentTab != AppDestination.EXPLORE) {
        currentTab = AppDestination.EXPLORE
    }

    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCuratedPack by viewModel.selectedCuratedPack.collectAsStateWithLifecycle()
    val statusBannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    val studioTopText by viewModel.studioTopText.collectAsStateWithLifecycle()
    val studioBottomText by viewModel.studioBottomText.collectAsStateWithLifecycle()
    val studioEmoji by viewModel.studioEmoji.collectAsStateWithLifecycle()
    val studioShape by viewModel.studioShape.collectAsStateWithLifecycle()
    val studioBgHex by viewModel.studioBgHex.collectAsStateWithLifecycle()
    val studioBorderHex by viewModel.studioBorderHex.collectAsStateWithLifecycle()

    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()
    val imageScale by viewModel.imageScale.collectAsStateWithLifecycle()
    val imageRotation by viewModel.imageRotation.collectAsStateWithLifecycle()
    val borderThickness by viewModel.borderThickness.collectAsStateWithLifecycle()
    val colorFilterMode by viewModel.colorFilterMode.collectAsStateWithLifecycle()

    val userPacksState by viewModel.userPacksState.collectAsStateWithLifecycle()
    val customStickersState by viewModel.customStickersState.collectAsStateWithLifecycle()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp
        val isExpandedScreen = maxWidth >= 840.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "👑",
                                        fontSize = 18.sp,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "ArewaStickers",
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Sannu, $userDisplayName",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showWhatsAppGuideDialog = true },
                            modifier = Modifier.testTag("top_bar_guide_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = "Yadda ake sakawa a WhatsApp"
                            )
                        }

                        IconButton(
                            onClick = {
                                signOut(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onSignOutComplete = {},
                                    scope = coroutineScope
                                )
                            },
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Fita Daga Asusu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar {
                        AppDestination.entries.forEach { dest ->
                            val selected = currentTab == dest
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTab = dest },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = dest.title,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag(dest.tag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        modifier = Modifier.fillMaxHeight(),
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AppDestination.entries.forEach { dest ->
                            val selected = currentTab == dest
                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentTab = dest },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = dest.title,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier
                                    .padding(vertical = 6.dp)
                                    .testTag(dest.tag)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    // Dismissible Status Toast Banner
                    AnimatedVisibility(visible = statusBannerMessage != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = statusBannerMessage ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearStatusBanner() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Rufe",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        when (currentTab) {
                            AppDestination.EXPLORE -> {
                                ExplorePacksScreen(
                                    packs = viewModel.curatedPacks,
                                    categories = viewModel.categories,
                                    selectedCategory = selectedCategory,
                                    searchQuery = searchQuery,
                                    selectedPack = selectedCuratedPack,
                                    isExpandedScreen = isExpandedScreen,
                                    onSelectCategory = viewModel::selectCategory,
                                    onSearchQueryChange = viewModel::updateSearchQuery,
                                    onSelectPack = viewModel::selectCuratedPack,
                                    onBookmarkPack = viewModel::bookmarkCuratedPackToCloud,
                                    onCustomizeStickerInStudio = { sticker ->
                                        viewModel.loadTemplateIntoStudio(
                                            topText = sticker.topText,
                                            bottomText = sticker.bottomText,
                                            emoji = sticker.centerEmoji,
                                            shape = sticker.shapeStyle,
                                            bgHex = sticker.bgColorHex,
                                            borderHex = sticker.borderColorHex
                                        )
                                        currentTab = AppDestination.STUDIO
                                    },
                                    onOpenStudioTab = { currentTab = AppDestination.STUDIO },
                                    onShowWhatsAppGuide = { showWhatsAppGuideDialog = true }
                                )
                            }
                            AppDestination.STUDIO -> {
                                StickerStudioScreen(
                                    topText = studioTopText,
                                    bottomText = studioBottomText,
                                    centerEmoji = studioEmoji,
                                    shapeStyle = studioShape,
                                    bgColorHex = studioBgHex,
                                    borderColorHex = studioBorderHex,
                                    selectedImageUri = selectedImageUri,
                                    imageScale = imageScale,
                                    imageRotation = imageRotation,
                                    borderThickness = borderThickness,
                                    colorFilterMode = colorFilterMode,
                                    isExpandedScreen = isExpandedScreen,
                                    onTopTextChange = viewModel::updateStudioTopText,
                                    onBottomTextChange = viewModel::updateStudioBottomText,
                                    onEmojiChange = viewModel::updateStudioEmoji,
                                    onShapeChange = viewModel::updateStudioShape,
                                    onColorChange = viewModel::updateStudioColors,
                                    onImageSelected = viewModel::updateSelectedImageUri,
                                    onImageScaleChange = viewModel::updateImageScale,
                                    onImageRotationChange = viewModel::updateImageRotation,
                                    onBorderThicknessChange = viewModel::updateBorderThickness,
                                    onColorFilterModeChange = viewModel::updateColorFilterMode,
                                    onResetEditorTransforms = viewModel::resetStickerEditorTransform,
                                    onSaveToCloud = viewModel::saveStudioStickerToCloud
                                )
                            }
                            AppDestination.SAVED -> {
                                SavedCollectionScreen(
                                    userPacksState = userPacksState,
                                    customStickersState = customStickersState,
                                    onCreatePack = viewModel::createNewCustomPack,
                                    onTogglePackFavorite = viewModel::togglePackFavorite,
                                    onDeletePack = viewModel::deletePack,
                                    onToggleStickerFavorite = viewModel::toggleCustomStickerFavorite,
                                    onDeleteSticker = viewModel::deleteCustomSticker,
                                    onOpenStudio = { currentTab = AppDestination.STUDIO }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showWhatsAppGuideDialog) {
        AlertDialog(
            onDismissRequest = { showWhatsAppGuideDialog = false },
            title = {
                Text(
                    text = "💬 Yadda Ake Saka Lambobi A WhatsApp",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "1. Zabi kowane fakiti a shafin 'Fakitoci' sannan ka danna 'Saka a WhatsApp'.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "2. Ko ka danna kowace lamba guda daya don aikawa kai tsaye cikin hirarka ta WhatsApp.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "3. A shafin 'Studio', zaka iya rubuta naka kalaman Hausa, ka zabi launi da alama, sannan ka adana a Cloud ko ka tura WhatsApp nan take!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showWhatsAppGuideDialog = false }) {
                    Text(text = "Na Fahimta, Madalla!", maxLines = 1, softWrap = false)
                }
            }
        )
    }
}
