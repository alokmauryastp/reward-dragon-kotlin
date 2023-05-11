package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.KpiData
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.Nullable

class KpiArrayAdapter  // invoke the suitable constructor of the ArrayAdapter class
    (context: Context, var arrayList: List<KpiData>?) :
    ArrayAdapter<KpiData?>(context, 0, arrayList!!) {

    private val TAG = "TeamCampaignAdapter"

    override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {

        // convertView which is recyclable view
        var currentItemView = convertView

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView =
                LayoutInflater.from(context).inflate(R.layout.custom_kpi_list, parent, false)
        }

        // get the position of the view from the ArrayAdapter
        //  var kpidata= arrayList[position]

        // then according to the position of the view assign the desired image for the same
        val textView: TextView = currentItemView!!.findViewById(R.id.rulename)
        val rulepoint: TextView = currentItemView.findViewById(R.id.rulepoint)
        textView.text = arrayList!![position].rule
        rulepoint.text = arrayList!![position].point.toString()
        Log.i(TAG, "getView: "+arrayList!![position])



        return currentItemView
    }
}