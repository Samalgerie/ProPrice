package dz.samalgeria.proprice.model.database

import androidx.annotation.WorkerThread
import dz.samalgeria.proprice.model.entities.Raw
import kotlinx.coroutines.flow.Flow

class RawRepository(private val dao: RawDao) {

    @WorkerThread
    suspend fun insertRaw(raw: Raw) {
        dao.insertRaw(raw)
    }


    @WorkerThread
    suspend fun updateRaw(raw: Raw) {
        dao.updateRaw(raw)
    }


    @WorkerThread
    suspend fun deleteRaw(list: List<Raw>) {
        dao.deleteRawList(list)
    }

    @WorkerThread
    suspend fun deleteAllRaw() {
        dao.deleteAllRaw()
    }


    val allRawListAsLiveData: Flow<List<Raw>> = dao.getAllRawListAsLiveData()

    @WorkerThread
    suspend fun allRawList(): List<Raw> = dao.getAllRawList()


}