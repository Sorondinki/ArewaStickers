package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class StickerRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createAndObservePack_authenticatedOwner_succeeds() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = StickerRepository(firestore)

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createStickerPack(
                title = "Gaisuwar Safe",
                author = "Amina",
                category = "Gaisuwa",
                trayEmoji = "☀️",
                accentColorHex = "#075E54"
            )
        }
        assertTrue(createResult.isSuccess)
        val packId = createResult.getOrThrow()
        assertTrue(packId.isNotEmpty())

        val emittedPacks = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserPacks(aliceUid).first { list -> list.any { it.id == packId } }
        }
        assertTrue(emittedPacks.any { it.id == packId && it.title == "Gaisuwar Safe" })
    }

    @Test
    fun createAndObserveCustomSticker_authenticatedOwner_succeeds() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = StickerRepository(firestore)

        val stickerResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createCustomSticker(
                packId = "default_pack",
                topText = "SANNU DA ZUWA",
                bottomText = "Maraba",
                centerEmoji = "🤝",
                shapeStyle = "STARBURST",
                bgColorHex = "#075E54",
                borderColorHex = "#FFD54F"
            )
        }
        assertTrue(stickerResult.isSuccess)
        val stickerId = stickerResult.getOrThrow()

        val emittedStickers = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeCustomStickers(aliceUid).first { list -> list.any { it.id == stickerId } }
        }
        assertTrue(emittedStickers.any { it.id == stickerId && it.topText == "SANNU DA ZUWA" })
    }

    @Test
    fun getPackById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = StickerRepository(firestore)
        val packId = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.createStickerPack(
                title = "Sirrin Alice",
                author = "Alice",
                category = "Na Musamman",
                trayEmoji = "🔒",
                accentColorHex = "#128C7E"
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = StickerRepository(firestore)
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                bobRepo.getPackById(aliceUid, packId)
            }
            fail("Expected FirebaseFirestoreException PERMISSION_DENIED")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }

    @Test
    fun observeUserPacks_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = StickerRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeUserPacks(aliceUid).first()
            }
            fail("Expected exception when unauthenticated")
        } catch (e: Exception) {
            val firestoreException = e.unwrapFirestoreException()
            assertNotNull("Expected nested FirebaseFirestoreException, got $e", firestoreException)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice@arewastickers.test"
        const val BOB_EMAIL = "bob@arewastickers.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
