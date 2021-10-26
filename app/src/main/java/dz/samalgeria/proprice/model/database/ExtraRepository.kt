package dz.samalgeria.proprice.model.database

import androidx.annotation.WorkerThread
import dz.samalgeria.proprice.model.entities.Extra
import kotlinx.coroutines.flow.Flow

class ExtraRepository(private val dao: ExtraDao) {
    // private val TAG = "ExtraRepository"

    @WorkerThread
    suspend fun insertExtra(extra: Extra) {
        dao.insertExtra(extra)
    }

    @WorkerThread
    suspend fun updateExtra(extra: Extra) {
        dao.updateExtra(extra)
    }


    @WorkerThread
    suspend fun deleteExtra(list: List<Extra>) {
        dao.deleteExtraList(list)
    }

    @WorkerThread
    suspend fun deleteAllExtra() {
        dao.deleteAllExtra()
    }


    val allExtraListAsLiveData: Flow<List<Extra>> = dao.getAllExtraListAsLiveData()

    @WorkerThread
    suspend fun allExtraList(): List<Extra> = dao.getAllExtraList()


}