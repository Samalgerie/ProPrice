package dz.samalgeria.proprice.model.database


import androidx.room.*
import dz.samalgeria.proprice.model.entities.Receipt
import dz.samalgeria.proprice.model.entities.relations.ReceiptComposition
import dz.samalgeria.proprice.model.entities.relations.ReceiptCompositionWithRaw
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {

    @Insert
    suspend fun insertReceipt(receipt: Receipt): Long

    @Insert
    suspend fun insertCompositionReceipt(receiptComposition: ReceiptComposition)

    @Transaction
    suspend fun insertReceiptCompositionList(list: List<ReceiptComposition>) {
        list.forEach { insertCompositionReceipt(it) }
    }


    @Update
    suspend fun updateReceipt(receipt: Receipt)

    @Transaction
    suspend fun updateReceiptComposition(receiptID: Long, list: List<ReceiptComposition>) {
        deleteReceiptCompositionList(receiptID)
        insertReceiptCompositionList(list)
    }


    @Delete
    suspend fun deleteReceipt(receipt: Receipt)


    @Query("DELETE FROM RECEIPT")
    suspend fun deleteAllReceipt()

    suspend fun deleteReceiptList(list: List<Receipt>) {
        list.forEach {
            deleteReceipt(it)
        }
    }

    @Transaction
    @Query("DELETE FROM RECEIPT_COMPOSITION WHERE receiptID = :receiptID")
    suspend fun deleteReceiptCompositionList(receiptID: Long)


    @Query("SELECT * FROM RECEIPT")
    suspend fun getAllReceiptList(): List<Receipt>

    @Query("SELECT * FROM RECEIPT WHERE receiptID = :receiptID")
    suspend fun getReceiptList(receiptID: Long): List<Receipt>

/*
    @Query("SELECT * FROM RECEIPT WHERE receiptID = :receiptID")
    suspend fun getReceiptCompositionWithRawDetailsList(receiptID: Long): List<ReceiptCompositionWithRawDetails>
*/

    @Query("SELECT * FROM RECEIPT ORDER BY receiptName")
    fun getAllReceiptListAsLiveData(): Flow<List<Receipt>>

    @Query("SELECT * FROM RECEIPT_COMPOSITION WHERE receiptID = :receiptID")
    suspend fun getReceiptCompositionList(receiptID: Long): List<ReceiptComposition>


    @Query("SELECT * FROM RECEIPT_COMPOSITION WHERE receiptID = :receiptID ORDER BY rawID")
    suspend fun getAllReceiptCompositionWithRawDetails(receiptID: Long): List<ReceiptCompositionWithRaw>


}