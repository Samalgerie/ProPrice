package dz.samalgeria.proprice.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import dz.samalgeria.proprice.R
import dz.samalgeria.proprice.databinding.ItemCustomListLayoutBinding
import dz.samalgeria.proprice.view.fragments.*

class CustomListItemAdapter(
    private val activity: Activity,
    private val fragment: Fragment?,
    private val listItems: List<String>,
) :

    RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {
    private val TAG = "taggyCustomAdapter"
    var isCheckedList = ArrayList<Boolean>()
    var type = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCustomListLayoutBinding =
            ItemCustomListLayoutBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = listItems[position]
        if (fragment is AddUpdateProductFragment) {
            // Log.d(TAG, "onBindViewHolder: $position  ${isCheckedList[position]}")
            holder.tvText.text = item
            if (type == 1) { // extra list
                if (isCheckedList[position]) {
                    holder.tvText.setTextColor(ContextCompat.getColor(activity, R.color.grey_500))
                } else {

                    holder.tvText.setTextColor(ContextCompat.getColor(activity, R.color.grey_900))
                    holder.itemView.setOnClickListener {
                        fragment.selectedListItem(position, type)
                    }
                }

            } else if (type == 2) holder.itemView.setOnClickListener {// receipt list
                fragment.selectedListItem(position, type)
            }

        } else if (fragment is AddUpdateExtraFragment) {
            holder.tvText.text = item
            holder.itemView.setOnClickListener {
                fragment.selectedListItem(item)
            }

        } else if (fragment is AddUpdateReceiptFragment) {
            holder.tvText.text = item
            if (isCheckedList[position]) {
                holder.tvText.setTextColor(ContextCompat.getColor(activity, R.color.grey_500))
            } else {

                holder.tvText.setTextColor(ContextCompat.getColor(activity, R.color.grey_900))
                holder.itemView.setOnClickListener {
                    fragment.selectedListItem(position)
                }
            }

        } else if (fragment is ReceiptFragment) {
            holder.tvText.text = item
            holder.itemView.setOnClickListener {
                //    fragment.selectedListItem(position)
            }

        } else if (fragment is CalculatorFragment) {
            holder.tvText.text = item
            if (item.first() == '*') {
                holder.tvText.setTextColor(ContextCompat.getColor(activity, R.color.grey_500))
            } else {

                holder.tvText.setTextColor(ContextCompat.getColor(activity, R.color.grey_900))
                holder.itemView.setOnClickListener {
                    fragment.selectedListItem(position)
                }
            }


        }


    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    class ViewHolder(view: ItemCustomListLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val tvText = view.tvText
    }
}


