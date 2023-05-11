package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.KpiPerformanceData
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.guilhe.views.CircularProgressView

class SpeedometerAdapter(var arrayList: ArrayList<KpiPerformanceData>, var context: Context) :
    RecyclerView.Adapter<SpeedometerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.speedometer_adapter, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]

        holder.name.text=model.kpi_name
        holder.percent.text=model.kpi_percent.toInt().toString()+"%"
        holder.circularProgressView.setProgress(model.kpi_percent.toFloat())
        holder.circularProgressView.animate()
        if (model.kpi_percent.toInt()<50){
            holder.circularProgressView.progressColor = ContextCompat.getColor(context,android.R.color.holo_orange_light)
            holder.circularProgressView.progressBackgroundColor = ContextCompat.getColor(context,android.R.color.holo_orange_light)
        }
//        holder.speedView.speedTo(model.kpi_percent.toFloat())
//        holder.speedView.trembleDegree = 0f

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parent: LinearLayout
//        var speedView: SpeedView
        var circularProgressView: CircularProgressView
        var name: TextView
        var percent: TextView

        init {
            parent = itemView.findViewById(R.id.parent)
            circularProgressView = itemView.findViewById(R.id.circular)
//            speedView = itemView.findViewById(R.id.speedView)
            name = itemView.findViewById(R.id.toolbarTitle)
            percent = itemView.findViewById(R.id.percent)
        }
    }

}