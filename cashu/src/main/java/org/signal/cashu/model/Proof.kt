package org.signal.cashu.model

import com.google.gson.Gson

data class Proof(
    val amount: Long,
    val C: String,
    val secret: String,
    val id: String
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}