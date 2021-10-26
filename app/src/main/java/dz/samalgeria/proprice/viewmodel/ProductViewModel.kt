package dz.samalgeria.proprice.viewmodel

import androidx.lifecycle.*
import dz.samalgeria.proprice.model.database.ProductRepository
import dz.samalgeria.proprice.model.entities.Product
import dz.samalgeria.proprice.model.entities.relations.ProductComposition
import dz.samalgeria.proprice.model.entities.relations.ProductCompositionWithExtra
import kotlinx.coroutines.launch
import java.util.*

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {


    suspend fun insertProduct(product: Product): Long =
        repository.insertProduct(product)


    suspend fun insertProductCompositionList(list: List<ProductComposition>) {
        repository.insertProductCompositionList(list)
    }


    fun updateProduct(product: Product) = viewModelScope.launch {
        repository.updateProduct(product)
    }

    suspend fun updateProductComposition(productID: Long, list: ArrayList<ProductComposition>) {
        repository.updateProductComposition(productID, list)

    }


    suspend fun deleteProduct(list: List<Product>) {
        repository.deleteProduct(list)
    }

    suspend fun deleteAllProduct() {
        repository.deleteAllProduct()
    }


    val allProductList: LiveData<List<Product>> = repository.allProductListAsLiveData.asLiveData()

    suspend fun allProductList(): List<Product> {
        return repository.allProductList()
    }

    suspend fun productCompositionList(productID: Long): List<ProductComposition> {
        return repository.productCompositionList(productID)
    }

    suspend fun allProductCompositionWithExtraDetails(productID: Long): List<ProductCompositionWithExtra> {
        return repository.allProductCompositionWithExtraDetails(productID)
    }


}

class ProductViewModelFactory(private val repository: ProductRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}