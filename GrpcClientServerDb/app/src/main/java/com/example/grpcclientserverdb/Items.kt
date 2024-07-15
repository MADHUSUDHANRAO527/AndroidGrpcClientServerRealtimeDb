package com.example.myapplication

data class Items(
    val id: String? = "",
    val itemName: String? = "",
    val itemQnty: String? = ""
)
fun Items.toMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "itemName" to itemName,
        "itemQnty" to itemQnty
    )
}
