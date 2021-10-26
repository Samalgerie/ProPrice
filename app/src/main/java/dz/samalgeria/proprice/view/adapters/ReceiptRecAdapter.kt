package dz.samalgeria.proprice.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import dz.samalgeria.proprice.R
import dz.samalgeria.proprice.databinding.RecycleItemLayoutReceiptBinding
import dz.samalgeria.proprice.model.entities.Receipt
import dz.samalgeria.proprice.view.fragments.ReceiptFragment

import java.util.*
import kotlin.collections.ArrayList


class ReceiptRecAdapter(private val fragment: ReceiptFragment) :
    RecyclerView.Adapter<ReceiptRecAdapter.ViewHolder>(), Filterable {

    private var allReceiptList: List<Receipt> = mutableListOf()
    var filteredReceiptList = ArrayList<Receipt>()

    var isCheckBoxList =
        ArrayList<Boolean>() // to keep the record of the checked receipt(for deleting them)
    var _isCheckBoxList = ArrayList<Boolean>()

    var allCheckBoxList = ArrayList<CheckBox>() // contains filtered items
    private var _allCheckBoxList = ArrayList<CheckBox>() // contains all items
    private val TAG = "TaggyReceiptRecycleViewAdapter"


    var isVisible = false
    var isCheckedAll = false

    private lateinit var binding: RecycleItemLayoutReceiptBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RecycleItemLayoutReceiptBinding.inflate(
            LayoutInflater.from(fragment.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        allCheckBoxList.add(holder.checkBox)
        _allCheckBoxList.add(holder.checkBox)
        val receipt = filteredReceiptList[position]
        holder.prefix.text = receipt.receiptName.first().toString().toUpperCase(Locale.getDefault())
        holder.name.text = receipt.receiptName.toUpperCase(Locale.getDefault())
        var weight = fragment.context!!.resources.getString(R.string.kg)
        weight = "${receipt.receiptWeight}$weight"
        holder.weight.text = weight

        if (isVisible) {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = isCheckBoxList[position]
        } else {
            holder.checkBox.visibility = View.GONE
            holder.checkBox.isChecked = false
        }
        holder.detailButton.setOnClickListener {
            if (isVisible) return@setOnClickListener
            fragment.editReceipt(receipt)
        }

        holder.itemView.setOnLongClickListener {
            // Log.d(TAG, "onBindViewHolder: size " + allCheckBoxList.size)
            if (!isVisible) {
                isVisible = true
                for (v in allCheckBoxList)
                    v.visibility = View.VISIBLE
                fragment.showAllCheckBox()
                holder.checkBox.isChecked = true
                isCheckBoxList[position] = true
                fragment.increaseCounter()
                //  notifyDataSetChanged()
            }
            true
        }

        holder.checkBox.setOnClickListener {
            if (holder.checkBox.isChecked) {
                isCheckBoxList[position] = true
                fragment.increaseCounter()
            } else {
                isCheckBoxList[position] = false
                fragment.decreaseCounter()
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredReceiptList.size
    }

    fun allReceiptList(list: List<Receipt>) {
        allReceiptList = list
        filteredReceiptList = allReceiptList as ArrayList<Receipt>
        Log.d(TAG, "allReceiptList: all Receipt :$allReceiptList")
        isCheckBoxList.clear()
        _isCheckBoxList.clear()
        allCheckBoxList.clear()
        _allCheckBoxList.clear()
        repeat(filteredReceiptList.size) {
            isCheckBoxList.add(false)
            _isCheckBoxList.add(false)
        }

        notifyDataSetChanged()

    }

    class ViewHolder(view: RecycleItemLayoutReceiptBinding) : RecyclerView.ViewHolder(view.root) {
        val checkBox = view.cbCheckRowReceipt
        val prefix = view.tvReceiptPrefix
        val name = view.tvReceiptName
        val weight = view.tvReceiptWeight
        val detailButton = view.ibDetailsReceipt

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                for (v in _allCheckBoxList)
                    v.isChecked = false
                // allCheckBoxList.clear()
                isCheckBoxList.clear()
                filteredReceiptList = if (charSearch.isEmpty()) {
                    // allCheckBoxList.addAll(_allCheckBoxList)
                    isCheckBoxList.addAll(_isCheckBoxList)
                    allReceiptList as ArrayList<Receipt>
                } else {
                    var count = 0
                    val resultList = ArrayList<Receipt>()
                    for (row in allReceiptList) {
                        if (row.receiptName.toLowerCase(Locale.ROOT).contains(
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
                filterResults.values = filteredReceiptList
                Log.d(TAG, "performFiltering: " + filterResults.values)
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // if(results?.count != filteredReceiptList.size)  return //  to avoid override the list in the case of deleting an item(s)
                Log.d(TAG, "publishResults: xxx" + results?.values)
                filteredReceiptList = results?.values as ArrayList<Receipt>
                notifyDataSetChanged()
            }
        }
    }


}