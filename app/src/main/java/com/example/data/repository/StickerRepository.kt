package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.CustomStickerItem
import com.example.data.model.CustomStickerPack
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StickerRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserPacks(userId: String = requireUserId()): Flow<List<CustomStickerPack>> = flow {
        val path = "users/$userId/sticker_packs"
        emitAll(
            db.collection("users").document(userId).collection("sticker_packs")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CustomStickerPack::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    fun observeCustomStickers(userId: String = requireUserId()): Flow<List<CustomStickerItem>> = flow {
        val path = "users/$userId/custom_stickers"
        emitAll(
            db.collection("users").document(userId).collection("custom_stickers")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CustomStickerItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createStickerPack(
        title: String,
        author: String,
        category: String,
        trayEmoji: String,
        accentColorHex: String,
        isFavorite: Boolean = false,
        stickersCount: Int = 1
    ): Result<String> {
        val uid = requireUserId()
        val packId = "pack_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val docRef = db.collection("users").document(uid).collection("sticker_packs").document(packId)
        val payload = mapOf(
            "userId" to uid,
            "title" to title.trim().take(60),
            "author" to author.trim().take(50),
            "category" to category.trim().take(40),
            "trayEmoji" to trayEmoji.take(16),
            "accentColorHex" to accentColorHex.take(12),
            "isFavorite" to isFavorite,
            "stickersCount" to stickersCount,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(packId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getPackById(targetUserId: String, packId: String): Result<CustomStickerPack?> {
        val docRef = db.collection("users").document(targetUserId).collection("sticker_packs").document(packId)
        return try {
            val snap = docRef.get().await()
            Result.success(snap.toObject(CustomStickerPack::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun togglePackFavorite(pack: CustomStickerPack): Result<Unit> {
        val uid = requireUserId()
        require(pack.id.isNotBlank()) { "Pack ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("sticker_packs").document(pack.id)
        return try {
            docRef.update(
                mapOf(
                    "isFavorite" to !pack.isFavorite,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteStickerPack(packId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("sticker_packs").document(packId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun createCustomSticker(
        packId: String,
        topText: String,
        bottomText: String,
        centerEmoji: String,
        shapeStyle: String,
        bgColorHex: String,
        borderColorHex: String,
        isFavorite: Boolean = true
    ): Result<String> {
        val uid = requireUserId()
        val stickerId = "stk_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val docRef = db.collection("users").document(uid).collection("custom_stickers").document(stickerId)
        val safeTop = topText.trim().ifEmpty { "SANNU!" }.take(60)
        val safeBottom = bottomText.trim().take(60)
        val safeShape = if (shapeStyle in listOf("CIRCLE", "STARBURST", "SHIELD", "HEXAGON", "BANNER")) shapeStyle else "CIRCLE"

        val payload = mapOf(
            "userId" to uid,
            "packId" to packId.take(128),
            "topText" to safeTop,
            "bottomText" to safeBottom,
            "centerEmoji" to centerEmoji.take(16),
            "shapeStyle" to safeShape,
            "bgColorHex" to bgColorHex.take(12),
            "borderColorHex" to borderColorHex.take(12),
            "isFavorite" to isFavorite,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(stickerId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun toggleStickerFavorite(sticker: CustomStickerItem): Result<Unit> {
        val uid = requireUserId()
        require(sticker.id.isNotBlank()) { "Sticker ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("custom_stickers").document(sticker.id)
        return try {
            docRef.update(
                mapOf(
                    "isFavorite" to !sticker.isFavorite,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteCustomSticker(stickerId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("custom_stickers").document(stickerId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }
}
