package dz.samalgeria.proprice.view.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dz.samalgeria.proprice.databinding.AddCompositionRecAdapterLayoutRawBinding
import dz.samalgeria.proprice.model.entities.relations.ReceiptComposition
import dz.samalgeria.proprice.utils.Constants
import dz.samalgeria.proprice.view.fragments.AddUpdateReceiptFragment

class AddCompositionRecAdapterRaw(
    private val fragment: AddUpdateReceiptFragment,
) :
    RecyclerView.Adapter<AddCompositionRecAdapterRaw.ViewHolder>() {
    private var listItems: List<ReceiptComposition> = mutableListOf()
    private var positionListItems = ArrayList<Boolean>()
    //private val TAG = "TAGGYAddRawAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AddCompositionRecAdapterLayoutRawBinding =
            AddCompositionRecAdapterLayoutRawBinding.inflate(
                LayoutInflater.from(fragment.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        with(holder) {
            tvName.text = item.rawName
            etQuantity.setText(Constants.display(item.rawQuantity))
            etQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(value: Editable?) {
                    try {
                        val quality = value.toString().toFloat()
                        if (listItems[position].rawQuantity != quality) {
                            listItems[position].rawQuantity = quality
                            fragment.updateAllValues()
                        }

                    } catch (e: Exception) {
                    }

                }
            })
            ibDeleteButton.setOnClickListener {
                fragment.deleteRawItem(position, item.receiptID.toInt())
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    fun updateItemList(list: List<ReceiptComposition>) {
        listItems = list
        positionListItems.clear()
        repeat(list.size) { positionListItems.add(false) }
        notifyDataSetChanged()
    }


    class ViewHolder(view: AddCompositionRecAdapterLayoutRawBinding) :
        RecyclerView.ViewHolder(view.root) {
        val tvName = view.tvIflRawName2
        val etQuantity = view.etInfRawQuantity2
        val ibDeleteButton = view.ibIflRawDelete
    }
}