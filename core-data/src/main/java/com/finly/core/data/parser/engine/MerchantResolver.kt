package com.finly.core.data.parser.engine

import java.util.Locale

data class ResolvedMerchant(
    val merchantRaw: String?,
    val merchantNormalized: String?
)

object MerchantResolver {

    private val merchantDictionary = mapOf(
        // Food & Delivery
        "swiggy" to "Swiggy",
        "zomato" to "Zomato",
        "blinkit" to "Blinkit",
        "zepto" to "Zepto",
        "bigbasket" to "BigBasket",
        "instamart" to "Swiggy Instamart",
        "domino" to "Dominos",
        "pizza hut" to "Pizza Hut",
        "mcdonald" to "McDonalds",
        "burger king" to "Burger King",
        "starbucks" to "Starbucks",

        // E-Commerce & Retail
        "amazon" to "Amazon",
        "amzn" to "Amazon",
        "flipkart" to "Flipkart",
        "myntra" to "Myntra",
        "ajio" to "Ajio",
        "nykaa" to "Nykaa",
        "zudio" to "Zudio",
        "zara" to "Zara",
        "uniqlo" to "Uniqlo",
        "reliance fresh" to "Reliance Fresh",
        "dmart" to "DMart",

        // Travel & Mobility
        "uber" to "Uber",
        "ola" to "Ola",
        "rapido" to "Rapido",
        "irctc" to "IRCTC",
        "redbus" to "Redbus",
        "makemytrip" to "MakeMyTrip",
        "cleartrip" to "Cleartrip",
        "indigo" to "IndiGo",
        "air india" to "Air India",
        "vistara" to "Vistara",

        // Subscriptions & Media
        "netflix" to "Netflix",
        "spotify" to "Spotify",
        "youtube" to "YouTube Premium",
        "prime video" to "Amazon Prime",
        "hotstar" to "Hotstar",
        "jiocinema" to "JioCinema",

        // Utilities & Telecom
        "airtel" to "Airtel",
        "jio" to "Jio",
        "vi" to "Vi",
        "vodafone" to "Vi",
        "bescom" to "BESCOM",
        "tata power" to "Tata Power",

        // Fuel & Energy
        "shell" to "Shell",
        "bpcl" to "BPCL",
        "hpcl" to "HPCL",
        "iocl" to "IOCL",

        // Healthcare
        "apollo" to "Apollo Pharmacy",
        "medplus" to "MedPlus",
        "pharmeasy" to "PharmEasy",
        "1mg" to "Tata 1mg",

        // Financial & EMI
        "lic" to "LIC",
        "hdfc life" to "HDFC Life",
        "icici prudential" to "ICICI Prudential",
        "bajaj finance" to "Bajaj Finance"
    )

    fun resolveMerchant(rawMerchant: String?, fullText: String): ResolvedMerchant {
        if (rawMerchant.isNullOrBlank()) {
            // Try extracting from full text if raw merchant is missing
            val extracted = extractMerchantFromText(fullText)
            val normalized = normalize(extracted ?: "")
            return ResolvedMerchant(merchantRaw = extracted, merchantNormalized = normalized)
        }

        val cleanRaw = rawMerchant.trim()
        val normalized = normalize(cleanRaw)

        return ResolvedMerchant(
            merchantRaw = cleanRaw,
            merchantNormalized = normalized ?: cleanRaw.capitalizeWords()
        )
    }

    private fun normalize(text: String): String? {
        val lower = text.lowercase(Locale.ROOT)
        for ((key, value) in merchantDictionary) {
            if (lower.contains(key)) {
                return value
            }
        }
        return null
    }

    private fun extractMerchantFromText(text: String): String? {
        val lower = text.lowercase(Locale.ROOT)
        for (key in merchantDictionary.keys) {
            if (lower.contains(key)) {
                return key.capitalizeWords()
            }
        }
        return null
    }

    private fun String.isNull_blank(): Boolean = this.isBlank()

    private fun String.capitalizeWords(): String {
        return this.split(" ").joinToString(" ") { word ->
            word.lowercase(Locale.ROOT).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        }
    }
}
