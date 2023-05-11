package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.GameCategoryModel
import aara.technologies.rewarddragon.utils.OnGameRefresh
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameCategoryAdapter(
    var arrayList: ArrayList<GameCategoryModel>,
    var context: Context,
    var onGameRefresh: OnGameRefresh
) :
    RecyclerView.Adapter<GameCategoryAdapter.ViewHolder>() {

    private var selectedIndex  : Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_category_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = arrayList[position]

        if (selectedIndex==position){
            holder.parent.setBackgroundDrawable(context.resources.getDrawable(R.drawable.gradient_bg))
        }else{
            holder.parent.setBackgroundDrawable(context.resources.getDrawable(R.drawable.gradient_without_stroke))
        }

        if (selectedIndex==position){
            onGameRefresh.gameRefresh(model.id)
        }

        holder.parent.setOnClickListener {
            selectedIndex = position
            notifyDataSetChanged()
//            onGameRefresh.gameRefresh(model.id)
        }


        holder.name.text = model.game_category


    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parent: LinearLayout

        var name: TextView

        init {
            parent = itemView.findViewById(R.id.parent)
            name = itemView.findViewById(R.id.toolbarTitle)

        }
    }


}