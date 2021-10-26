package dz.samalgeria.proprice.model.entities.relations

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import dz.samalgeria.proprice.model.entities.Raw
import dz.samalgeria.proprice.model.entities.Receipt
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "RECEIPT_COMPOSITION", primaryKeys = ["receiptID", "rawID"],
    foreignKeys = [
        ForeignKey(
            entity = Receipt::class,
            parentColumns = ["receiptID"],
            childColumns = ["receiptID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Raw::class,
            parentColumns = ["rawID"],
            childColumns = ["rawID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReceiptComposition(
    @ColumnInfo(index = true) var receiptID: Long,
    @ColumnInfo(index = true) val rawID: Long,
    @ColumnInfo var rawQuantity: Float,
    @Ignore var rawName: String
) : Parcelable {
    constructor(receiptID: Long, rawID: Long, rawQuantity: Float) : this(
        receiptID,
        rawID,
        rawQuantity,
        "",
    )
}