package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.KpiListInPerformanceBinding
import aara.technologies.rewarddragon.model.KpiPerformanceModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class KpiPerformanceAdapter(private var list: List<KpiPerformanceModel>, var context: Context) :

    RecyclerView.Adapter<KpiPerformanceAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        var binding: KpiListInPerformanceBinding? = null

        init {
            binding = KpiListInPerformanceBinding.bind(ItemView)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.kpi_list_in_performance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = list[position]

        holder.binding!!.kpiName.text = model.kpi_name
        holder.binding!!.totalTarget.text = model.total_target
        holder.binding!!.totalActual.text = model.total_actual
        holder.binding!!.totalGap.text = model.total_gap.replace("-","")

    }

    override fun getItemCount(): Int {
        return list.size
    }

}