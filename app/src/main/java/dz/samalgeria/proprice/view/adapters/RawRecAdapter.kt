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
import dz.samalgeria.proprice.databinding.RecycleItemLayoutRawBinding
import dz.samalgeria.proprice.model.entities.Raw
import dz.samalgeria.proprice.view.fragments.RawFragment
import java.util.*
import kotlin.collections.ArrayList


class RawRecAdapter(private val fragment: RawFragment) :
    RecyclerView.Adapter<RawRecAdapter.ViewHolder>(), Filterable {

    private var allRawList: List<Raw> = mutableListOf()
    var filteredRawList = ArrayList<Raw>()

    var isCheckBoxList =
        ArrayList<Boolean>() // to keep the record of the checked raw(for deleting them)
    var _isCheckBoxList = ArrayList<Boolean>()

    var allCheckBoxList = ArrayList<CheckBox>() // contains filtered items
    private var _allCheckBoxList = ArrayList<CheckBox>() // contains all items
    private val TAG = "TaggyRawRecycleViewAdapter"


    var isVisible = false
    var isCheckedAll = false

    private lateinit var binding: RecycleItemLayoutRawBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RecycleItemLayoutRawBinding.inflate(
            LayoutInflater.from(fragment.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        allCheckBoxList.add(holder.checkBox)
        _allCheckBoxList.add(holder.checkBox)
        val raw = filteredRawList[position]
        holder.prefix.text = raw.rawName.first().toString().toUpperCase(Locale.getDefault())
        holder.name.text = raw.rawName.toUpperCase(Locale.getDefault())
        var price = fragment.context!!.resources.getString(R.string.dz)
        price = "${raw.rawPrice}$price"
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
            fragment.editRaw(raw)
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
        return filteredRawList.size
    }

    fun allRawList(list: List<Raw>) {
        allRawList = list
        filteredRawList = allRawList as ArrayList<Raw>
        Log.d(TAG, "allRawList: all Raw :$allRawList")
        isCheckBoxList.clear()
        _isCheckBoxList.clear()
        allCheckBoxList.clear()
        _allCheckBoxList.clear()
        repeat(filteredRawList.size) {
            isCheckBoxList.add(false)
            _isCheckBoxList.add(false)
        }

        notifyDataSetChanged()

    }

    class ViewHolder(view: RecycleItemLayoutRawBinding) : RecyclerView.ViewHolder(view.root) {
        val checkBox = view.cbCheckRowRaw
        val prefix = view.tvRawPrefix
        val name = view.tvRawName
        val price = view.tvRawPrice
        val detailButton = view.ibDetailsRaw

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                for (v in _allCheckBoxList)
                    v.isChecked = false
                // allCheckBoxList.clear()
                isCheckBoxList.clear()
                filteredRawList = if (charSearch.isEmpty()) {
                    // allCheckBoxList.addAll(_allCheckBoxList)
                    isCheckBoxList.addAll(_isCheckBoxList)
                    allRawList as ArrayList<Raw>
                } else {
                    var count = 0
                    val resultList = ArrayList<Raw>()
                    for (row in allRawList) {
                        if (row.rawName.toLowerCase(Locale.ROOT).contains(
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
                filterResults.values = filteredRawList
                Log.d(TAG, "performFiltering: " + filterResults.values)
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // if(results?.count != filteredRawList.size)  return //  to avoid override the list in the case of deleting an item(s)
                Log.d(TAG, "publishResults: xxx" + results?.values)
                filteredRawList = results?.values as ArrayList<Raw>
                notifyDataSetChanged()
            }
        }
    }


}