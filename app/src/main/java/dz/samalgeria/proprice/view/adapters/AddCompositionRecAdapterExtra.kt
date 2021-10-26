package dz.samalgeria.proprice.view.adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dz.samalgeria.proprice.databinding.AddCompositionRecAdapterLayoutExtraBinding
import dz.samalgeria.proprice.model.entities.relations.ProductComposition
import dz.samalgeria.proprice.utils.Constants
import dz.samalgeria.proprice.view.fragments.AddUpdateProductFragment


class AddCompositionRecAdapterExtra(
    private val fragment: AddUpdateProductFragment,
) :
    RecyclerView.Adapter<AddCompositionRecAdapterExtra.ViewHolder>() {
    private var listItems: List<ProductComposition> = mutableListOf()
    private var positionListItems = ArrayList<Boolean>()
    private val TAG = "TAGGYAddExtraAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AddCompositionRecAdapterLayoutExtraBinding =
            AddCompositionRecAdapterLayoutExtraBinding.inflate(
                LayoutInflater.from(fragment.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")
        val item = listItems[position]
        with(holder) {
            tvName.text = item.extraName
            etQuantity.setText(Constants.display(item.extraQuantity))
            etQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(value: Editable?) {
                    try {
                        val quality = value.toString().toFloat()
                        if (listItems[position].extraQuantity != quality) {
                            listItems[position].extraQuantity = quality
                            fragment.updateAllValues()
                        }
                    } catch (e: Exception) {
                    }

                }
            })

        }

        holder.ibDeleteButton.setOnClickListener {
            fragment.deleteExtraItem(position, item.productID.toInt())
        }
    }


    override fun getItemCount(): Int {
        return listItems.size

    }

    fun updateItemList(list: List<ProductComposition>) {
        listItems = list
        positionListItems.clear()
        repeat(list.size) { positionListItems.add(false) }
        notifyDataSetChanged()

    }


    class ViewHolder(view: AddCompositionRecAdapterLayoutExtraBinding) :
        RecyclerView.ViewHolder(view.root) {
        val tvName = view.tvIflExtraName2
        val etQuantity = view.etInfExtraQuantity2
        val ibDeleteButton = view.ibIflExtraDelete
    }
}