package com.example.data.model

object CuratedStickerCatalog {

    val categories = listOf(
        "Duka",
        "Gaisuwa",
        "Barkwanci",
        "Biki & Murna",
        "Addu'a & Fatan Alheri",
        "Kasuwanci",
        "Soyayya"
    )

    val shapeStyles = listOf(
        "CIRCLE" to "Da'ira (Badge)",
        "STARBURST" to "Tauraro (Arewa Star)",
        "SHIELD" to "Garkuwa (Shield)",
        "HEXAGON" to " Kusurwa Shida (Hex)",
        "BANNER" to "Tuta (Ribbon)"
    )

    val colorPalettes = listOf(
        Pair("#075E54", "#FFD54F"), // WhatsApp Emerald + Royal Gold
        Pair("#128C7E", "#FFFFFF"), // Teal Green + Crisp White
        Pair("#7B1FA2", "#FFD54F"), // Royal Purple + Gold
        Pair("#C62828", "#FFE082"), // Zaria Crimson + Warm Gold
        Pair("#1565C0", "#80D8FF"), // Kano Indigo + Sky Blue
        Pair("#2E7D32", "#A5D6A7"), // Forest Green + Mint
        Pair("#D84315", "#FFCC80"), // Terracotta + Sunset Amber
        Pair("#263238", "#25D366")  // Midnight + Neon WhatsApp Green
    )

    val featuredEmojis = listOf(
        "🤝", "😂", "🎉", "🤲", "👑", "🔥", "❤️", "🌟", "🕌", "☕", "🚀", "💰", "😎", "🙏", "🌺", "🥁"
    )

