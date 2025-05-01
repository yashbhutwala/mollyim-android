package org.signal.cashu.service

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTransactionType(value: String?): TransactionType? {
        return value?.let { TransactionType.valueOf(it) }
    }

    @TypeConverter
    fun transactionTypeToString(type: TransactionType?): String? {
        return type?.name
    }

    @TypeConverter
    fun fromTransactionStatus(value: String?): TransactionStatus? {
        return value?.let { TransactionStatus.valueOf(it) }
    }

    @TypeConverter
    fun transactionStatusToString(status: TransactionStatus?): String? {
        return status?.name
    }
}