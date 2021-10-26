package dz.samalgeria.proprice.model.database


import androidx.room.*
import dz.samalgeria.proprice.model.entities.Raw
import kotlinx.coroutines.flow.Flow

@Dao
interface RawDao {


    @Insert
    suspend fun insertRaw(raw: Raw)


    @Update
    suspend fun updateRaw(raw: Raw)


    @Delete
    suspend fun deleteRaw(raw: Raw)

    @Transaction
    suspend fun deleteRawList(list: List<Raw>) {
        for (raw in list) deleteRaw(raw)
    }

    @Query("DELETE FROM RAW")
    suspend fun deleteAllRaw()


    @Query("SELECT * FROM RAW ORDER BY rawName")
    fun getAllRawListAsLiveData(): Flow<List<Raw>>

    @Query("SELECT * FROM RAW ORDER BY rawName")
    suspend fun getAllRawList(): List<Raw>

}