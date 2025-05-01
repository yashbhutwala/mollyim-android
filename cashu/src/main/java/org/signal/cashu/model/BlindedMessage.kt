package org.signal.cashu.model

import com.google.gson.Gson

data class BlindedMessage(
    val amount: Long,
    val blindedMessage: String,
    val id: String
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}