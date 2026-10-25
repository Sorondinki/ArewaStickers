const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read sticker packs", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("sticker_packs").get());
});

test("Authenticated user: cannot read another user's sticker pack", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("sticker_packs").doc("pack_1").set({
      userId: BOB_UID,
      title: "Bob Pack",
      author: "Bob",
      category: "Barkwanci",
      trayEmoji: "😂",
      accentColorHex: "#075E54",
      isFavorite: false,
      stickersCount: 3,
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("sticker_packs").doc("pack_1").get());
});

test("Authenticated user: can create and read their own valid sticker pack", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const packRef = aliceDb.collection("users").doc(ALICE_UID).collection("sticker_packs").doc("pack_valid");

  await assertSucceeds(
    packRef.set({
      userId: ALICE_UID,
      title: "Sannu da Zuwa",
      author: "Arewa Creator",
      category: "Gaisuwa",
      trayEmoji: "🤝",
      accentColorHex: "#128C7E",
      isFavorite: true,
      stickersCount: 5,
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );

  await assertSucceeds(packRef.get());
});

test("Authenticated user: shadow update with ghost field is rejected", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("sticker_packs").doc("pack_1").set({
      userId: ALICE_UID,
      title: "Alice Pack",
      author: "Alice",
      category: "Biki",
      trayEmoji: "🎉",
      accentColorHex: "#075E54",
      isFavorite: false,
      stickersCount: 4,
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("sticker_packs").doc("pack_1").update({
      isFavorite: true,
      isVerifiedGhostField: true,
      updatedAt: new Date(),
    })
  );
});

test("Authenticated user: can create and list their own custom stickers", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const stickerRef = aliceDb.collection("users").doc(ALICE_UID).collection("custom_stickers").doc("sticker_1");

  await assertSucceeds(
    stickerRef.set({
      userId: ALICE_UID,
      packId: "pack_1",
      topText: "Madalla!",
      bottomText: "Mun gode sosai",
      centerEmoji: "🌟",
      shapeStyle: "STARBURST",
      bgColorHex: "#075E54",
      borderColorHex: "#FFD54F",
      isFavorite: true,
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("custom_stickers").where("userId", "==", ALICE_UID).get()
  );
});
