package dz.samalgeria.proprice.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dz.samalgeria.proprice.R
import dz.samalgeria.proprice.application.ProPriceApplication
import dz.samalgeria.proprice.databinding.DialogCustomListBinding
import dz.samalgeria.proprice.databinding.FragmentAddUpdateExtraBinding
import dz.samalgeria.proprice.model.entities.Extra
import dz.samalgeria.proprice.utils.Constants
import dz.samalgeria.proprice.utils.Constants.display
import dz.samalgeria.proprice.view.activities.ProPriceActivity
import dz.samalgeria.proprice.view.adapters.CustomListItemAdapter
import dz.samalgeria.proprice.viewmodel.ExtraViewModel
import dz.samalgeria.proprice.viewmodel.ExtraViewModelFactory

import java.util.*

class AddUpdateExtraFragment : Fragment(), View.OnClickListener {
    private val TAG = "AddUpdateExtraFragment"
    private var isUpdateCase = false
    private var _binding: FragmentAddUpdateExtraBinding? = null
    private val binding get() = _binding!!
    private lateinit var mExtraDetails: Extra
    private lateinit var mCustomListDialog: Dialog

    private val extraViewModel: ExtraViewModel by viewModels {
        ExtraViewModelFactory(((activity as ProPriceActivity).application as ProPriceApplication).extraRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUpdateExtraBinding.inflate(inflater, container, false)
        val view = binding.root

        val args: AddUpdateExtraFragmentArgs by navArgs()

        if (args.extra != null) {
            mExtraDetails = args.extra!!
            binding.etExtraName.setText(mExtraDetails.extraName)
            binding.etExtraPrice.setText(display(mExtraDetails.extraPrice))
            binding.etExtraUnites.setText(mExtraDetails.extraUnit)
            binding.bAddUpdateExtra.text = resources.getString(R.string.lbl_update)
            isUpdateCase = true
        } else
            mExtraDetails = Extra(-1L, "", 0f, "")


        setupActionBar()
        setupOnClickListeners()

        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.et_extra_unites -> {
                customItemsListDialog(
                    resources.getString(R.string.title_select_measure_unit),
                    Constants.measureUnites()
                )
                return
            }
            R.id.b_add_update_extra -> {
                var name = binding.etExtraName.text.toString().trim { it <= ' ' }
                name = name.toUpperCase()
                var price = binding.etExtraPrice.text.toString().trim { it <= ' ' }
                if (price == ".") price = ""
                val unit = binding.etExtraUnites.text.toString().trim { it <= ' ' }
                when {
                    TextUtils.isEmpty(name) -> {
                        Toast.makeText(
                            (activity as ProPriceActivity),
                            resources.getString(R.string.err_msg_enter_name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    TextUtils.isEmpty(price) -> {
                        Toast.makeText(
                            (activity as ProPriceActivity),
                            resources.getString(R.string.err_msg_enter_price),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    TextUtils.isEmpty(unit) -> {
                        Toast.makeText(
                            (activity as ProPriceActivity),
                            resources.getString(R.string.err_msg_select_measure_unit),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        mExtraDetails.extraName = name.toUpperCase()
                        mExtraDetails.extraPrice = display(price.toFloat()).toFloat()
                        mExtraDetails.extraUnit = unit

                        if (mExtraDetails.extraID == -1L) {
                            Log.d(TAG, "onClick: $mExtraDetails")
                            mExtraDetails.extraID = 0L
                            extraViewModel.insertExtra(mExtraDetails)
                            Toast.makeText(
                                (activity as ProPriceActivity),
                                resources.getString(R.string.add_new_extra_msg),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d(TAG, "onClick: end")

                        } else {
                            Log.d(TAG, "onClick: " + mExtraDetails.extraID)
                            extraViewModel.updateExtra(mExtraDetails)
                            Toast.makeText(
                                (activity as ProPriceActivity),
                                resources.getString(R.string.update_extra_msg),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Constants.hideKeyboard(context, requireView().windowToken)
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setupOnClickListeners() {
        binding.etExtraName.setOnClickListener(this)
        binding.etExtraPrice.setOnClickListener(this)
        binding.etExtraUnites.setOnClickListener(this)
        binding.bAddUpdateExtra.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun selectedListItem(item: String) {
        mCustomListDialog.dismiss()
        binding.etExtraUnites.setText(item)
    }

    private fun setupActionBar() {
        (activity as ProPriceActivity).setSupportActionBar(binding.toolbar)
        (activity as ProPriceActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as ProPriceActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        if (isUpdateCase) {
            (activity as ProPriceActivity).supportActionBar?.let {
                (binding.toolbar.getChildAt(0) as TextView).text =
                    resources.getString(R.string.title_edit_extra)
            }
        } else {
            (activity as ProPriceActivity).supportActionBar?.let {
                (binding.toolbar.getChildAt(0) as TextView).text =
                    resources.getString(R.string.title_add_extra)
            }
        }

    }

    private fun customItemsListDialog(title: String, itemsList: List<String>) {
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title

        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(
            requireActivity(),
            this@AddUpdateExtraFragment,
            itemsList,
        )
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }
}