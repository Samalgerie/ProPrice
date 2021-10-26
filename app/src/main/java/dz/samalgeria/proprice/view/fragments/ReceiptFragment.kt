package dz.samalgeria.proprice.view.fragments


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
import dz.samalgeria.proprice.databinding.FragmentReceiptBinding
import dz.samalgeria.proprice.model.entities.Receipt
import dz.samalgeria.proprice.utils.Constants
import dz.samalgeria.proprice.view.activities.ProPriceActivity
import dz.samalgeria.proprice.view.adapters.ReceiptRecAdapter
import dz.samalgeria.proprice.viewmodel.ReceiptViewModel
import dz.samalgeria.proprice.viewmodel.ReceiptViewModelFactory
import kotlinx.coroutines.*
import java.util.*


class ReceiptFragment : Fragment(), View.OnClickListener {

    //private val TAG = "TAGGYReceiptFragment"
    private lateinit var list: MutableList<Receipt>
    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ReceiptRecAdapter
    private var count = 0


    private val receiptViewModel: ReceiptViewModel by viewModels {
        ReceiptViewModelFactory((requireActivity().application as ProPriceApplication).receiptRepository)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)
        val view = binding.root
        setUpBars()
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*        lifecycleScope.launch {
            receiptViewModel.insertReceipt(Receipt(0, "AAA", 500f))
            receiptViewModel.insertReceipt(Receipt(0, "BBB", 2f))
            receiptViewModel.insertReceipt(Receipt(0, "CCC", 428f))
            receiptViewModel.insertReceipt(Receipt(0, "DDD", 50.8f))
            receiptViewModel.insertReceipt(Receipt(0, "EEE", 33f))
            receiptViewModel.insertReceipt(Receipt(0, "FFF", 82f))
            receiptViewModel.insertReceipt(Receipt(0, "GGG", 59.99f))

        }*/

        binding.cbCheckAllReceipt.isChecked = false
        setOnClickListeners()
        recycleViewSetUp()
        setupOnBackPressed()
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
                adapter.filter.filter(newText)
                return true
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                val listToDelete: MutableList<Receipt> = mutableListOf()
                for (i in adapter.isCheckBoxList.indices) {
                    if (adapter.isCheckBoxList[i]) {
                        listToDelete.add(adapter.filteredReceiptList[i])
                    }
                }
                if (listToDelete.size > 0)
                    deleteReceipt(listToDelete)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.cbCheckAllReceipt.visibility == View.VISIBLE) {
                        setupUIOriginalState()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
            )
    }

    private fun setupUIOriginalState() {
        binding.fabAddRowReceipt.visibility = View.VISIBLE
        binding.actionBarTitle.text = resources.getString(R.string.receipt)
        binding.toolbar.menu.findItem(R.id.delete).setShowAsAction(0)
        binding.toolbar.menu.findItem(R.id.delete).isVisible = false
        binding.toolbar.menu.findItem(R.id.search).setShowAsAction(1)
        binding.toolbar.menu.findItem(R.id.search).isVisible = true
        val search = binding.toolbar.menu.findItem(R.id.search)
        val searchView = search?.actionView as? SearchView
        searchView?.isFocusable = false
        binding.cbCheckAllReceipt.visibility = View.GONE
        binding.cbCheckAllReceipt.isChecked = false
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
        adapter = ReceiptRecAdapter(this@ReceiptFragment)
        binding.recyclerView.adapter = adapter
        receiptViewModel.allReceiptListAsLiveData.observe(viewLifecycleOwner) {
            it.let {
                if (it.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.tvNoReceiptAdded.visibility = View.GONE
                    adapter.allReceiptList(it)
                    list = it as MutableList<Receipt>
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.tvNoReceiptAdded.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.fabAddRowReceipt.setOnClickListener(this)
        binding.cbCheckAllReceipt.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpBars() {
        Constants.setBottomNavigationBar(activity as ProPriceActivity, 2)
        Constants.setStateBar(activity as ProPriceActivity, 2)
        (activity as ProPriceActivity).setSupportActionBar(binding.toolbar)
        (activity as ProPriceActivity).supportActionBar?.title = ""
        (activity as ProPriceActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as ProPriceActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.fabAddRowReceipt -> {
                NavHostFragment.findNavController(this)
                    .navigate(
                        ReceiptFragmentDirections.actionNavigationReceiptToNavigationAddReceipt(
                            null
                        )
                    )
            }
            binding.cbCheckAllReceipt -> {
                setupCheckAllReceipt()
            }
        }
    }

    fun showAllCheckBox() {
        binding.fabAddRowReceipt.visibility = View.GONE
        binding.actionBarTitle.text = "0"
        binding.cbCheckAllReceipt.visibility = View.VISIBLE
        (activity as ProPriceActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.toolbar.menu.findItem(R.id.delete).setShowAsAction(1)
        binding.toolbar.menu.findItem(R.id.delete).isVisible = true
        binding.toolbar.menu.findItem(R.id.search).setShowAsAction(0)
        binding.toolbar.menu.findItem(R.id.search).isVisible = false
    }

    fun decreaseCounter() {
        count--
        binding.cbCheckAllReceipt.isChecked = false
        binding.actionBarTitle.text = count.toString()
    }

    fun increaseCounter() {
        count++
        binding.actionBarTitle.text = count.toString()
        if (count == adapter.itemCount)
            binding.cbCheckAllReceipt.isChecked = true
    }

    private fun setupCheckAllReceipt() {
        if (binding.cbCheckAllReceipt.isChecked) {
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

    fun editReceipt(receipt: Receipt) {
        NavHostFragment.findNavController(this)
            .navigate(
                ReceiptFragmentDirections.actionNavigationReceiptToNavigationAddReceipt(
                    receipt
                )
            )
    }

    private fun deleteReceipt(listToDelete: MutableList<Receipt>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                when (count) {
                    list.size ->
                        receiptViewModel.deleteAllReceipt()
                    else -> {
                        receiptViewModel.deleteReceipt(listToDelete)
                    }
                }
                withContext(Dispatchers.Main) {
                    delay(5)// to give time to delete receipts
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
        //Log.d(TAG, "deleteReceipt: count $count")
        //Log.d(TAG, "deleteReceipt: list xxx$listToDelete")
        val text = when (count) {
            list.size -> resources.getString(R.string.delete_all_items_msg)
            1 -> {
                listToDelete[0].receiptName + " " + resources.getString(R.string.delete_one_item_msg)
            }
            else -> count.toString() + " " + resources.getString(R.string.delete_many_items_msg)
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        builder.setTitle(resources.getString(R.string.delete))
        builder.setMessage(text)
        builder.create().show()
    }


}



