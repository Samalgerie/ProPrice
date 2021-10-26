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
import dz.samalgeria.proprice.databinding.FragmentExtraBinding
import dz.samalgeria.proprice.model.entities.Extra
import dz.samalgeria.proprice.utils.Constants
import dz.samalgeria.proprice.view.activities.ProPriceActivity
import dz.samalgeria.proprice.view.adapters.ExtraRecAdapter
import dz.samalgeria.proprice.viewmodel.ExtraViewModel
import dz.samalgeria.proprice.viewmodel.ExtraViewModelFactory
import kotlinx.coroutines.*
import java.util.*


class ExtraFragment : Fragment(), View.OnClickListener {

    //private val TAG = "TAGGYExtraFragment"
    private lateinit var extraList: MutableList<Extra>
    private var _binding: FragmentExtraBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ExtraRecAdapter
    private var count = 0


    private val weMixViewModel: ExtraViewModel by viewModels {
        ExtraViewModelFactory((requireActivity().application as ProPriceApplication).extraRepository)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExtraBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBars()
        /*     weMixViewModel.insertExtra(Extra(0, "AAA", 500f, "m"))
               weMixViewModel.insertExtra(Extra(0, "BBB", 2f, "mm"))
               weMixViewModel.insertExtra(Extra(0, "CCC", 428f, "ml"))
               weMixViewModel.insertExtra(Extra(0, "DDD", 50.8f, "g"))
               weMixViewModel.insertExtra(Extra(0, "EEE", 33f, "m"))
               weMixViewModel.insertExtra(Extra(0, "FFF", 82f, "l"))
               weMixViewModel.insertExtra(Extra(0, "GGG", 59.99f, "kg"))
       */
        binding.cbCheckAllExtra.isChecked = false
        recycleViewSetUp()
        setOnClickListeners()
        setupOnBackPressed()
        setHasOptionsMenu(true)
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
                val listToDelete: MutableList<Extra> = mutableListOf()
                //Log.d(TAG, "adapter.isCheckBoxList.size: " + adapter.isCheckBoxList.size)
                for (i in adapter.isCheckBoxList.indices) {
                    if (adapter.isCheckBoxList[i]) {
                        listToDelete.add(adapter.filteredExtraList[i])
                    }
                }
                //Log.d(TAG, "list.size: " + list.size)
                //Log.d(TAG, "listToDelete.size: " + listToDelete.size)
                if (listToDelete.size > 0)
                    deleteExtra(listToDelete)

            }
            R.id.search -> {
                binding.tvNoExtraAdded.text = ""
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.cbCheckAllExtra.visibility == View.VISIBLE) {
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
        binding.fabAddRowExtra.visibility = View.VISIBLE
        binding.actionBarTitle.text = resources.getString(R.string.extra)
        binding.toolbar.menu.findItem(R.id.delete).setShowAsAction(0)
        binding.toolbar.menu.findItem(R.id.delete).isVisible = false
        binding.toolbar.menu.findItem(R.id.search).setShowAsAction(1)
        binding.toolbar.menu.findItem(R.id.search).isVisible = true
        val search = binding.toolbar.menu.findItem(R.id.search)
        val searchView = search?.actionView as? SearchView
        searchView?.isFocusable = false
        binding.cbCheckAllExtra.visibility = View.GONE
        binding.cbCheckAllExtra.isChecked = false
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
        adapter = ExtraRecAdapter(this@ExtraFragment)
        binding.recyclerView.adapter = adapter
        weMixViewModel.allExtraListAsLiveData.observe(viewLifecycleOwner) {
            it.let {
                //Log.d(TAG, "recycleViewSetUp: " + it.size)
                if (it.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.tvNoExtraAdded.visibility = View.GONE
                    extraList = it as MutableList<Extra>
                    adapter.allExtraList(it)
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.tvNoExtraAdded.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.fabAddRowExtra.setOnClickListener(this)
        binding.cbCheckAllExtra.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpBars() {
        Constants.setBottomNavigationBar(activity as ProPriceActivity, 3)
        Constants.setStateBar(activity as ProPriceActivity, 3)
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
            binding.fabAddRowExtra -> {
                NavHostFragment.findNavController(this)
                    .navigate(ExtraFragmentDirections.actionNavigationExtraToAddExtraFragment(null))
            }
            binding.cbCheckAllExtra -> {
                setupCheckAllExtra()


            }
        }
    }

    fun showAllCheckBox() {
        binding.fabAddRowExtra.visibility = View.GONE
        binding.actionBarTitle.text = "0"
        binding.cbCheckAllExtra.visibility = View.VISIBLE
        (activity as ProPriceActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.toolbar.menu.findItem(R.id.delete).setShowAsAction(1)
        binding.toolbar.menu.findItem(R.id.delete).isVisible = true
        binding.toolbar.menu.findItem(R.id.search).setShowAsAction(0)
        binding.toolbar.menu.findItem(R.id.search).isVisible = false
    }

    fun decreaseCounter() {
        count--
        binding.cbCheckAllExtra.isChecked = false
        binding.actionBarTitle.text = count.toString()
    }

    fun increaseCounter() {
        count++
        binding.actionBarTitle.text = count.toString()
        if (count == adapter.itemCount)
            binding.cbCheckAllExtra.isChecked = true
    }

    private fun setupCheckAllExtra() {
        if (binding.cbCheckAllExtra.isChecked) {
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

    fun editExtra(extra: Extra) {
        NavHostFragment.findNavController(this)
            .navigate(ExtraFragmentDirections.actionNavigationExtraToAddExtraFragment(extra))
    }

    private fun deleteExtra(listToDelete: MutableList<Extra>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                when (count) {
                    extraList.size ->
                        weMixViewModel.deleteAllExtra()
                    else -> {
                        weMixViewModel.deleteExtra(listToDelete)
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

        }

        val text = when (count) {
            extraList.size -> resources.getString(R.string.delete_all_items_msg)
            1 -> {
                listToDelete[0].extraName + " " + resources.getString(R.string.delete_one_item_msg)
            }
            else -> count.toString() + " " + resources.getString(R.string.delete_many_items_msg)
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        builder.setTitle(resources.getString(R.string.delete))
        builder.setMessage(text)
        builder.create().show()
    }
}



