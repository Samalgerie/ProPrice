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
import dz.samalgeria.proprice.databinding.FragmentAddUpdateProductBinding
import dz.samalgeria.proprice.model.entities.Extra
import dz.samalgeria.proprice.model.entities.Product
import dz.samalgeria.proprice.model.entities.Receipt
import dz.samalgeria.proprice.model.entities.relations.ProductComposition
import dz.samalgeria.proprice.utils.Constants
import dz.samalgeria.proprice.utils.Constants.display
import dz.samalgeria.proprice.view.activities.ProPriceActivity
import dz.samalgeria.proprice.view.adapters.AddCompositionRecAdapterExtra
import dz.samalgeria.proprice.view.adapters.CustomListItemAdapter
import dz.samalgeria.proprice.viewmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class AddUpdateProductFragment : Fragment(), View.OnClickListener {
    private var isUpdateCase = false
    private var extraPosition = 0
    private var receiptPosition = 0
    private var isCheckedList = ArrayList<Boolean>()
    private lateinit var customListItemAdapter: CustomListItemAdapter
    private lateinit var addCompositionRecAdapterExtra: AddCompositionRecAdapterExtra
    private val TAG = "TAGGYAddUpdateProduct"
    private lateinit var allExtraList: ArrayList<Extra>
    private var allReceiptList = arrayListOf<Receipt>()
    private var compositionList = arrayListOf<ProductComposition>()
    private var _binding: FragmentAddUpdateProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var mProductDetails: Product
    private lateinit var mCustomListDialog: Dialog


    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(((activity as ProPriceActivity).application as ProPriceApplication).productRepository)
    }
    private val receiptViewModel: ReceiptViewModel by viewModels {
        ReceiptViewModelFactory(((activity as ProPriceActivity).application as ProPriceApplication).receiptRepository)
    }
    private val extraViewModel: ExtraViewModel by viewModels {
        ExtraViewModelFactory(((activity as ProPriceActivity).application as ProPriceApplication).extraRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUpdateProductBinding.inflate(inflater, container, false)
        val view = binding.root

        lifecycleScope.launch {
            getAllExtraAndReceiptList()
            val args: AddUpdateProductFragmentArgs by navArgs()
            if (args.product != null) {
                mProductDetails = args.product!!
                binding.etProductName.setText(mProductDetails.productName)
                binding.etProductDiscount.setText(display(mProductDetails.productDiscount))
                binding.etProductWeight.setText(display(mProductDetails.productWeight))
                binding.etSelectReceiptName.setText(getReceiptName(mProductDetails.receiptID))
                binding.bAddUpdateProduct.text = resources.getString(R.string.lbl_update)
                setupExtraListLayout(mProductDetails)
                isUpdateCase = true

            } else
                mProductDetails = Product(
                    productID = -1L,
                    receiptID = -1L,
                    productDiscount = 0f,
                    productName = "",
                    productWeight = 0f
                )
            setupActionBar()
        }
        recycleViewSetUp()
        setupOnClickListeners()


        return view
    }

    private fun getReceiptName(receiptID: Long?): String {
        var name = ""
        var position = 0
        if (receiptID == null) return name
        allReceiptList.forEach {
            if (it.receiptID == receiptID) {
                name = it.receiptName
                receiptPosition = position
                return@forEach
            }
            position++
        }
        return name
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_ifl_extra_name1 -> {
                if (allExtraList.isEmpty()) return
                val list = allExtraList.map { it.extraName }
                customItemsListDialog(
                    resources.getString(R.string.title_select_extra_name),
                    list,
                    1
                )
            }
            R.id.et_select_receipt_name -> {
                if (allReceiptList.isEmpty()) return
                val list = allReceiptList.map { it.receiptName }
                customItemsListDialog(
                    resources.getString(R.string.title_select_receipt_name),
                    list,
                    2
                )
            }
            R.id.b_add_update_product -> addUpdateProduct()
            R.id.ib_ifl_extra_add -> addNewExtraLayout()
        }
    }

    private fun setupOnClickListeners() {
        binding.etProductName.setOnClickListener(this)
        binding.etProductWeight.setOnClickListener(this)
        binding.etSelectReceiptName.setOnClickListener(this)
        binding.bAddUpdateProduct.setOnClickListener(this)
        binding.tvIflExtraName1.setOnClickListener(this)
        binding.ibIflExtraAdd.setOnClickListener(this)
    }

    private fun recycleViewSetUp() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        addCompositionRecAdapterExtra = AddCompositionRecAdapterExtra(this@AddUpdateProductFragment)
        binding.recyclerView.adapter = addCompositionRecAdapterExtra
    }

    private fun setupExtraListLayout(product: Product) {
        lifecycleScope.launch {
            compositionList =
                productViewModel.productCompositionList(product.productID) as ArrayList<ProductComposition>
            Log.d(TAG, "setupExtraListLayout: list of composition$compositionList")
            delay(50)
            withContext(Dispatchers.Main) {
                for (i in 0 until compositionList.size) {
                    for (j in 0 until allExtraList.size) {
                        if (allExtraList[j].extraID == compositionList[i].extraID) {
                            compositionList[i].extraName = allExtraList[j].extraName
                            compositionList[i].productID = j.toLong()
                            isCheckedList[j] = true
                            break
                        }
                    }
                }
                addCompositionRecAdapterExtra.updateItemList(compositionList)
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
                    resources.getString(R.string.title_edit_product)
            }
        } else {
            (activity as ProPriceActivity).supportActionBar?.let {
                (binding.toolbar.getChildAt(0) as TextView).text =
                    resources.getString(R.string.title_add_product)
            }
        }

        //supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
    }

    private fun addNewExtraLayout() {
        val name = binding.tvIflExtraName1.text.toString().trim { it <= ' ' }
        var quantity = binding.etInfExtraQuantity1.text.toString().trim { it <= ' ' }
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
                    resources.getString(R.string.err_msg_enter_extra_quantity),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Constants.hideKeyboard(context, requireView().windowToken)
                val extraID = allExtraList[extraPosition].extraID
                val composition =
                    ProductComposition(extraPosition.toLong(), extraID, quantity.toFloat(), name)
                updateAddExtraQuantityAdapterLayout(composition)
            }
        }
    }

    private fun updateAddExtraQuantityAdapterLayout(composition: ProductComposition) {
        compositionList.add(composition)
        addCompositionRecAdapterExtra.updateItemList(compositionList)
        deactivateExtraCustomAdapterView(extraPosition)
        binding.tvIflExtraName1.text = ""
        binding.etInfExtraQuantity1.text.clear()

    }

    private fun addUpdateProduct() {
        val name = binding.etProductName.text.toString().trim { it <= ' ' }
        var weight = binding.etProductWeight.text.toString().trim { it <= ' ' }
        if (weight == ".") weight = ""
        var discount = binding.etProductDiscount.text.toString().trim { it <= ' ' }
        if (discount == ".") discount = ""
        val receipt = binding.etSelectReceiptName.text.toString().trim { it <= ' ' }

        when {
            TextUtils.isEmpty(name) -> {
                Toast.makeText(
                    (activity as ProPriceActivity),
                    resources.getString(R.string.err_msg_enter_pro_name),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(weight) -> {
                Toast.makeText(
                    (activity as ProPriceActivity),
                    resources.getString(R.string.err_msg_enter_pro_weight),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(discount) || (discount.toFloat() < 0f) || (discount.toFloat() > 100f) -> {
                Toast.makeText(
                    (activity as ProPriceActivity),
                    resources.getString(R.string.err_msg_enter_pro_discount),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(receipt) -> {
                Toast.makeText(
                    (activity as ProPriceActivity),
                    resources.getString(R.string.err_msg_select_receipt_name),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                mProductDetails.productName = name.toUpperCase()
                mProductDetails.receiptID = allReceiptList[receiptPosition].receiptID
                mProductDetails.productWeight = display(weight.toFloat()).toFloat()
                mProductDetails.productDiscount = display(discount.toFloat()).toFloat()

                if (mProductDetails.productID == -1L)
                    insertProduct(mProductDetails)
                else
                    updateProduct(mProductDetails)

                Constants.hideKeyboard(context, requireView().windowToken)
                findNavController().popBackStack()
            }
        }
    }

    private fun insertProduct(proDetails: Product) {
        lifecycleScope.launch(Dispatchers.IO) {
            proDetails.productID = 0L
            val productID = productViewModel.insertProduct(proDetails)
            compositionList.forEach { it.productID = productID }
            productViewModel.insertProductCompositionList(compositionList)
        }
        Toast.makeText(
            (activity as ProPriceActivity),
            resources.getString(R.string.add_new_product_msg),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateProduct(proDetails: Product) {
        lifecycleScope.launch(Dispatchers.IO) {
            productViewModel.updateProduct(proDetails)
            compositionList.forEach { it.productID = proDetails.productID }
            productViewModel.updateProductComposition(proDetails.productID, compositionList)
        }
        Toast.makeText(
            (activity as ProPriceActivity),
            resources.getString(R.string.update_product_msg),
            Toast.LENGTH_SHORT
        ).show()
        Log.e("Updating", "Success")
    }

    private fun customItemsListDialog(title: String, itemsList: List<String>, type: Int) {
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding =
            DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        customListItemAdapter = CustomListItemAdapter(
            requireActivity(),
            this@AddUpdateProductFragment,
            itemsList
        )
        customListItemAdapter.isCheckedList = isCheckedList
        customListItemAdapter.type = type
        binding.rvList.adapter = customListItemAdapter
        mCustomListDialog.show()
    }

    fun selectedListItem(position: Int, type: Int) {
        mCustomListDialog.dismiss()
        if (type == 2) {
            binding.etSelectReceiptName.setText(allReceiptList[position].receiptName)
            this.receiptPosition = position
        } else if (type == 1) {
            binding.tvIflExtraName1.text = allExtraList[position].extraName
            this.extraPosition = position
        }
    }

    private suspend fun getAllExtraAndReceiptList() {

        allReceiptList = receiptViewModel.allReceiptList() as ArrayList<Receipt>
        allExtraList = extraViewModel.allExtraList() as ArrayList<Extra>
        allExtraList.forEach {
            it.extraName =
                it.extraName + "(" + it.extraPrice + resources.getString(R.string.dz0) + it.extraUnit + ")"
        }
        allReceiptList.forEach {
            it.receiptName =
                it.receiptName + "(" + it.receiptWeight + resources.getString(R.string.kg) + ")"
        }

        repeat(allExtraList.size) { isCheckedList.add(false) }

    }

    private fun deactivateExtraCustomAdapterView(position: Int) {
        Log.d(TAG, "deactivateView: $position")
        isCheckedList[position] = true
    }

    private fun activateExtraCustomAdapterView(position: Int) {
        isCheckedList[position] = false
    }

    fun deleteExtraItem(position: Int, originalPosition: Int) {
        activateExtraCustomAdapterView(originalPosition)
        compositionList.removeAt(position)
        addCompositionRecAdapterExtra.updateItemList(compositionList)
    }

    fun updateAllValues() {

    }
}




