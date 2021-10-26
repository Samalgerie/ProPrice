package dz.samalgeria.proprice.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.akaita.android.circularseekbar.CircularSeekBar
import com.akaita.android.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import dz.samalgeria.proprice.R
import dz.samalgeria.proprice.application.ProPriceApplication
import dz.samalgeria.proprice.databinding.DialogCustomListBinding
import dz.samalgeria.proprice.databinding.FragmentCalculatorBinding
import dz.samalgeria.proprice.model.entities.Product
import dz.samalgeria.proprice.model.entities.relations.ProductCompositionWithExtra
import dz.samalgeria.proprice.model.entities.relations.ReceiptCompositionWithRaw
import dz.samalgeria.proprice.utils.Constants
import dz.samalgeria.proprice.utils.Constants.display
import dz.samalgeria.proprice.utils.Constants.setBottomNavigationBar
import dz.samalgeria.proprice.view.activities.ProPriceActivity
import dz.samalgeria.proprice.view.adapters.CustomListItemAdapter
import dz.samalgeria.proprice.viewmodel.*
import kotlinx.coroutines.launch


class CalculatorFragment : Fragment(), View.OnClickListener {
    //private val TAG = "TAGGYCalculator"
    private var receiptCost = 0f
    private var productionCost = 0f
    private var currentDiscount = 0f
    private var currentProfitRatio = 0f
    private var discount = 0f
    private var profitRatio = 0f
    private var position = 0
    private var _binding: FragmentCalculatorBinding? = null
    private lateinit var customListItemAdapter: CustomListItemAdapter
    private lateinit var mCustomListDialog: Dialog
    private lateinit var allProductList: ArrayList<Product>
    private lateinit var allReceiptCompositionWithRawList: ArrayList<ReceiptCompositionWithRaw>
    private lateinit var allProduceCompositionWithExtraList: ArrayList<ProductCompositionWithExtra>
    private var mProductDetails: Product? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(((activity as ProPriceActivity).application as ProPriceApplication).productRepository)
    }
    private val receiptViewModel: ReceiptViewModel by viewModels {
        ReceiptViewModelFactory(((activity as ProPriceActivity).application as ProPriceApplication).receiptRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        val view = binding.root
        setUpUIWithSharedPerfValues()
        lifecycleScope.launch {
            getAllProductList()
            val args: CalculatorFragmentArgs by navArgs()
            args.product?.let {
                mProductDetails = args.product
                binding.tvSelectProductName.text = it.productName
                updateUIWithNewProduct(mProductDetails!!)
            }
            if (mProductDetails == null) {
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                val proID = sharedPref?.getLong("proID", -1L)!!
                if (proID != -1L) {
                    mProductDetails = findProductByID(proID)
                    if (mProductDetails != null && mProductDetails!!.receiptID != null) {
                        binding.tvSelectProductName.text = mProductDetails!!.productName
                        updateUIWithNewProduct(mProductDetails!!)
                    }
                }
            }
        }
        setOnClickListeners()
        return view
    }

    private fun findProductByID(proID: Long): Product? {
        var product: Product? = null
        allProductList.forEach {
            if (it.productID == proID) {
                product = it
            }
        }
        return product

    }

    private fun setUpUIWithSharedPerfValues() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val applyAnimation = sharedPref?.getInt("applyAnimation", 2)!!
        if (applyAnimation == 1) {
            with(sharedPref.edit()) {
                putInt("applyAnimation", 2)
                apply()
            }
            setUISettings1()
        } else setUISettings2()

        profitRatio = sharedPref.getFloat("profitRatio", 0f)
        productionCost = sharedPref.getFloat("productionCost", 0f)
        currentProfitRatio = profitRatio
        with(binding) {
            if (productionCost != 0f)
                etProductionCosts.setText(display(productionCost))
            sbProfitRatio.progress = profitRatio
            sbProfitRatio.setOnCircularSeekBarChangeListener(object :
                OnCircularSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: CircularSeekBar?,
                    progress: Float,
                    fromUser: Boolean
                ) {
                    if (mProductDetails == null) return
                    currentProfitRatio = display(progress).toFloat()
                    sbProfitRatio.progressTextColor =
                        if (display(currentProfitRatio) != display(profitRatio))
                            ContextCompat.getColor(activity as ProPriceActivity, R.color.grey_500)
                        else
                            ContextCompat.getColor(
                                activity as ProPriceActivity,
                                R.color.profit_ratio
                            )
                    displayCalculatedValue()
                }

                override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
                override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}

            })

            sbProfitRatio.setOnCenterClickedListener { _, progress ->
                if (mProductDetails == null) return@setOnCenterClickedListener
                with(sharedPref.edit()) {
                    putFloat("profitRatio", display(progress).toFloat())
                    apply()
                }
                profitRatio = currentProfitRatio
                sbProfitRatio.progressTextColor =
                    ContextCompat.getColor(activity as ProPriceActivity, R.color.profit_ratio)
            }

            sbDiscount.setOnCircularSeekBarChangeListener(object : OnCircularSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: CircularSeekBar?,
                    progress: Float,
                    fromUser: Boolean
                ) {
                    if (mProductDetails == null) return
                    currentDiscount = display(progress).toFloat()
                    sbDiscount.progressTextColor =
                        if (display(currentDiscount) != display(discount))
                            ContextCompat.getColor(activity as ProPriceActivity, R.color.grey_500)
                        else
                            ContextCompat.getColor(activity as ProPriceActivity, R.color.discount)

                    displayCalculatedValue()
                }


                override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
                override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}

            })

            sbDiscount.setOnCenterClickedListener { _, _ ->
                when {
                    mProductDetails != null -> {
                        updateProductDiscount()
                        sbDiscount.progressTextColor =
                            ContextCompat.getColor(activity as ProPriceActivity, R.color.discount)
                    }
                }
            }

            etProductionCosts.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(value: Editable?) {
                    try {
                        productionCost = display(value.toString().toFloat()).toFloat()
                        with(sharedPref.edit()) {
                            putFloat("productionCost", productionCost)
                            apply()
                        }
                        if (mProductDetails != null)
                            displayCalculatedValue()
                    } catch (e: Exception) {
                    }
                }
            })


        }
    }

    override fun onClick(view: View?) {
        if (allProductList.isEmpty()) return
        val list = allProductList.map { it.productName }
        customItemsListDialog(
            resources.getString(R.string.title_select_product_name),
            list
        )
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (requireView().windowToken != null)
            Constants.hideKeyboard(context, requireView().windowToken)
    }

    private fun setOnClickListeners() {
        binding.tvSelectProductName
            .setOnClickListener(this)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUISettings1() {
        hideStateBar()
        val startAnimationBackImage =
            AnimationUtils.loadAnimation(activity as ProPriceActivity, R.anim.anim_start_background)
        val startAnimationAppName =
            AnimationUtils.loadAnimation(activity as ProPriceActivity, R.anim.anim_start_app_name)
        val startAnimationAppName2 = AnimationUtils.loadAnimation(
            activity as ProPriceActivity,
            R.anim.anim_start_app_top_name
        )
        val startAnimationMain =
            AnimationUtils.loadAnimation(activity as ProPriceActivity, R.anim.anim_start_menu)

        binding.llAppName.animation = startAnimationAppName
        binding.backLL.animation = startAnimationAppName2
        binding.ivBackImage.animation = startAnimationBackImage

        startAnimationBackImage.setAnimationListener(object : Animation.AnimationListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onAnimationStart(p0: Animation?) {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.clCalculatorMainLayout.animation = startAnimationMain
                    binding.clCalculatorMainLayout.visibility = View.VISIBLE
                    setBottomNavigationBar(activity as ProPriceActivity, 0)
                    (activity as ProPriceActivity)
                        .findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.VISIBLE

                }, 1500)
            }

            override fun onAnimationEnd(p0: Animation?) {
                setUpStateBar()
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })
    }

    private fun setUISettings2() {
        binding.clCalculatorMainLayout.visibility = View.VISIBLE
        binding.ivBackImage.visibility = View.GONE
        binding.ivSmallBackImage.visibility = View.VISIBLE
        binding.llAppName.visibility = View.GONE
        setBottomNavigationBar(activity as ProPriceActivity, 0)
        Constants.setStateBar(activity as ProPriceActivity, 0)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpStateBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (activity as ProPriceActivity).window.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            (activity as ProPriceActivity).window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        Constants.setStateBar(activity as ProPriceActivity, 0)
    }

    private fun hideStateBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (activity as ProPriceActivity).window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            (activity as ProPriceActivity).window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun customItemsListDialog(title: String, itemsList: List<String>) {
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        customListItemAdapter = CustomListItemAdapter(
            requireActivity(),
            this@CalculatorFragment,
            itemsList
        )
        binding.rvList.adapter = customListItemAdapter
        mCustomListDialog.show()
    }

    fun selectedListItem(position: Int) {
        mCustomListDialog.dismiss()
        binding.tvSelectProductName.text = allProductList[position].productName
        this.position = position
        mProductDetails = allProductList[position]
        updateUIWithNewProduct(mProductDetails!!)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref!!.edit()) {
            putLong("proID", mProductDetails!!.productID)
            apply()
        }
    }

    private fun updateUIWithNewProduct(product: Product) {
        setSBDiscountUI(product.productDiscount)
        lifecycleScope.launch {
            getItemsList()
            updateAllValues()
            displayCalculatedValue()
        }

    }

    private fun setSBDiscountUI(discount: Float) {
        this.discount = discount
        currentDiscount = discount
        binding.sbDiscount.progress = discount
        binding.sbProfitRatio.progress = profitRatio
        binding.sbDiscount.progressTextColor =
            ContextCompat.getColor(activity as ProPriceActivity, R.color.discount)

        binding.sbProfitRatio.progressTextColor =
            ContextCompat.getColor(activity as ProPriceActivity, R.color.profit_ratio)

        binding.llSeekbarNames.visibility = View.GONE

    }

    private fun updateProductDiscount() {
        mProductDetails!!.productDiscount = currentDiscount
        productViewModel.updateProduct(mProductDetails!!)
    }

    private fun updateAllValues() {
        if (mProductDetails == null) return
        receiptCost = 0f
        var weight = 0f
        allReceiptCompositionWithRawList.forEach {
            receiptCost += it.receiptComposition.rawQuantity * it.rawDetails.rawPrice
            weight += it.receiptComposition.rawQuantity
        }
        receiptCost = if (weight == 0f) 0f
        else (receiptCost * mProductDetails!!.productWeight) / weight
        allProduceCompositionWithExtraList.forEach {
            receiptCost += it.productComposition.extraQuantity * it.extraDetails.extraPrice
        }
    }

    private fun displayCalculatedValue() {
        //  lifecycleScope.launch {
        //CHECK...
        var cost = productionCost * mProductDetails!!.productWeight
        binding.tvCalculateReceipt.text = display(receiptCost)
        binding.tvCalculateProductionCost.text = display(cost)
        cost += receiptCost
        binding.tvCalculateReceiptWithProductionCost.text = display(cost)
        val price =
            (10000 * cost) / (10000 - 100 * (currentProfitRatio + currentDiscount) + currentProfitRatio * currentDiscount)
        binding.tvCalculatePrice.text = display(price)
        val discount = price * currentDiscount / 100
        binding.tvCalculateDiscount.text = display(discount)
        val profit = (price - discount) * currentProfitRatio / 100
        binding.tvCalculateProfit.text = display(profit)
        //  }
    }

    private suspend fun getAllProductList() {
        allProductList = productViewModel.allProductList() as ArrayList<Product>
        allProductList.forEach {
            if (it.receiptID == null) it.productName = "*" + it.productName
        }
    }

    private suspend fun getItemsList() {
        val productID = mProductDetails!!.productID
        val receiptID = mProductDetails!!.receiptID
        allProduceCompositionWithExtraList =
            productViewModel.allProductCompositionWithExtraDetails(productID) as ArrayList<ProductCompositionWithExtra>
        allReceiptCompositionWithRawList =
            receiptViewModel.allReceiptCompositionWithExtraDetails(receiptID!!) as ArrayList<ReceiptCompositionWithRaw>

        //  Log.d(TAG, "getItemsList: $allProduceCompositionWithExtraList")
        //  Log.d(TAG, "getItemsList: $allReceiptCompositionWithRawList")
    }


}