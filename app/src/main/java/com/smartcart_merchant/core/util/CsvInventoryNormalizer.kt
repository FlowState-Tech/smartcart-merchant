package com.smartcart_merchant.core.util

object CsvInventoryNormalizer {

    private const val HEADER =
        "sku,name,brand,categoryId,priceAmount,currency,quantity,minThreshold"

    data class Result(
        val bytes: ByteArray,
        val rowCount: Int,
        val lineErrors: List<String> = emptyList(),
        val priceWarnings: List<String> = emptyList()
    )

    fun normalize(rawBytes: ByteArray, fileName: String): Result {
        if (!fileName.endsWith(".csv", ignoreCase = true)) {
            throw IllegalArgumentException(
                "Solo se admiten archivos CSV. Exporta tu Excel como CSV antes de subirlo."
            )
        }

        val content = rawBytes.toString(Charsets.UTF_8)
        val lines = content.lines().filter { it.trim().isNotEmpty() }
        if (lines.isEmpty()) {
            throw IllegalArgumentException("El archivo CSV está vacío")
        }

        val output = mutableListOf(HEADER)
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        var startIndex = 0
        val firstParts = splitCsvLine(lines.first())
        if (firstParts.isNotEmpty() && firstParts[0].equals("sku", ignoreCase = true)) {
            startIndex = 1
        }

        var rowCount = 0
        for (i in startIndex until lines.size) {
            val lineNumber = i + 1
            val parts = splitCsvLine(lines[i])
            if (parts.all { it.isBlank() }) continue
            try {
                val row = normalizeRow(parts, lineNumber)
                output.add(row.csvLine)
                warnings.addAll(row.warnings)
                rowCount++
            } catch (e: IllegalArgumentException) {
                errors.add("Fila $lineNumber: ${e.message}")
            }
        }

        if (rowCount == 0 && errors.isNotEmpty()) {
            throw IllegalArgumentException(errors.joinToString("\n"))
        }
        if (rowCount == 0) {
            throw IllegalArgumentException("No se encontraron filas de productos válidas en el CSV")
        }

        return Result(
            bytes = output.joinToString("\n").toByteArray(Charsets.UTF_8),
            rowCount = rowCount,
            lineErrors = errors,
            priceWarnings = warnings
        )
    }

    private data class NormalizedRow(val csvLine: String, val warnings: List<String>)

    private fun normalizeRow(parts: List<String>, lineNumber: Int): NormalizedRow {
        val warnings = mutableListOf<String>()
        return when {
            parts.size >= 11 -> {
                val price = parts[4].trim().toDoubleOrNull()
                    ?: throw IllegalArgumentException("priceAmount inválido")
                if (price > 10_000) {
                    warnings.add("Fila $lineNumber: precio S/ $price muy alto — confirma antes de subir")
                }
                NormalizedRow(parts.take(11).joinToString(","), warnings)
            }
            parts.size >= 8 -> {
                val price = parts[4].trim().toDoubleOrNull()
                    ?: throw IllegalArgumentException("priceAmount inválido")
                if (price > 10_000) {
                    warnings.add("Fila $lineNumber: precio S/ $price muy alto — confirma antes de subir")
                }
                NormalizedRow(parts.take(8).joinToString(","), warnings)
            }
            parts.size == 4 -> {
                val sku = parts[0].trim()
                val name = parts[1].trim()
                val price = parts[2].trim().toDoubleOrNull()
                    ?: throw IllegalArgumentException("precio inválido")
                val category = parts[3].trim().toLongOrNull() ?: 1L
                if (price > 10_000) {
                    warnings.add("Fila $lineNumber: precio S/ $price muy alto — confirma antes de subir")
                }
                NormalizedRow(
                    listOf(sku, name, "Generic", category.toString(), price.toString(), "PEN", "50", "5")
                        .joinToString(","),
                    warnings
                )
            }
            else -> throw IllegalArgumentException(
                "se requieren 8+ columnas o 4 simplificadas (sku,product_name,price,category)"
            )
        }
    }

    private fun splitCsvLine(line: String): List<String> =
        line.split(",").map { it.trim().removeSurrounding("\"") }
}
