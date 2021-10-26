package dz.samalgeria.proprice.viewmodel

import androidx.lifecycle.*
import dz.samalgeria.proprice.model.database.ReceiptRepository
import dz.samalgeria.proprice.model.entities.Receipt
import dz.samalgeria.proprice.model.entities.relations.ReceiptComposition
import dz.samalgeria.proprice.model.entities.relations.ReceiptCompositionWithRaw
import kotlinx.coroutines.launch

class ReceiptViewModel(private val repository: ReceiptRepository) : ViewModel() {

    suspend fun insertReceipt(receipt: Receipt): Long =
        repository.insertReceipt(receipt)


    suspend fun insertReceiptCompositionList(list: ArrayList<ReceiptComposition>) {
        repository.insertReceiptCompositionList(list)
    }


    fun updateReceipt(receipt: Receipt) = viewModelScope.launch {
        repository.updateReceipt(receipt)
    }

    suspend fun updateReceiptComposition(receiptID: Long, list: List<ReceiptComposition>) {
        repository.updateReceiptComposition(receiptID, list)
    }


    suspend fun deleteReceipt(list: List<Receipt>) {
        repository.deleteReceipt(list)
    }

    suspend fun deleteAllReceipt() {
        repository.deleteAllReceipt()
    }

    val allReceiptListAsLiveData: LiveData<List<Receipt>> =
        repository.allReceiptListAsLiveData.asLiveData()

    suspend fun allReceiptList(): List<Receipt> {
        return repository.allReceiptList()
    }

    suspend fun receiptCompositionList(receiptID: Long): ArrayList<ReceiptComposition> =
        repository.receiptCompositionList(receiptID) as ArrayList<ReceiptComposition>


    suspend fun allReceiptCompositionWithExtraDetails(receiptID: Long): List<ReceiptCompositionWithRaw> {
        return repository.allReceiptCompositionWithRawDetails(receiptID)
    }


}

class ReceiptViewModelFactory(private val repository: ReceiptRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReceiptViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReceiptViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}