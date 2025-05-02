package org.signal.cashu.service

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.Date

open class Transaction : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var amount: Long = 0
    var type: String = ""
    var timestamp: Long = 0
    var status: String = ""
    var mintUrl: String = ""
    var memo: String? = null

    companion object {
        fun create(
            id: Long = 0,
            amount: Long,
            type: TransactionType,
            timestamp: Date,
            status: TransactionStatus,
            mintUrl: String,
            memo: String? = null
        ): Transaction {
            val transaction = Transaction()
            transaction.id = id
            transaction.amount = amount
            transaction.type = type.name
            transaction.timestamp = timestamp.time
            transaction.status = status.name
            transaction.mintUrl = mintUrl
            transaction.memo = memo
            return transaction
        }
    }

    fun getTransactionType(): TransactionType {
        return TransactionType.valueOf(type)
    }

    fun getTransactionStatus(): TransactionStatus {
        return TransactionStatus.valueOf(status)
    }

    fun getTimestampAsDate(): Date {
        return Date(timestamp)
    }
}

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