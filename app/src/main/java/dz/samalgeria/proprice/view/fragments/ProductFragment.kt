package dz.samalgeria.proprice.view.fragments


//import android.util.Log

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dz.samalgeria.proprice.R
import dz.samalgeria.proprice.application.ProPriceApplication
import dz.samalgeria.proprice.databinding.FragmentProductBinding
import dz.samalgeria.proprice.model.entities.Product
import dz.samalgeria.proprice.utils.Constants
import dz.samalgeria.proprice.view.activities.ProPriceActivity
import dz.samalgeria.proprice.view.adapters.ProductRecAdapter
import dz.samalgeria.proprice.viewmodel.ProductViewModel
import dz.samalgeria.proprice.viewmodel.ProductViewModelFactory
import kotlinx.coroutines.*
import java.util.*


class ProductFragment : Fragment(), View.OnClickListener {

    // private var update = true
    //private val TAG = "TAGGYProductFragment"
    private lateinit var list: MutableList<Product>
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductRecAdapter
    private var count = 0


    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((requireActivity().application as ProPriceApplication).productRepository)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        val view = binding.root
        setHasOptionsMenu(true)
        setUpBars()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*        lifecycleScope.launch {
            productViewModel.insertProduct(Product(0, null, "AAAA", 502f, 50f))
            productViewModel.insertProduct(Product(0, null, "BBBB", 2f, 50f))
            productViewModel.insertProduct(Product(0, null, "CCCC", 428f, 50f))
            productViewModel.insertProduct(Product(0, null, "DDDD", 5.8f, 50f))
            productViewModel.insertProduct(Product(0, null, "EEEE", 33.6f, 50f))
            productViewModel.insertProduct(Product(0, null, "FFFF", 82f, 50f))
            productViewModel.insertProduct(Product(0, null, "GGGG", 52.8f, 50f))
        }*/
        binding.cbCheckAllProduct.isChecked = false
        setOnClickListeners()
        recycleViewSetUp()
        setupOnBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.fabAddRowProduct -> {
                NavHostFragment.findNavController(this)
                    .navigate(
                        ProductFragmentDirections.actionNavigationProductToAddProductFragment(
                            null
                        )
                    )
            }
            binding.cbCheckAllProduct -> {
                setupCheckAllProduct()


            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_menu, menu)
        val search = menu.findItem(R.id.search)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = false
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //Log.d(TAG, "onQueryTextSubmit: $newText")
                adapter.filter.filter(newText)
                return true
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                val listToDelete: MutableList<Product> = mutableListOf()
                //Log.d(TAG, "adapter.isCheckBoxList.size: " + adapter.isCheckBoxList.size)
                for (i in adapter.isCheckBoxList.indices) {
                    if (adapter.isCheckBoxList[i]) {
                        listToDelete.add(adapter.filteredProductList[i])
                    }
                }
                //Log.d(TAG, "list.size: " + list.size)
                //Log.d(TAG, "listToDelete.size: " + listToDelete.size)
                if (listToDelete.size > 0)
                    deleteProduct(listToDelete)

            }
            R.id.search -> {
                //(activity as WeMixActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)


            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.cbCheckAllProduct.visibility == View.VISIBLE) {
                        setupUIOriginalState()
                        //  requireActivity().onBackPressed()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
            )
    }

    private fun setupUIOriginalState() {
        binding.fabAddRowProduct.visibility = View.VISIBLE
        binding.actionBarTitle.text = resources.getString(R.string.product)
        binding.toolbar.menu.findItem(R.id.delete).setShowAsAction(0)
        binding.toolbar.menu.findItem(R.id.delete).isVisible = false
        binding.toolbar.menu.findItem(R.id.search).setShowAsAction(1)
        binding.toolbar.menu.findItem(R.id.search).isVisible = true
        val search = binding.toolbar.menu.findItem(R.id.search)
        val searchView = search?.actionView as? SearchView
        searchView?.isFocusable = false
        binding.cbCheckAllProduct.visibility = View.GONE
        binding.cbCheckAllProduct.isChecked = false
        adapter.isVisible = false
        adapter.isCheckedAll = false
        for (v in adapter.allCheckBoxList) {
            v.visibility = View.GONE
            v.isChecked = false
        }
        (activity as ProPriceActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        count = 0
        searchView?.setQuery("", true)

        searchView?.onActionViewCollapsed()
    }

    private fun recycleViewSetUp() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductRecAdapter(this@ProductFragment)
        binding.recyclerView.adapter = adapter
        productViewModel.allProductList.observe(viewLifecycleOwner) {
            it.let {
                //Log.d(TAG, "recycleViewSetUp: " + it.size)
                if (it.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.tvNoProductAdded.visibility = View.GONE
                    adapter.allProductList(it)
                    list = it as MutableList<Product>
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.tvNoProductAdded.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.fabAddRowProduct.setOnClickListener(this)
        binding.cbCheckAllProduct.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpBars() {
        Constants.setBottomNavigationBar(activity as ProPriceActivity, 4)
        Constants.setStateBar(activity as ProPriceActivity, 4)
        (activity as ProPriceActivity).setSupportActionBar(binding.toolbar)
        (activity as ProPriceActivity).supportActionBar?.title = ""
        (activity as ProPriceActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as ProPriceActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
    }

    fun showAllCheckBox() {
        binding.fabAddRowProduct.visibility = View.GONE
        binding.actionBarTitle.text = "0"
        binding.cbCheckAllProduct.visibility = View.VISIBLE
        (activity as ProPriceActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.toolbar.menu.findItem(R.id.delete).setShowAsAction(1)
        binding.toolbar.menu.findItem(R.id.delete).isVisible = true
        binding.toolbar.menu.findItem(R.id.search).setShowAsAction(0)
        binding.toolbar.menu.findItem(R.id.search).isVisible = false
    }

    fun decreaseCounter() {
        count--
        binding.cbCheckAllProduct.isChecked = false
        binding.actionBarTitle.text = count.toString()
    }

    fun increaseCounter() {
        count++
        // Log.d(TAG, "increaseCounter: count $count")
        binding.actionBarTitle.text = count.toString()
        if (count == adapter.itemCount)
            binding.cbCheckAllProduct.isChecked = true
    }

    private fun setupCheckAllProduct() {
        if (binding.cbCheckAllProduct.isChecked) {
            adapter.isCheckedAll = true
            for (v in adapter.allCheckBoxList) {
                v.isChecked = true
            }
            for (i in adapter.isCheckBoxList.indices) {
                adapter.isCheckBoxList[i] = true
            }
            count = adapter.itemCount
            binding.actionBarTitle.text = count.toString()
        } else {
            adapter.isCheckedAll = false
            for (v in adapter.allCheckBoxList) {
                v.isChecked = false
            }
            for (i in adapter.isCheckBoxList.indices) {
                adapter.isCheckBoxList[i] = false
            }
            count = 0
            binding.actionBarTitle.text = "0"
        }
    }

    fun editProduct(product: Product) {
        NavHostFragment.findNavController(this)
            .navigate(ProductFragmentDirections.actionNavigationProductToAddProductFragment(product))
    }

    private fun deleteProduct(listToDelete: MutableList<Product>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                when (count) {
                    list.size ->
                        productViewModel.deleteAllProduct()
                    else -> {
                        productViewModel.deleteProduct(listToDelete)
                    }
                }
                withContext(Dispatchers.Main) {
                    delay(5)
                    setupUIOriginalState()
                }
            }

            Toast
                .makeText(
                    requireContext(),
                    resources.getString(R.string.items_are_removed),
                    Toast.LENGTH_SHORT
                ).show()

            //   findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        //Log.d(TAG, "deleteProduct: count $count")
        //Log.d(TAG, "deleteProduct: list xxx$listToDelete")
        val text = when (count) {
            list.size -> resources.getString(R.string.delete_all_items_msg)
            1 -> {
                listToDelete[0].productName + " " + resources.getString(R.string.delete_one_item_msg)
            }
            else -> count.toString() + " " + resources.getString(R.string.delete_many_items_msg)
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        builder.setTitle(resources.getString(R.string.delete))
        builder.setMessage(text)
        builder.create().show()
    }

    fun calculator(product: Product) {
        findNavController()
            .navigate(
                ProductFragmentDirections.actionNavigationProductToNavigationCalculator(
                    product
                )
            )
    }
}



