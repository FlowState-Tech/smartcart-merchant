package com.smartcart_merchant.core.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smartcart_merchant.features.store.domain.model.OperatingHour

object OperatingHoursJsonHelper {
    private val gson = Gson()

    fun fromJson(json: String?): List<OperatingHour> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            val type = object : TypeToken<List<OperatingHour>>() {}.type
            gson.fromJson<List<OperatingHour>>(json, type) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    fun toJson(hours: List<OperatingHour>): String = gson.toJson(hours)
}
