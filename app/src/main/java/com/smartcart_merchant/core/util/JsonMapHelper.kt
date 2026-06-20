package com.smartcart_merchant.core.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonMapHelper {
    private val gson = Gson()

    fun intMapFromJson(json: String?): Map<String, Int> {
        if (json.isNullOrBlank()) return emptyMap()
        return runCatching {
            val type = object : TypeToken<Map<String, Int>>() {}.type
            gson.fromJson<Map<String, Int>>(json, type) ?: emptyMap()
        }.getOrDefault(emptyMap())
    }

    fun intMapToJson(map: Map<String, Int>): String = gson.toJson(map)

    fun doubleMapFromJson(json: String?): Map<String, Double> {
        if (json.isNullOrBlank()) return emptyMap()
        return runCatching {
            val type = object : TypeToken<Map<String, Double>>() {}.type
            gson.fromJson<Map<String, Double>>(json, type) ?: emptyMap()
        }.getOrDefault(emptyMap())
    }

    fun doubleMapToJson(map: Map<String, Double>): String = gson.toJson(map)
}
