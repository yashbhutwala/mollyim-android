package org.signal.cashu.service

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Long,
    val type: TransactionType,
    val timestamp: Date,
    val status: TransactionStatus,
    val mintUrl: String,
    val memo: String? = null
)

enum class TransactionType {
    SEND,
    RECEIVE,
    MINT,
    MELT
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED
}