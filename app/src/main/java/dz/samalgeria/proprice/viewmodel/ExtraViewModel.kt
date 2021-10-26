package dz.samalgeria.proprice.viewmodel

import androidx.lifecycle.*
import dz.samalgeria.proprice.model.database.ExtraRepository
import dz.samalgeria.proprice.model.entities.Extra
import kotlinx.coroutines.launch

class ExtraViewModel(private val repository: ExtraRepository) : ViewModel() {

    fun insertExtra(extra: Extra) = viewModelScope.launch {
        repository.insertExtra(extra)
    }

    fun updateExtra(extra: Extra) = viewModelScope.launch {
        repository.updateExtra(extra)
    }

    suspend fun deleteExtra(list: List<Extra>) {
        repository.deleteExtra(list)
    }

    suspend fun deleteAllExtra() {
        repository.deleteAllExtra()
    }

    val allExtraListAsLiveData: LiveData<List<Extra>> =
        repository.allExtraListAsLiveData.asLiveData()

    suspend fun allExtraList(): List<Extra> {
        return repository.allExtraList()
    }

}

class ExtraViewModelFactory(private val repository: ExtraRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExtraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExtraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}