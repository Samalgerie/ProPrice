package dz.samalgeria.proprice.viewmodel

import androidx.lifecycle.*
import dz.samalgeria.proprice.model.database.RawRepository
import dz.samalgeria.proprice.model.entities.Raw
import kotlinx.coroutines.launch

class RawViewModel(private val repository: RawRepository) : ViewModel() {

    fun insertRaw(raw: Raw) = viewModelScope.launch {
        repository.insertRaw(raw)
    }

    val allRawListAsLiveData: LiveData<List<Raw>> = repository.allRawListAsLiveData.asLiveData()

    suspend fun allRawList(): List<Raw> {
        return repository.allRawList()
    }

    fun updateRaw(raw: Raw) = viewModelScope.launch {
        repository.updateRaw(raw)
    }

    suspend fun deleteRaw(list: List<Raw>) {
        repository.deleteRaw(list)
    }

    suspend fun deleteAllRaw() {
        repository.deleteAllRaw()
    }


}

class RawViewModelFactory(private val repository: RawRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RawViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RawViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}