package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.TeamDataLayoutBinding
import aara.technologies.rewarddragon.model.TeamsDataModel
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class MyTeamAdapter(var itemss: ArrayList<TeamsDataModel>, var context: Context) :
    RecyclerView.Adapter<MyTeamAdapter.ViewHolder>() {


    private var arraylist: ArrayList<TeamsDataModel> = ArrayList()

    init {
        this.arraylist.addAll(itemss)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var binding: TeamDataLayoutBinding? = null

        init {
            binding = TeamDataLayoutBinding.bind(itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.team_data_layout, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = itemss[position]
        holder.binding!!.toolbarTitle.text = model.employee_name
        holder.binding!!.designation.text = model.designation


    }

    override fun getItemCount(): Int {
        return itemss.size
    }

    fun filter(id: String) {
        var id = id
        id = id.toLowerCase(Locale.getDefault())
        itemss.clear()
        if (id.isEmpty()) {
            itemss.addAll(arraylist)
        } else {
            println(arraylist.size)
            for (wp in arraylist) {

                if (wp.employee_name.toLowerCase(Locale.getDefault()).contains(id)) {
                    itemss.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

}

