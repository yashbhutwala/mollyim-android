package org.signal.cashu.database

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.signal.cashu.service.TransactionStatus
import org.signal.cashu.service.TransactionType
import javax.inject.Inject

class CashuDatabase @Inject constructor(private val context: Context) {
    private val realm: Realm

    init {
        Realm.init(context)
        // Use a simplified configuration without migration options to avoid Kotlin metadata issues
        val config = RealmConfiguration.Builder()
            .name("cashu_database.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded() // For development, use proper migration in production
            .build()
        realm = Realm.getInstance(config)
    }

    fun transactionDao(): TransactionDao = TransactionDaoImpl(realm)
    fun mintUrlDao(): MintUrlDao = MintUrlDaoImpl(realm)

    fun close() {
        realm.close()
    }
}

open class TransactionEntityRealm : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var amount: Long = 0
    var timestamp: Long = 0
    @Required
    var type: String = ""
    @Required
    var status: String = ""
    var memo: String? = null
}

// Mapping functions between Realm objects and domain models
fun TransactionEntityRealm.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        timestamp = timestamp,
        type = TransactionType.valueOf(type),
        status = TransactionStatus.valueOf(status),
        memo = memo
    )
}

fun TransactionEntity.toRealmObject(): TransactionEntityRealm {
    val entity = TransactionEntityRealm()
    entity.id = this.id
    entity.amount = this.amount
    entity.timestamp = this.timestamp
    entity.type = this.type.name
    entity.status = this.status.name
    entity.memo = this.memo
    return entity
}

open class MintUrlRealm : RealmObject() {
    @PrimaryKey
    var url: String = ""
    var name: String = ""
    var isDefault: Boolean = false
}

fun MintUrlRealm.toEntity(): MintUrl {
    return MintUrl(
        url = url,
        name = name,
        isDefault = isDefault
    )
}

fun MintUrl.toRealmObject(): MintUrlRealm {
    val entity = MintUrlRealm()
    entity.url = this.url
    entity.name = this.name
    entity.isDefault = this.isDefault
    return entity
}

data class TransactionEntity(
    val id: String,
    val amount: Long,
    val timestamp: Long,
    val type: TransactionType,
    val status: TransactionStatus,
    val memo: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransactionEntity

        if (id != other.id) return false
        if (amount != other.amount) return false
        if (timestamp != other.timestamp) return false
        if (type != other.type) return false
        if (status != other.status) return false
        if (memo != other.memo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + (memo?.hashCode() ?: 0)
        return result
    }
}

data class MintUrl(
    val url: String,
    val name: String,
    val isDefault: Boolean = false
)

interface TransactionDao {
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    suspend fun insertTransaction(transaction: TransactionEntity)
    suspend fun getTransactionById(id: String): TransactionEntity?
    suspend fun getTotalAmount(type: TransactionType, status: TransactionStatus): Long?
}

class TransactionDaoImpl(private val realm: Realm) : TransactionDao {
    override fun getAllTransactions(): Flow<List<TransactionEntity>> = flow {
        val transactions = realm.where(TransactionEntityRealm::class.java)
            .findAll()
            .sort("timestamp")

        // Convert RealmResults to List<TransactionEntity>
        emit(transactions.map { it.toEntity() })
    }.flowOn(Dispatchers.IO)

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        withContext(Dispatchers.IO) {
            realm.executeTransaction { r ->
                r.copyToRealmOrUpdate(transaction.toRealmObject())
            }
        }
    }

    override suspend fun getTransactionById(id: String): TransactionEntity? {
        return withContext(Dispatchers.IO) {
            realm.where(TransactionEntityRealm::class.java)
                .equalTo("id", id)
                .findFirst()
                ?.toEntity()
        }
    }

    override suspend fun getTotalAmount(type: TransactionType, status: TransactionStatus): Long? {
        return withContext(Dispatchers.IO) {
            val transactions = realm.where(TransactionEntityRealm::class.java)
                .equalTo("type", type.name)
                .equalTo("status", status.name)
                .findAll()

            // Calculate total manually since Realm sum() operation might not be safe with Kotlin extensions
            transactions.sumOf { it.amount }
        }
    }
}

interface MintUrlDao {
    fun getAllMintUrls(): Flow<List<MintUrl>>
    suspend fun insertMintUrl(mintUrl: MintUrl)
    suspend fun getDefaultMintUrl(): MintUrl?
    suspend fun clearDefaultMintUrl()
    suspend fun setDefaultMintUrl(url: String)
}

class MintUrlDaoImpl(private val realm: Realm) : MintUrlDao {
    override fun getAllMintUrls(): Flow<List<MintUrl>> = flow {
        val mintUrls = realm.where(MintUrlRealm::class.java)
            .findAll()
            .sort("isDefault")

        emit(mintUrls.map { it.toEntity() })
    }.flowOn(Dispatchers.IO)

    override suspend fun insertMintUrl(mintUrl: MintUrl) {
        withContext(Dispatchers.IO) {
            realm.executeTransaction { r ->
                r.copyToRealmOrUpdate(mintUrl.toRealmObject())
            }
        }
    }

    override suspend fun getDefaultMintUrl(): MintUrl? {
        return withContext(Dispatchers.IO) {
            realm.where(MintUrlRealm::class.java)
                .equalTo("isDefault", true)
                .findFirst()
                ?.toEntity()
        }
    }

    override suspend fun clearDefaultMintUrl() {
        withContext(Dispatchers.IO) {
            realm.executeTransaction { r ->
                r.where(MintUrlRealm::class.java)
                    .equalTo("isDefault", true)
                    .findAll()
                    .forEach { it.isDefault = false }
            }
        }
    }

    override suspend fun setDefaultMintUrl(url: String) {
        withContext(Dispatchers.IO) {
            realm.executeTransaction { r ->
                // First clear all existing defaults
                r.where(MintUrlRealm::class.java)
                    .equalTo("isDefault", true)
                    .findAll()
                    .forEach { it.isDefault = false }

                // Then set the new default
                r.where(MintUrlRealm::class.java)
                    .equalTo("url", url)
                    .findFirst()
                    ?.let { it.isDefault = true }
            }
        }
    }
}