package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.LeaderBoardModel
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LeaderBoardAdapter(var arrayList: MutableList<LeaderBoardModel>, var context:Context) :
    RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reward_point_adapter, parent, false)
        return ViewHolder(view)
    }
    private  val TAG = "LeaderBoardAdapter"
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: LeaderBoardModel = arrayList[position]
        holder.name.text = model.employee_name
        holder.point.text = model.earned_point.toString()
    //    Log.i(TAG, "onBindViewHolder: ${model.user_image}")
        Glide.with(context).load(model.user_image).placeholder(R.drawable.user).into(holder.image)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var point: TextView
        lateinit var image:ImageView

        init {
            name = itemView.findViewById(R.id.toolbarTitle)
            point = itemView.findViewById(R.id.point)
            image=itemView.findViewById(R.id.image)

        }
    }

}