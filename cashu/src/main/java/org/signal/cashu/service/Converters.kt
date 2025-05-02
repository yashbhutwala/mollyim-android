package org.signal.cashu.service

import java.util.Date

/**
 * Utility class containing conversion functions for working with Realm data
 * when converting between entity and domain models.
 */
class Converters {
    /**
     * Converts a timestamp (Long) to a Date object.
     */
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converts a Date object to a timestamp (Long).
     */
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    /**
     * Converts a String representation of TransactionType to the enum value.
     */
    fun fromTransactionType(value: String?): TransactionType? {
        return value?.let { TransactionType.valueOf(it) }
    }

    /**
     * Converts a TransactionType enum to its String representation.
     */
    fun transactionTypeToString(type: TransactionType?): String? {
        return type?.name
    }

    /**
     * Converts a String representation of TransactionStatus to the enum value.
     */
    fun fromTransactionStatus(value: String?): TransactionStatus? {
        return value?.let { TransactionStatus.valueOf(it) }
    }

    /**
     * Converts a TransactionStatus enum to its String representation.
     */
    fun transactionStatusToString(status: TransactionStatus?): String? {
        return status?.name
    }
}