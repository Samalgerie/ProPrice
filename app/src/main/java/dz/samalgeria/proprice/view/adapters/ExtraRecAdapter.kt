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
import dz.samalgeria.proprice.databinding.RecycleItemLayoutExtraBinding
import dz.samalgeria.proprice.model.entities.Extra
import dz.samalgeria.proprice.view.fragments.ExtraFragment

import java.util.*
import kotlin.collections.ArrayList


class ExtraRecAdapter(private val fragment: ExtraFragment) :
    RecyclerView.Adapter<ExtraRecAdapter.ViewHolder>(), Filterable {

    private var allExtraList: List<Extra> = mutableListOf()
    var filteredExtraList = ArrayList<Extra>()

    var isCheckBoxList =
        ArrayList<Boolean>() // to keep the record of the checked extra(for deleting them)
    var _isCheckBoxList = ArrayList<Boolean>()

    var allCheckBoxList = ArrayList<CheckBox>() // contains filtered items
    private var _allCheckBoxList = ArrayList<CheckBox>() // contains all items
    private val TAG = "TaggyExtraRecycleViewAdapter"


    var isVisible = false
    var isCheckedAll = false

    private lateinit var binding: RecycleItemLayoutExtraBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RecycleItemLayoutExtraBinding.inflate(
            LayoutInflater.from(fragment.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        allCheckBoxList.add(holder.checkBox)
        _allCheckBoxList.add(holder.checkBox)
        val extra = filteredExtraList[position]
        holder.prefix.text = extra.extraName.first().toString().toUpperCase(Locale.getDefault())
        holder.name.text = extra.extraName.toUpperCase(Locale.getDefault())
        var price = fragment.context!!.resources.getString(R.string.dz0)
        val unit = extra.extraUnit
        Log.d(TAG, "onBindViewHolder: unit $unit")
        price = "$price${unit}"
        price = "${extra.extraPrice}$price"
        holder.price.text = price

        if (isVisible) {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = isCheckBoxList[position]
        } else {
            holder.checkBox.visibility = View.GONE
            holder.checkBox.isChecked = false
        }
        holder.detailButton.setOnClickListener {
            if (isVisible) return@setOnClickListener
            fragment.editExtra(extra)
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
        return filteredExtraList.size
    }

    fun allExtraList(list: List<Extra>) {
        allExtraList = list
        filteredExtraList = allExtraList as ArrayList<Extra>
        Log.d(TAG, "allExtraList: all Extra :$allExtraList")
        isCheckBoxList.clear()
        _isCheckBoxList.clear()
        allCheckBoxList.clear()
        _allCheckBoxList.clear()
        repeat(filteredExtraList.size) {
            isCheckBoxList.add(false)
            _isCheckBoxList.add(false)
        }

        notifyDataSetChanged()

    }

    class ViewHolder(view: RecycleItemLayoutExtraBinding) : RecyclerView.ViewHolder(view.root) {
        val checkBox = view.cbCheckRowExtra
        val prefix = view.tvExtraPrefix
        val name = view.tvExtraName
        val price = view.tvExtraPrice
        val detailButton = view.ibDetailsExtra

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                for (v in _allCheckBoxList)
                    v.isChecked = false
                // allCheckBoxList.clear()
                isCheckBoxList.clear()
                filteredExtraList = if (charSearch.isEmpty()) {
                    // allCheckBoxList.addAll(_allCheckBoxList)
                    isCheckBoxList.addAll(_isCheckBoxList)
                    allExtraList as ArrayList<Extra>
                } else {
                    var count = 0
                    val resultList = ArrayList<Extra>()
                    for (row in allExtraList) {
                        if (row.extraName.toLowerCase(Locale.ROOT).contains(
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
                filterResults.values = filteredExtraList
                Log.d(TAG, "performFiltering: " + filterResults.values)
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // if(results?.count != filteredExtraList.size)  return //  to avoid override the list in the case of deleting an item(s)
                Log.d(TAG, "publishResults: xxx" + results?.values)
                filteredExtraList = results?.values as ArrayList<Extra>
                notifyDataSetChanged()
            }
        }
    }


}