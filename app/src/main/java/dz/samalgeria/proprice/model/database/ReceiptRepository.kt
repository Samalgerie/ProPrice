package dz.samalgeria.proprice.model.database

import androidx.annotation.WorkerThread
import dz.samalgeria.proprice.model.entities.Receipt
import dz.samalgeria.proprice.model.entities.relations.ReceiptComposition
import dz.samalgeria.proprice.model.entities.relations.ReceiptCompositionWithRaw
import kotlinx.coroutines.flow.Flow

class ReceiptRepository(private val dao: ReceiptDao) {

    @WorkerThread
    suspend fun insertReceipt(receipt: Receipt): Long =
        dao.insertReceipt(receipt)

    @WorkerThread
    suspend fun insertReceiptCompositionList(list: List<ReceiptComposition>) {
        dao.insertReceiptCompositionList(list)
    }


    @WorkerThread
    suspend fun updateReceipt(receipt: Receipt) {
        dao.updateReceipt(receipt)
    }

    @WorkerThread
    suspend fun updateReceiptComposition(receiptID: Long, list: List<ReceiptComposition>) {
        dao.updateReceiptComposition(receiptID, list)
    }


    @WorkerThread
    suspend fun deleteReceipt(list: List<Receipt>) {
        dao.deleteReceiptList(list)
    }

    @WorkerThread
    suspend fun deleteAllReceipt() {
        dao.deleteAllReceipt()
    }


    val allReceiptListAsLiveData: Flow<List<Receipt>> = dao.getAllReceiptListAsLiveData()

    @WorkerThread
    suspend fun allReceiptList(): List<Receipt> =
        dao.getAllReceiptList()


    @WorkerThread
    suspend fun receiptList(receiptID: Long): List<Receipt> =
        dao.getReceiptList(receiptID)


    /*   @WorkerThread
       suspend fun receiptCompositionWithRawDetailsList(receiptID: Long): List<ReceiptCompositionWithRawDetails> =
           dao.getReceiptCompositionWithRawDetailsList(receiptID)

   */
    @WorkerThread
    suspend fun receiptCompositionList(receiptID: Long): List<ReceiptComposition> =
        dao.getReceiptCompositionList(receiptID)


    @WorkerThread
    suspend fun allReceiptCompositionWithRawDetails(receiptID: Long): List<ReceiptCompositionWithRaw> =
        dao.getAllReceiptCompositionWithRawDetails(receiptID)


}