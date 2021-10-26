package dz.samalgeria.proprice.model.entities


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity()
data class Raw(
    @PrimaryKey(autoGenerate = true) var rawID: Long = 0,
    @ColumnInfo var rawName: String,
    @ColumnInfo var rawPrice: Float
) : Parcelable
