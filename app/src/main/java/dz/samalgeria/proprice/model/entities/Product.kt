package dz.samalgeria.proprice.model.entities

import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.SET_NULL
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(
    tableName = "PRODUCT", foreignKeys = [
        ForeignKey(
            entity = Receipt::class,
            parentColumns = ["receiptID"],
            childColumns = ["receiptID"],
            onDelete = SET_NULL,
        )
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true) var productID: Long,
    @ColumnInfo() @Nullable var receiptID: Long?,
    @ColumnInfo var productName: String,
    @ColumnInfo var productWeight: Float,
    @ColumnInfo var productDiscount: Float,
) : Parcelable {
/*    constructor(productID: Long, productName: String, productMaxPrice: Float, productWeight: Float) : this(
        productID,
        productName,
        productMaxPrice,
        productWeight,
        "none"
    )*/

}