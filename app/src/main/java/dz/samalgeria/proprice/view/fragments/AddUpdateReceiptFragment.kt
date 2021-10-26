package dz.samalgeria.proprice.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dz.samalgeria.proprice.R
import dz.samalgeria.proprice.application.ProPriceApplication
import dz.samalgeria.proprice.databinding.DialogCustomListBinding
import dz.samalgeria.proprice.databinding.FragmentAddUpdateReceiptBinding
import dz.samalgeria.proprice.model.entities.Raw
import dz.samalgeria.proprice.model.entities.Receipt
import dz.samalgeria.proprice.model.entities.relations.ReceiptComposition
import dz.samalgeria.proprice.utils.Constants
import dz.samalgeria.proprice.utils.Constants.display
import dz.samalgeria.proprice.view.activities.ProPriceActivity
import dz.samalgeria.proprice.view.adapters.AddCompositionRecAdapterRaw
import dz.samalgeria.proprice.view.adapters.CustomListItemAdapter
import dz.samalgeria.proprice.viewmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class AddUpdateReceiptFragment : Fragment(), View.OnClickListener {
    private val TAG = "AddUpdateReceiptFragment"
    private var isUpdateCase = false
    private var position = 0
    private var isCheckedList = ArrayList<Boolean>()
    private var weight = 0f
    private lateinit var customListItemAdapter: CustomListItemAdapter
    private lateinit var addCompositionRecAdapterRaw: AddCompositionRecAdapterRaw
    private lateinit var allRawList: ArrayList<Raw>
    private var compositionList = arrayListOf<ReceiptComposition>()
    private var _binding: FragmentAddUpdateReceiptBinding? = null
    private val binding get() = _binding!!
    private lateinit var mReceiptDetails: Receipt
    private lateinit var mCustomListDialog: Dialog

    private val receiptViewModel: ReceiptViewModel by viewModels {
        ReceiptViewModelFactory(((activity as ProPriceActivity).application as ProPriceApplication).receiptRepository)
    }
    private val rawViewModel: RawViewModel by viewModels {
        RawViewModelFactory(((activity as ProPriceActivity).application as ProPriceApplication).rawRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUpdateReceiptBinding.inflate(inflater, container, false)
        val view = binding.root
        getAllReceiptAndRawList()

        val args: AddUpdateReceiptFragmentArgs by navArgs()
        if (args.receipt != null) {
            mReceiptDetails = args.receipt!!
            binding.etReceiptName.setText(mReceiptDetails.receiptName)
            binding.bAddUpdateReceipt.text = resources.getString(R.string.lbl_update)
            setupRawListLayout(mReceiptDetails)
            isUpdateCase = true
        } else
            mReceiptDetails = Receipt(receiptID = -1L, receiptWeight = 0f, receiptName = "")
        setupActionBar()
        setupOnClickListeners()
        recycleViewSetUp()
        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_ifl_raw_name1 -> {
                if (allRawList.isEmpty()) return
                val list = allRawList.map { it.rawName }
                customItemsListDialog(
                    resources.getString(R.string.title_select_raw_name),
                    list
                )
            }
            R.id.b_add_update_receipt -> addUpdateReceipt()
            R.id.ib_ifl_raw_add -> addNewRawLayout()
        }
    }

    private fun setupOnClickListeners() {
        binding.etReceiptName.setOnClickListener(this)
        binding.bAddUpdateReceipt.setOnClickListener(this)
        binding.tvIflRawName1.setOnClickListener(this)
        binding.ibIflRawAdd.setOnClickListener(this)
    }

    private fun recycleViewSetUp() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        addCompositionRecAdapterRaw = AddCompositionRecAdapterRaw(this@AddUpdateReceiptFragment)
        binding.recyclerView.adapter = addCompositionRecAdapterRaw
    }

    private fun updateRecycleView() {
        binding.recyclerView.adapter = addCompositionRecAdapterRaw
        addCompositionRecAdapterRaw.updateItemList(compositionList)
        updateAllValues()
    }

    private fun setupRawListLayout(receipt: Receipt) {
        lifecycleScope.launch {
            compositionList = receiptViewModel.receiptCompositionList(receipt.receiptID)
            Log.d(TAG, "setupRawListLayout: list of composition$compositionList")
            delay(50)
            withContext(Dispatchers.Main) {
                for (i in 0 until compositionList.size) {
                    for (j in 0 until allRawList.size) {
                        if (allRawList[j].rawID == compositionList[i].rawID) {
                            compositionList[i].rawName = allRawList[j].rawName
                            compositionList[i].receiptID = j.toLong()
                            isCheckedList[j] = true
                            break
                        }
                    }
                }
                updateRecycleView()

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupActionBar() {
        (activity as ProPriceActivity).setSupportActionBar(binding.toolbar)
        (activity as ProPriceActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as ProPriceActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        if (isUpdateCase) {
            (activity as ProPriceActivity).supportActionBar?.let {
                (binding.toolbar.getChildAt(0) as TextView).text =
                    resources.getString(R.string.title_edit_receipt)
            }
        } else {
            (activity as ProPriceActivity).supportActionBar?.let {
                (binding.toolbar.getChildAt(0) as TextView).text =
                    resources.getString(R.string.title_add_receipt)
            }
        }

    }

    private fun addNewRawLayout() {
        val name = binding.tvIflRawName1.text.toString().trim { it <= ' ' }
        var quantity = binding.etInfRawQuantity1.text.toString().trim { it <= ' ' }
        if (quantity == ".") quantity = ""
        when {
            TextUtils.isEmpty(name) -> {
                Toast.makeText(
                    (activity as ProPriceActivity),
                    resources.getString(R.string.err_msg_select_raw),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(quantity) -> {
                Toast.makeText(
                    (activity as ProPriceActivity),
                    resources.getString(R.string.err_msg_enter_raw_quantity),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Constants.hideKeyboard(context, requireView().windowToken)
                val rawID = allRawList[position].rawID
                val composition =
                    ReceiptComposition(position.toLong(), rawID, quantity.toFloat(), name)
                updateAddRawQuantityAdapterLayout(composition)
            }
        }
    }

    private fun updateAddRawQuantityAdapterLayout(composition: ReceiptComposition) {
        compositionList.add(composition)
        updateRecycleView()
        deactivateRawCustomAdapterView(position)
        binding.tvIflRawName1.text = ""
        binding.etInfRawQuantity1.text.clear()

    }

    private fun addUpdateReceipt() {
        val name = binding.etReceiptName.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(name) -> {
                Toast.makeText(
                    (activity as ProPriceActivity),
                    resources.getString(R.string.err_msg_enter_receipt_name),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                mReceiptDetails.receiptName = name.toUpperCase()
                mReceiptDetails.receiptWeight = display(weight).toFloat()

                if (mReceiptDetails.receiptID == -1L)
                    insertReceipt(mReceiptDetails)
                else
                    updateReceipt(mReceiptDetails)

                Constants.hideKeyboard(context, requireView().windowToken)
                findNavController().popBackStack()
            }
        }
    }

    private fun insertReceipt(receipt: Receipt) {
        lifecycleScope.launch(Dispatchers.IO) {
            receipt.receiptID = 0
            val receiptID = receiptViewModel.insertReceipt(receipt)
            compositionList.forEach { it.receiptID = receiptID }
            receiptViewModel.insertReceiptCompositionList(compositionList)
        }
        Toast.makeText(
            (activity as ProPriceActivity),
            resources.getString(R.string.add_new_receipt_msg),
            Toast.LENGTH_SHORT
        ).show()

        // You even print the log if Toast is not displayed on emulator
        Log.e("Insertion", "Success")
    }

    private fun updateReceipt(receipt: Receipt) {
        lifecycleScope.launch(Dispatchers.IO) {
            receiptViewModel.updateReceipt(receipt)
            compositionList.forEach { it.receiptID = receipt.receiptID }
            Log.d(TAG, "updateReceipt: xxx${receipt.receiptID}")
            receiptViewModel.updateReceiptComposition(
                receipt.receiptID,
                compositionList as List<ReceiptComposition>
            )
        }
        Toast.makeText(
            (activity as ProPriceActivity),
            resources.getString(R.string.update_receipt_msg),
            Toast.LENGTH_SHORT
        ).show()
        Log.e("Updating", "Success")
    }

    private fun customItemsListDialog(title: String, itemsList: List<String>) {
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        customListItemAdapter = CustomListItemAdapter(
            requireActivity(),
            this@AddUpdateReceiptFragment,
            itemsList
        )
        customListItemAdapter.isCheckedList = isCheckedList
        binding.rvList.adapter = customListItemAdapter
        mCustomListDialog.show()
    }

    fun selectedListItem(position: Int) {
        mCustomListDialog.dismiss()
        binding.tvIflRawName1.text = allRawList[position].rawName
        this.position = position

    }

    private fun getAllReceiptAndRawList() {
        lifecycleScope.launch {
            allRawList = rawViewModel.allRawList() as ArrayList<Raw>
            allRawList.forEach {
                it.rawName =
                    it.rawName + "(" + it.rawPrice + resources.getString(R.string.dz) + ")"
            }

            repeat(allRawList.size) { isCheckedList.add(false) }
        }
    }

    private fun deactivateRawCustomAdapterView(position: Int) {
        Log.d(TAG, "deactivateView: $position")
        isCheckedList[position] = true
    }

    private fun activateRawCustomAdapterView(position: Int) {
        isCheckedList[position] = false
    }

    fun deleteRawItem(position: Int, originalPosition: Int) {
        activateRawCustomAdapterView(originalPosition)
        compositionList.removeAt(position)
        updateRecycleView()
    }

    fun updateAllValues() {
        var totalPrice = 0f
        var price = 0f
        weight = 0f
        if (compositionList.size != 0) {
            compositionList.forEach {
                totalPrice += allRawList[it.receiptID.toInt()].rawPrice * it.rawQuantity
                weight += it.rawQuantity
            }
            price = totalPrice / weight
        }

        binding.tvWeight.text = display(weight)
        binding.tvPrice.text = display(price)
        binding.tvTotalPrice.text = display(totalPrice)
    }
}

