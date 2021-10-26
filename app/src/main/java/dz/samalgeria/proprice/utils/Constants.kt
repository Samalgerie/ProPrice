package dz.samalgeria.proprice.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import dz.samalgeria.proprice.R
import dz.samalgeria.proprice.view.activities.ProPriceActivity

object Constants {

    fun measureUnites(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("Unit")
        list.add("kg")
        list.add("g")
        list.add("m")
        list.add("mm")
        list.add("l")
        list.add("ml")
        return list
    }

    fun hideKeyboard(context: Context?, token: IBinder) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(token, 0)
    }

    fun display(d: Float): String {
        return if (d == (d.toLong()).toFloat())
            String.format("%d", d.toLong())
        else {
            String.format("%s", d.roundTo(1))

        }
    }

    private fun Float.roundTo(n: Int): Float {
        return "%.${n}f".format(this).replace(',', '.').toFloat()
    }

    @SuppressLint("RestrictedApi")
    fun setBottomNavigationBar(activity: ProPriceActivity, type: Int) {
        val layout = (activity).findViewById<ConstraintLayout>(R.id.cl_main_layout)
        val fromColor = (layout.background as ColorDrawable).color
        val toColor = when (type) {
            1 -> ContextCompat.getColor(activity, R.color.status_bar_raw2)
            2 -> ContextCompat.getColor(activity, R.color.status_bar_receipt2)
            3 -> ContextCompat.getColor(activity, R.color.status_bar_extra2)
            4 -> ContextCompat.getColor(activity, R.color.status_bar_product2)
            else -> ContextCompat.getColor(activity, R.color.status_bar_calculator2)
        }

        val backgroundColorAnimator = ObjectAnimator.ofObject(
            layout,
            "backgroundColor",
            ArgbEvaluator(),
            fromColor,
            toColor
        )
        backgroundColorAnimator.duration = 500
        backgroundColorAnimator.start()
    }

    @SuppressLint("RestrictedApi")
    fun setStateBar(activity: ProPriceActivity, type: Int) {
        val fromColor = activity.window.statusBarColor
        val toColor = when (type) {
            1 -> ContextCompat.getColor(activity, R.color.status_bar_raw)
            2 -> ContextCompat.getColor(activity, R.color.status_bar_receipt)
            3 -> ContextCompat.getColor(activity, R.color.status_bar_extra)
            4 -> ContextCompat.getColor(activity, R.color.status_bar_product)
            else -> ContextCompat.getColor(activity, R.color.status_bar_calculator)
        }
        val backgroundColorAnimator = ObjectAnimator.ofObject(
            activity.window,
            "statusBarColor",
            ArgbEvaluator(),
            fromColor,
            toColor
        )
        backgroundColorAnimator.duration = 100
        backgroundColorAnimator.start()
    }

}