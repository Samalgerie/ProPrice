package dz.samalgeria.proprice.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dz.samalgeria.proprice.model.entities.Extra
import dz.samalgeria.proprice.model.entities.Product
import dz.samalgeria.proprice.model.entities.Raw
import dz.samalgeria.proprice.model.entities.Receipt
import dz.samalgeria.proprice.model.entities.relations.ProductComposition
import dz.samalgeria.proprice.model.entities.relations.ReceiptComposition

@Database(
    entities = [Raw::class, Extra::class, Receipt::class, Product::class, ReceiptComposition::class, ProductComposition::class],
    version = 1
)
abstract class
WeMixRoomDatabase : RoomDatabase() {
    abstract fun rawDao(): RawDao
    abstract fun extraDao(): ExtraDao
    abstract fun productDao(): ProductDao
    abstract fun receiptDao(): ReceiptDao

    companion object {
        @Volatile
        private var INSTANCE: WeMixRoomDatabase? = null

        fun getDatabase(context: Context): WeMixRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeMixRoomDatabase::class.java,
                    "wemix"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}