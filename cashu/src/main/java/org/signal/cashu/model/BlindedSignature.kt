package org.signal.cashu.model

import com.google.gson.Gson

data class BlindedSignature(
    val amount: Long,
    val C_: String,
    val id: String
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}