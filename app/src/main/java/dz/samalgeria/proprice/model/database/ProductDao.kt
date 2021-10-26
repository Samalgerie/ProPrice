package dz.samalgeria.proprice.model.database


import androidx.room.*
import dz.samalgeria.proprice.model.entities.Product
import dz.samalgeria.proprice.model.entities.relations.ProductComposition
import dz.samalgeria.proprice.model.entities.relations.ProductCompositionWithExtra
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {


    @Insert
    suspend fun insertProduct(product: Product): Long

    @Insert
    suspend fun insertCompositionProduct(productComposition: ProductComposition)


    @Transaction
    suspend fun insertProductCompositionList(list: List<ProductComposition>) {
        list.forEach { insertCompositionProduct(it) }
    }

    @Update
    suspend fun updateProduct(product: Product)


    suspend fun updateProductComposition(productID: Long, list: List<ProductComposition>) {
        deleteCompositionProduct(productID)
        list.forEach { insertCompositionProduct(it) }
    }

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM PRODUCT_COMPOSITION WHERE productID =:productID")
    suspend fun deleteCompositionProduct(productID: Long)

    @Transaction
    suspend fun deleteProductList(list: List<Product>) {
        for (product in list) deleteProduct(product)
    }

    @Query("DELETE FROM PRODUCT")
    suspend fun deleteAllProduct()


    @Query("SELECT * FROM PRODUCT ORDER BY productName")
    fun getAllProductListAsLiveData(): Flow<List<Product>>

    @Query("SELECT * FROM PRODUCT ORDER BY productName")
    suspend fun getAllProductList(): List<Product>


    @Query("SELECT * FROM PRODUCT_COMPOSITION WHERE productID= :productID")
    suspend fun getProductCompositionList(productID: Long): List<ProductComposition>

    @Query("SELECT * FROM PRODUCT_COMPOSITION WHERE productID= :productID ORDER BY extraID")
    suspend fun getAllProductCompositionWithExtraDetails(productID: Long): List<ProductCompositionWithExtra>


}