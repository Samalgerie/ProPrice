package dz.samalgeria.proprice.view.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dz.samalgeria.proprice.R
import dz.samalgeria.proprice.application.ProPriceApplication
import dz.samalgeria.proprice.databinding.FragmentAddUpdateRawBinding
import dz.samalgeria.proprice.model.entities.Raw
import dz.samalgeria.proprice.utils.Constants.display
import dz.samalgeria.proprice.view.activities.ProPriceActivity
import dz.samalgeria.proprice.viewmodel.RawViewModel
import dz.samalgeria.proprice.viewmodel.RawViewModelFactory

class AddUpdateRawFragment : Fragment(), View.OnClickListener {
    private val TAG = "AddUpdateRawFragment"
    private var isUpdateCase = false
    private var _binding: FragmentAddUpdateRawBinding? = null
    private val binding get() = _binding!!
    private lateinit var mRawDetails: Raw

    private val weMixViewModel: RawViewModel by viewModels {
        RawViewModelFactory(((activity as ProPriceActivity).application as ProPriceApplication).rawRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUpdateRawBinding.inflate(inflater, container, false)
        val view = binding.root
        val args: AddUpdateRawFragmentArgs by navArgs()

        if (args.raw != null) {
            mRawDetails = args.raw!!
            binding.etRawName.setText(mRawDetails.rawName)
            binding.etRawPrice.setText(display(mRawDetails.rawPrice))
            binding.bAddUpdateRaw.text = resources.getString(R.string.lbl_update)
            isUpdateCase = true
        } else
            mRawDetails = Raw(rawID = -1L, rawName = "", rawPrice = 0f)
        setupActionBar()
        setupOnClickListeners()

        return view
    }

    private fun setupOnClickListeners() {
        binding.etRawName.setOnClickListener(this)
        binding.etRawPrice.setOnClickListener(this)
        binding.bAddUpdateRaw.setOnClickListener(this)
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
                    resources.getString(R.string.title_edit_raw)
            }
        } else {
            (activity as ProPriceActivity).supportActionBar?.let {
                (binding.toolbar.getChildAt(0) as TextView).text =
                    resources.getString(R.string.title_add_raw)
            }
        }

        //supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.b_add_update_raw -> {
                var name = binding.etRawName.text.toString().trim { it <= ' ' }
                name = name.toUpperCase()
                var price = binding.etRawPrice.text.toString().trim { it <= ' ' }
                if (price == ".") price = ""
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

                    else -> {
                        mRawDetails.rawName = name.toUpperCase()
                        mRawDetails.rawPrice = display(price.toFloat()).toFloat()

                        if (mRawDetails.rawID == -1L) {
                            mRawDetails.rawID = 0
                            weMixViewModel.insertRaw(mRawDetails)
                            Toast.makeText(
                                (activity as ProPriceActivity),
                                resources.getString(R.string.add_new_raw_msg),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            weMixViewModel.updateRaw(mRawDetails)
                            Toast.makeText(
                                (activity as ProPriceActivity),
                                resources.getString(R.string.update_raw_msg),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}