package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.ConcernListLayoutBinding
import aara.technologies.rewarddragon.model.ConcernModel
import aara.technologies.rewarddragon.utils.Constant
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class MyConcernAdapter(
    var arrayList: ArrayList<ConcernModel>,
    var context: Context,
    var pageNumber: Int,
) :
    RecyclerView.Adapter<MyConcernAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.concern_list_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model: ConcernModel = arrayList[position]

        holder.binding.ticketId.text = model.ticket
        holder.binding.aging.text =
            Constant.daysCounter(model.createdAt.toString().substring(0, 10)) + " Days"
        holder.binding.status.text = if (model.status == 1) "Open" else "Closed"
        holder.binding.actionOwner.text = model.action_owner_name
        holder.binding.comments.text = model.comment

        holder.binding.description.text = model.description

        if (pageNumber == 1) {
            holder.binding.ll4.visibility = View.VISIBLE
            holder.binding.ll3.visibility = View.GONE
        } else {
            holder.binding.ll4.visibility = View.GONE
            holder.binding.ll3.visibility = View.VISIBLE
        }


    }


    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ConcernListLayoutBinding.bind(itemView)
    }
}