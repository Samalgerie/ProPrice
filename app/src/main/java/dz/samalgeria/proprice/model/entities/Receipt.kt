package dz.samalgeria.proprice.model.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity()
data class Receipt(
    @PrimaryKey(autoGenerate = true) var receiptID: Long,
    @ColumnInfo var receiptName: String,
    @ColumnInfo var receiptWeight: Float
) : Parcelable