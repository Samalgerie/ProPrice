package dz.samalgeria.proprice.model.database

import androidx.annotation.WorkerThread
import dz.samalgeria.proprice.model.entities.Product
import dz.samalgeria.proprice.model.entities.relations.ProductComposition
import dz.samalgeria.proprice.model.entities.relations.ProductCompositionWithExtra
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val dao: ProductDao) {

    @WorkerThread
    suspend fun insertProduct(product: Product): Long =
        dao.insertProduct(product)

    @WorkerThread
    suspend fun insertProductCompositionList(list: List<ProductComposition>) {
        dao.insertProductCompositionList(list)
    }


    @WorkerThread
    suspend fun updateProduct(product: Product) {
        dao.updateProduct(product)
    }

    @WorkerThread
    suspend fun updateProductComposition(productID: Long, list: List<ProductComposition>) {
        dao.updateProductComposition(productID, list)
    }


    @WorkerThread
    suspend fun deleteProduct(list: List<Product>) {
        dao.deleteProductList(list)
    }

    @WorkerThread
    suspend fun deleteAllProduct() {
        dao.deleteAllProduct()
    }


    val allProductListAsLiveData: Flow<List<Product>> = dao.getAllProductListAsLiveData()

    @WorkerThread
    suspend fun allProductList(): List<Product> = dao.getAllProductList()

    @WorkerThread
    suspend fun productCompositionList(productID: Long): List<ProductComposition> =
        dao.getProductCompositionList(productID)

    @WorkerThread
    suspend fun allProductCompositionWithExtraDetails(productID: Long): List<ProductCompositionWithExtra> =
        dao.getAllProductCompositionWithExtraDetails(productID)


}