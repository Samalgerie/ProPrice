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
import dz.samalgeria.proprice.databinding.FragmentRawBinding
import dz.samalgeria.proprice.model.entities.Raw
import dz.samalgeria.proprice.utils.Constants
import dz.samalgeria.proprice.view.activities.ProPriceActivity
import dz.samalgeria.proprice.view.adapters.RawRecAdapter
import dz.samalgeria.proprice.viewmodel.RawViewModel
import dz.samalgeria.proprice.viewmodel.RawViewModelFactory
import kotlinx.coroutines.*
import java.util.*


class RawFragment : Fragment(), View.OnClickListener {

    //    private val TAG = "taggyRawFragment"
    private lateinit var list: MutableList<Raw>
    private var _binding: FragmentRawBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RawRecAdapter
    private var count = 0


    private val rawViewModel: RawViewModel by viewModels {
        RawViewModelFactory((requireActivity().application as ProPriceApplication).rawRepository)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRawBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

/*
        rawViewModel.insertRaw(Raw(0, "AAA", 500f))
        rawViewModel.insertRaw(Raw(0, "BBB", 2f))
        rawViewModel.insertRaw(Raw(0, "CCC", 428f))
        rawViewModel.insertRaw(Raw(0, "DDD", 50.8f))
        rawViewModel.insertRaw(Raw(0, "EEE", 33f))
        rawViewModel.insertRaw(Raw(0, "FFF", 82f))
        rawViewModel.insertRaw(Raw(0, "GGG", 59.99f))
*/

        binding.cbCheckAllRaw.isChecked = false
        setOnClickListeners()
        recycleViewSetUp()
        setupOnBackPressed()
        setUpBars()
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
                val listToDelete: MutableList<Raw> = mutableListOf()
                for (i in adapter.isCheckBoxList.indices) {
                    if (adapter.isCheckBoxList[i]) {
                        listToDelete.add(adapter.filteredRawList[i])
                    }
                }
                if (listToDelete.size > 0)
                    deleteRaw(listToDelete)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.cbCheckAllRaw.visibility == View.VISIBLE) {
                        setupUIOriginalState()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
            )
    }

    private fun setupUIOriginalState() {
        binding.fabAddRowRaw.visibility = View.VISIBLE
        binding.actionBarTitle.text = resources.getString(R.string.raw)
        binding.toolbar.menu.findItem(R.id.delete).setShowAsAction(0)
        binding.toolbar.menu.findItem(R.id.delete).isVisible = false
        binding.toolbar.menu.findItem(R.id.search).setShowAsAction(1)
        binding.toolbar.menu.findItem(R.id.search).isVisible = true
        val search = binding.toolbar.menu.findItem(R.id.search)
        val searchView = search?.actionView as? SearchView
        searchView?.isFocusable = false
        binding.cbCheckAllRaw.visibility = View.GONE
        binding.cbCheckAllRaw.isChecked = false
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
        adapter = RawRecAdapter(this@RawFragment)
        binding.recyclerView.adapter = adapter
        rawViewModel.allRawListAsLiveData.observe(viewLifecycleOwner) {
            it.let {
                if (it.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.tvNoRawAdded.visibility = View.GONE
                    adapter.allRawList(it)
                    list = it as MutableList<Raw>
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.tvNoRawAdded.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.fabAddRowRaw.setOnClickListener(this)
        binding.cbCheckAllRaw.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpBars() {
        Constants.setBottomNavigationBar(activity as ProPriceActivity, 1)
        Constants.setStateBar(activity as ProPriceActivity, 1)
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
            binding.fabAddRowRaw -> {
                NavHostFragment.findNavController(this)
                    .navigate(RawFragmentDirections.actionNavigationRawToAddRawFragment(null))
            }
            binding.cbCheckAllRaw -> {
                setupCheckAllRaw()
            }
        }
    }

    fun showAllCheckBox() {
        binding.fabAddRowRaw.visibility = View.GONE
        binding.actionBarTitle.text = "0"
        binding.cbCheckAllRaw.visibility = View.VISIBLE
        (activity as ProPriceActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.toolbar.menu.findItem(R.id.delete).setShowAsAction(1)
        binding.toolbar.menu.findItem(R.id.delete).isVisible = true
        binding.toolbar.menu.findItem(R.id.search).setShowAsAction(0)
        binding.toolbar.menu.findItem(R.id.search).isVisible = false
    }

    fun decreaseCounter() {
        count--
        binding.cbCheckAllRaw.isChecked = false
        binding.actionBarTitle.text = count.toString()
    }

    fun increaseCounter() {
        count++
        binding.actionBarTitle.text = count.toString()
        if (count == adapter.itemCount)
            binding.cbCheckAllRaw.isChecked = true
    }

    private fun setupCheckAllRaw() {
        if (binding.cbCheckAllRaw.isChecked) {
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

    fun editRaw(raw: Raw) {
        NavHostFragment.findNavController(this)
            .navigate(RawFragmentDirections.actionNavigationRawToAddRawFragment(raw))
    }

    private fun deleteRaw(listToDelete: MutableList<Raw>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                when (count) {
                    list.size ->
                        rawViewModel.deleteAllRaw()
                    else -> {
                        rawViewModel.deleteRaw(listToDelete)
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
        //Log.d(TAG, "deleteRaw: count $count")
        //Log.d(TAG, "deleteRaw: list xxx$listToDelete")
        val text = when (count) {
            list.size -> resources.getString(R.string.delete_all_items_msg)
            1 -> {
                listToDelete[0].rawName + " " + resources.getString(R.string.delete_one_item_msg)
            }
            else -> count.toString() + " " + resources.getString(R.string.delete_many_items_msg)
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        builder.setTitle(resources.getString(R.string.delete))
        builder.setMessage(text)
        builder.create().show()
    }


}



