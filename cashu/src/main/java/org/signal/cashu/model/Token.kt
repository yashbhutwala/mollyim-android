package org.signal.cashu.model

import com.google.gson.annotations.SerializedName
import com.google.gson.Gson

data class Token(
    @SerializedName("token")
    val token: List<BlindedMessage>,
    @SerializedName("mint")
    val mint: String,
    @SerializedName("proofs")
    val proofs: List<Proof>
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}