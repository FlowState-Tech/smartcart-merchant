package com.smartcart_merchant.core.util

data class RetailCategory(
    val id: Long,
    val name: String,
    val description: String = ""
)

object CategoryCatalog {
    val defaults: List<RetailCategory> = listOf(
        RetailCategory(1, "Abarrotes", "Productos de despensa básica"),
        RetailCategory(2, "Lácteos", "Leche, quesos y derivados"),
        RetailCategory(3, "Bebidas", "Gaseosas, jugos y agua"),
        RetailCategory(4, "Cuidado Personal", "Higiene y cuidado personal"),
        RetailCategory(5, "Limpieza", "Artículos de limpieza del hogar"),
        RetailCategory(6, "Snacks", "Golosinas y snacks"),
        RetailCategory(7, "Panadería", "Pan y productos de panadería"),
        RetailCategory(8, "Congelados", "Productos congelados")
    )

    fun nameFor(id: Long): String = defaults.find { it.id == id }?.name ?: "Cat. $id"
}
