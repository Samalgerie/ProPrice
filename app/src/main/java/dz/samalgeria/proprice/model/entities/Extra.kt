package dz.samalgeria.proprice.model.entities


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Extra(
    @PrimaryKey(autoGenerate = true) var extraID: Long = 0L,
    @ColumnInfo(name = "extra_name") var extraName: String,
    @ColumnInfo var extraPrice: Float,
    @ColumnInfo var extraUnit: String
) : Parcelable
