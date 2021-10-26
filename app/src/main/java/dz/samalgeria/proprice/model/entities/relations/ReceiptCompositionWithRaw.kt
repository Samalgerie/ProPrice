package dz.samalgeria.proprice.model.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import dz.samalgeria.proprice.model.entities.Raw

data class ReceiptCompositionWithRaw(
    @Embedded var receiptComposition: ReceiptComposition,
    @Relation(
        parentColumn = "rawID",
        entityColumn = "rawID",
    )
    var rawDetails: Raw,

    )