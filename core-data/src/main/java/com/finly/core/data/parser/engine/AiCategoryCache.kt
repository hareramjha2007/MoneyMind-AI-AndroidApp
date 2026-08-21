package com.finly.core.data.parser.engine

import java.util.concurrent.ConcurrentHashMap

object AiCategoryCache {
    private val memoryCache = ConcurrentHashMap<String, String>()

    fun get(key: String): String? {
        return memoryCache[key.lowercase().trim()]
    }

    fun put(key: String, category: String) {
        memoryCache[key.lowercase().trim()] = category
    }
}
