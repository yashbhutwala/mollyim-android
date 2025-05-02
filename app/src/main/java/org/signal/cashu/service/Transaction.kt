package org.signal.cashu.service

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Transaction : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var amount: Long = 0
    var timestamp: Long = 0
    var type: TransactionType = TransactionType.RECEIVE
    var status: TransactionStatus = TransactionStatus.PENDING
    var memo: String? = null
}

enum class TransactionType {
    SEND,
    RECEIVE
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED
}