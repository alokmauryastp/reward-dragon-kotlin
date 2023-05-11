package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.TeamLeaderboardModel
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeamLeaderBoardAdapter(var arrayList: ArrayList<TeamLeaderboardModel>, var context: Context) :
    RecyclerView.Adapter<TeamLeaderBoardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reward_point_adapter, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = arrayList[position]

        holder.name.text = model.first_name+" "+model.last_name
        holder.point.text = model.earned_point.toString()

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var point: TextView

        init {
            name = itemView.findViewById(R.id.toolbarTitle)
            point = itemView.findViewById(R.id.point)

        }
    }

}