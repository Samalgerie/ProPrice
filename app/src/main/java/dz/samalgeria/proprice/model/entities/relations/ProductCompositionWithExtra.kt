package dz.samalgeria.proprice.model.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import dz.samalgeria.proprice.model.entities.Extra

data class ProductCompositionWithExtra(
    @Embedded var productComposition: ProductComposition,
    @Relation(
        parentColumn = "extraID",
        entityColumn = "extraID",
    )
    var extraDetails: Extra
)