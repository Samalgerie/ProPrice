package dz.samalgeria.proprice.model.entities.relations

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import dz.samalgeria.proprice.model.entities.Extra
import dz.samalgeria.proprice.model.entities.Product
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "PRODUCT_COMPOSITION",
    primaryKeys = ["productID", "extraID"],
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["productID"],
            childColumns = ["productID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Extra::class,
            parentColumns = ["extraID"],
            childColumns = ["extraID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProductComposition(
    @ColumnInfo(index = true) var productID: Long,
    @ColumnInfo(index = true) var extraID: Long,
    @ColumnInfo var extraQuantity: Float,
    @Ignore var extraName: String
) : Parcelable {
    constructor(productID: Long, extraID: Long, extraQuantity: Float) : this(
        productID,
        extraID,
        extraQuantity,
        "",
    )
}