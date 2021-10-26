package dz.samalgeria.proprice.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dz.samalgeria.proprice.R
import dz.samalgeria.proprice.databinding.RecycleItemLayoutProductBinding
import dz.samalgeria.proprice.model.entities.Product
import dz.samalgeria.proprice.view.fragments.ProductFragment
import java.util.*
import kotlin.collections.ArrayList


class ProductRecAdapter(private val fragment: ProductFragment) :
    RecyclerView.Adapter<ProductRecAdapter.ViewHolder>(), Filterable {

    private var allProductList: List<Product> = mutableListOf()
    var filteredProductList = ArrayList<Product>()

    var isCheckBoxList =
        ArrayList<Boolean>() // to keep the record of the checked raw(for deleting them)
    var _isCheckBoxList = ArrayList<Boolean>()

    var allCheckBoxList = ArrayList<CheckBox>() // contains filtered items
    private var _allCheckBoxList = ArrayList<CheckBox>() // contains all items
    private val TAG = "TaggyProductRecycleViewAdapter"


    var isVisible = false
    var isCheckedAll = false

    private lateinit var binding: RecycleItemLayoutProductBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RecycleItemLayoutProductBinding.inflate(
            LayoutInflater.from(fragment.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            allCheckBoxList.add(checkBox)
            _allCheckBoxList.add(checkBox)
            val product = filteredProductList[position]
            prefix.text =
                product.productName.first().toString().toUpperCase(Locale.getDefault())
            name.text = product.productName.toUpperCase(Locale.getDefault())
            var value = product.productDiscount.toString()
            discount.text = "$value%"
            value = fragment.context!!.resources.getString(R.string.kg)
            value = "${product.productWeight}$value"
            weight.text = value
            if (product.receiptID == null) {
                val drawable = ContextCompat.getDrawable(
                    fragment.context!!,
                    R.drawable.ic_grey_icon_calculator
                )
                calculatorButton.setImageDrawable(drawable)
            } else {
                val drawable =
                    ContextCompat.getDrawable(fragment.context!!, R.drawable.ic_icon_calculator)
                calculatorButton.setImageDrawable(drawable)
            }


            if (isVisible) {
                checkBox.visibility = View.VISIBLE
                checkBox.isChecked = isCheckBoxList[position]
            } else {
                checkBox.visibility = View.GONE
                checkBox.isChecked = false
            }
            detailButton.setOnClickListener {
                if (isVisible) return@setOnClickListener
                fragment.editProduct(product)
            }
            calculatorButton.setOnClickListener {
                if (isVisible) return@setOnClickListener
                else {
                    if (product.receiptID != null)
                        fragment.calculator(product)
                }
            }

            itemView.setOnLongClickListener {
                // Log.d(TAG, "onBindViewHolder: size " + allCheckBoxList.size)
                if (!isVisible) {
                    isVisible = true
                    for (v in allCheckBoxList)
                        v.visibility = View.VISIBLE
                    fragment.showAllCheckBox()
                    checkBox.isChecked = true
                    isCheckBoxList[position] = true
                    fragment.increaseCounter()
                    //  notifyDataSetChanged()
                }
                true
            }

            checkBox.setOnClickListener {
                if (checkBox.isChecked) {
                    isCheckBoxList[position] = true
                    fragment.increaseCounter()
                } else {
                    isCheckBoxList[position] = false
                    fragment.decreaseCounter()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredProductList.size
    }

    fun allProductList(list: List<Product>) {
        allProductList = list
        filteredProductList = allProductList as ArrayList<Product>
        Log.d(TAG, "allProductList: all Product :$allProductList")
        isCheckBoxList.clear()
        _isCheckBoxList.clear()
        allCheckBoxList.clear()
        _allCheckBoxList.clear()
        repeat(filteredProductList.size) {
            isCheckBoxList.add(false)
            _isCheckBoxList.add(false)
        }

        notifyDataSetChanged()

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                for (v in _allCheckBoxList)
                    v.isChecked = false
                // allCheckBoxList.clear()
                isCheckBoxList.clear()
                filteredProductList = if (charSearch.isEmpty()) {
                    // allCheckBoxList.addAll(_allCheckBoxList)
                    isCheckBoxList.addAll(_isCheckBoxList)
                    allProductList as ArrayList<Product>
                } else {
                    var count = 0
                    val resultList = ArrayList<Product>()
                    for (row in allProductList) {
                        if (row.productName.toLowerCase(Locale.ROOT).contains(
                                constraint.toString().toLowerCase(
                                    Locale.ROOT
                                )
                            )
                        ) {
                            resultList.add(row)
                            // allCheckBoxList.add(_allCheckBoxList[count])
                            isCheckBoxList.add(_isCheckBoxList[count])
                        }
                        count++
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredProductList
                Log.d(TAG, "performFiltering: " + filterResults.values)
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // if(results?.count != filteredProductList.size)  return //  to avoid override the list in the case of deleting an item(s)
                Log.d(TAG, "publishResults: xxx" + results?.values)
                filteredProductList = results?.values as ArrayList<Product>
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(view: RecycleItemLayoutProductBinding) : RecyclerView.ViewHolder(view.root) {
        val checkBox = view.cbCheckRowProduct
        val prefix = view.tvProductPrefix
        val name = view.tvProductName
        val discount = view.tvDiscount
        val weight = view.tvProductWeight
        val detailButton = view.ibDetailsProduct
        val calculatorButton = view.ibCalculator

    }

}