    val packs: List<CuratedStickerPack> = listOf(
        CuratedStickerPack(
            id = "curated_gaisuwa_arewa",
            title = "Gaisuwar Arewa",
            subtitle = "Gaisuwar safe, yamma da girmamawa ta yau da kullum",
            author = "ArewaStickers Studio",
            category = "Gaisuwa",
            trayEmoji = "🤝",
            accentColorHex = "#075E54",
            downloadsLabel = "18.4K",
            stickers = listOf(
                CuratedStickerItem("g1", "curated_gaisuwa_arewa", "INA KWANA!", "Lafiya kalau?", "☀️", "STARBURST", "#075E54", "#FFD54F"),
                CuratedStickerItem("g2", "curated_gaisuwa_arewa", "BARKA DA RANA", "Yaya aiki?", "🌤️", "CIRCLE", "#128C7E", "#FFFFFF"),
                CuratedStickerItem("g3", "curated_gaisuwa_arewa", "SANNU DA ZUWA", "Maraba da kai", "🤝", "SHIELD", "#2E7D32", "#FFD54F"),
                CuratedStickerItem("g4", "curated_gaisuwa_arewa", "SAI ANJIMA!", "A sauka lafiya", "👋", "HEXAGON", "#00695C", "#FFE082"),
                CuratedStickerItem("g5", "curated_gaisuwa_arewa", "MU KWANA LAFIYA", "Allah ya tashe mu", "🌙", "CIRCLE", "#1A237E", "#FFD54F"),
                CuratedStickerItem("g6", "curated_gaisuwa_arewa", "RANAR JUMU'A", "Barka da Jumu'a", "🕌", "STARBURST", "#075E54", "#A5D6A7")
            )
        ),
        CuratedStickerPack(
            id = "curated_barkwanci_nishadi",
            title = "Barkwanci & Nishadi",
            subtitle = "Lambobin dariya da zantukan hira masu ban sha'awa",
            author = "Kano Creative Hub",
            category = "Barkwanci",
            trayEmoji = "😂",
            accentColorHex = "#D84315",
            downloadsLabel = "24.9K",
            stickers = listOf(
                CuratedStickerItem("b1", "curated_barkwanci_nishadi", "KAI JAMA'A!", "Wannan zance fa?", "😂", "STARBURST", "#D84315", "#FFD54F"),
                CuratedStickerItem("b2", "curated_barkwanci_nishadi", "BA MATSALA!", "Komai dai-dai ne", "😎", "HEXAGON", "#263238", "#25D366"),
                CuratedStickerItem("b3", "curated_barkwanci_nishadi", "GASKIYA NE!", "100% Na Yarda", "🔥", "SHIELD", "#C62828", "#FFE082"),
                CuratedStickerItem("b4", "curated_barkwanci_nishadi", "A SHA SHAYI", "Huta kadan aboki", "☕", "CIRCLE", "#4E342E", "#FFCC80"),
                CuratedStickerItem("b5", "curated_barkwanci_nishadi", "WALLAHI KUWA!", "Ba karya a ciki", "🙌", "BANNER", "#6A1B9A", "#FFD54F"),
                CuratedStickerItem("b6", "curated_barkwanci_nishadi", "AIKIN KENAN!", "Muna nan muna kallo", "👀", "CIRCLE", "#1565C0", "#80D8FF")
            )
        ),
        CuratedStickerPack(
            id = "curated_biki_murna",
            title = "Biki & Taya Murna",
            subtitle = "Sallah, Auren Arewa, Haihuwa da Nasara",
            author = "Zaria Royal Arts",
            category = "Biki & Murna",
            trayEmoji = "🎉",
            accentColorHex = "#7B1FA2",
            downloadsLabel = "15.2K",
            stickers = listOf(
                CuratedStickerItem("m1", "curated_biki_murna", "BARKA DA SALLAH", "Allah ya maimaita mana", "🎉", "STARBURST", "#7B1FA2", "#FFD54F"),
                CuratedStickerItem("m2", "curated_biki_murna", "ALLAH SANYA ALHERI", "Bikin Aure Na Musamman", "👑", "SHIELD", "#AD1457", "#FFE082"),
                CuratedStickerItem("m3", "curated_biki_murna", "MADALLA DA NASARA", "Muna taya ka murna!", "🏆", "HEXAGON", "#075E54", "#FFD54F"),
                CuratedStickerItem("m4", "curated_biki_murna", "BARKA DA HAIHUWA", "Allah ya raya mana", "👶", "CIRCLE", "#00838F", "#FFFFFF"),
                CuratedStickerItem("m5", "curated_biki_murna", "RANAR FARIN CIKI", "A ci gaba da murna", "🥁", "BANNER", "#EF6C00", "#FFF59D"),
                CuratedStickerItem("m6", "curated_biki_murna", "KYAUTA TA MUSAMMAN", "Ga naka nan!", "🎁", "STARBURST", "#283593", "#FFD54F")
            )
        ),
        CuratedStickerPack(
            id = "curated_addua_alheri",
            title = "Addu'a & Fatan Alheri",
            subtitle = "Godiya, addu'o'in samun sauki da albarka",
            author = "ArewaStickers Studio",
            category = "Addu'a & Fatan Alheri",
            trayEmoji = "🤲",
            accentColorHex = "#128C7E",
            downloadsLabel = "31.0K",
            stickers = listOf(
                CuratedStickerItem("a1", "curated_addua_alheri", "ALHAMDULILLAH", "Godiya ta tabbata ga Allah", "🤲", "STARBURST", "#075E54", "#FFD54F"),
                CuratedStickerItem("a2", "curated_addua_alheri", "ALLAH YA BADA SAUKI", "Kaffara ne In Sha Allah", "💚", "SHIELD", "#2E7D32", "#FFFFFF"),
                CuratedStickerItem("a3", "curated_addua_alheri", "ALLAH YA KIYAYE", "Hanya lafiya!", "🚀", "HEXAGON", "#00695C", "#FFE082"),
                CuratedStickerItem("a4", "curated_addua_alheri", "MUN GODE SOSAI", "Allah ya saka da alheri", "🌟", "CIRCLE", "#1565C0", "#FFD54F"),
                CuratedStickerItem("a5", "curated_addua_alheri", "AMIN YA RABBI", "Allah ya amsa addu'a", "🕌", "BANNER", "#37474F", "#25D366"),
                CuratedStickerItem("a6", "curated_addua_alheri", "ALBARKA TA TABBATA", "Cikin dukkan al'amura", "🌺", "CIRCLE", "#6A1B9A", "#FFD54F")
            )
        ),
        CuratedStickerPack(
            id = "curated_kasuwanci_arewa",
            title = "Kasuwanci & Ciniki",
            subtitle = "Na 'yan kasuwa, masu saye da sayarwa a WhatsApp",
            author = "Kantin Kwari Digital",
            category = "Kasuwanci",
            trayEmoji = "💰",
            accentColorHex = "#1565C0",
            downloadsLabel = "12.7K",
            stickers = listOf(
                CuratedStickerItem("k1", "curated_kasuwanci_arewa", "KAYAN SUN ISO!", "Sabon kaya masu kyau", "📦", "STARBURST", "#1565C0", "#FFD54F"),
                CuratedStickerItem("k2", "curated_kasuwanci_arewa", "FARASHI MAI SAUKI", "Yi magana a DM yanzu", "💰", "SHIELD", "#075E54", "#FFFFFF"),
                CuratedStickerItem("k3", "curated_kasuwanci_arewa", "MUN TURA KAYA", "Za ka samu nan ba da jimawa ba", "🚚", "HEXAGON", "#2E7D32", "#FFE082"),
                CuratedStickerItem("k4", "curated_kasuwanci_arewa", "MUNA GODIYA", "Sai wani lokacin!", "🤝", "CIRCLE", "#4527A0", "#FFD54F"),
                CuratedStickerItem("k5", "curated_kasuwanci_arewa", "AIKA KUDI LAFIYA", "Mun ga alert dinka!", "✅", "BANNER", "#263238", "#25D366"),
                CuratedStickerItem("k6", "curated_kasuwanci_arewa", "Kasuwa Ta Bude", "Maraba da masu sayayya", "🌟", "STARBURST", "#C62828", "#FFD54F")
            )
        ),
        CuratedStickerPack(
            id = "curated_soyayya_kauna",
            title = "Soyayya & Kauna",
            subtitle = "Zantukan kauna da kulawa na musamman",
            author = "Arewa Love Studio",
            category = "Soyayya",
            trayEmoji = "❤️",
            accentColorHex = "#C62828",
            downloadsLabel = "19.8K",
            stickers = listOf(
                CuratedStickerItem("s1", "curated_soyayya_kauna", "INA SON KI/KA", "Daga karkashin zuciya", "❤️", "STARBURST", "#C62828", "#FFD54F"),
                CuratedStickerItem("s2", "curated_soyayya_kauna", "KE CE TA MUSAMMAN", "Hasken zuciyata", "🌹", "CIRCLE", "#AD1457", "#FFFFFF"),
                CuratedStickerItem("s3", "curated_soyayya_kauna", "INA KEWAR KA", "Yaushe za mu hadu?", "🥺", "SHIELD", "#6A1B9A", "#FFE082"),
                CuratedStickerItem("s4", "curated_soyayya_kauna", "FARIN CIKINA", "Allah ya bar mu tare", "✨", "HEXAGON", "#075E54", "#FFD54F"),
                CuratedStickerItem("s5", "curated_soyayya_kauna", "KULA DA KANKA", "Ina maka fatan alheri", "💖", "BANNER", "#D81B60", "#FFF59D"),
                CuratedStickerItem("s6", "curated_soyayya_kauna", "MURMUSHINKA", "Yana sa ni farin ciki", "😊", "CIRCLE", "#283593", "#80D8FF")
            )
        )
    )
}
