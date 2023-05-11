package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.KpiAdapterBinding
import aara.technologies.rewarddragon.model.KpiModel
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText


class KPIAdapter(var arrayList: ArrayList<KpiModel>, var context: Context) :
    RecyclerView.Adapter<KPIAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.kpi_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = arrayList[position]

        holder.binding!!.toolbarTitle.text = model.name


        holder.binding!!.checkbox.setOnCheckedChangeListener { p0, p1 -> model.isChecked = p1 }


        if (model.kpi_unit.equals("Minutes", ignoreCase = true) || model.kpi_unit.equals(
                "Seconds",
                ignoreCase = true
            )
        ) {

            holder.binding!!.point.isActivated = false
            holder.binding!!.point.isFocusable = false
            holder.binding!!.point.isCursorVisible = false
            hideKeyboardFrom(context, holder.binding!!.point)


            holder.binding!!.point.setOnClickListener {

                openSetPointvalueDialog(holder.binding!!.point)
            }
        }




        holder.binding!!.point.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                model.point = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private val TAG = "KPIAdapter"


    fun openSetPointvalueDialog(point: EditText) {
        val builder = AlertDialog.Builder(context)
            .create()
        builder.setCancelable(true)
        builder.setCanceledOnTouchOutside(true)
        val view = LayoutInflater.from(context).inflate(R.layout.points_dialog, null)
        val hours = view.findViewById<TextInputEditText>(R.id.hours)
        val minute = view.findViewById<TextInputEditText>(R.id.minute)
        val submit = view.findViewById<TextView>(R.id.update)

        minute.filters = arrayOf<InputFilter>(MinMaxFilter(0, 59))
        // val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
        builder.setView(view)

        submit.setOnClickListener {
            var min = minute.text.toString()
            var hour = hours.text.toString()

            if (min.toInt() < 10) {
                min = "0$min"
            }

            if (hour.toInt() < 10) {
                hour = "0$hour"
            }
            point.setText("$hour:$min")

            builder.dismiss()
        }
        /*  button.setOnClickListener {
              builder.dismiss()
          }*/
        builder.setCanceledOnTouchOutside(false)
        builder.show()


    }


    // Custom class to define min and max for the edit text
    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        // Initialized
        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dStart: Int,
            dEnd: Int
        ): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        // Check if input c is in between min a and max b and
        // returns corresponding boolean
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: KpiAdapterBinding? = null

        init {
            binding = KpiAdapterBinding.bind(itemView)
        }
    }


    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}