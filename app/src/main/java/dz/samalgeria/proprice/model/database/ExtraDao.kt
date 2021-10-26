package dz.samalgeria.proprice.model.database


import androidx.room.*
import dz.samalgeria.proprice.model.entities.Extra
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtraDao {

    @Insert
    suspend fun insertExtra(extra: Extra)

    @Update
    suspend fun updateExtra(extra: Extra)

    @Delete
    suspend fun deleteExtra(extra: Extra)

    @Transaction
    suspend fun deleteExtraList(list: List<Extra>) {
        for (extra in list) deleteExtra(extra)
    }

    @Query("DELETE FROM EXTRA")
    suspend fun deleteAllExtra()


    @Query("SELECT * FROM EXTRA ORDER BY extra_name")
    fun getAllExtraListAsLiveData(): Flow<List<Extra>>

    @Query("SELECT * FROM EXTRA ORDER BY extra_name")
    suspend fun getAllExtraList(): List<Extra>


}