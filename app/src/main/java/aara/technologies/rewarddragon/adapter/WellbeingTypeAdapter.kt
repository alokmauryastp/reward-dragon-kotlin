package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.WellbeingTypeModel
import aara.technologies.rewarddragon.utils.TypeRefresh
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class WellbeingTypeAdapter(
    var arrayList: ArrayList<WellbeingTypeModel>,
    var context: Context,
    var onGameRefresh: TypeRefresh
) :
    RecyclerView.Adapter<WellbeingTypeAdapter.ViewHolder>() {

    private var selectedIndex: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.wellbeing_type_adapter, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = arrayList[position]

        if (selectedIndex == position) {
            holder.indicator.visibility = View.VISIBLE
            holder.name.setTextColor(context.getColor(R.color.yellow_app))
            holder.image.setColorFilter(
                ContextCompat.getColor(context, R.color.yellow_app),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            holder.name.setTextColor(context.getColor(R.color.white))
            holder.indicator.visibility = View.GONE
            holder.image.setColorFilter(
                ContextCompat.getColor(context, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }

        if (selectedIndex == position) {
            onGameRefresh.refresh(position)
        }

        holder.image.setImageDrawable(model.image)

        holder.parent.setOnClickListener {
            selectedIndex = position
            notifyDataSetChanged()
            onGameRefresh.refresh(position)
//            onGameRefresh.gameRefresh(model.id)
        }

        holder.name.text = model.name

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parent: RelativeLayout
        var name: TextView
        var image: ImageView
        var indicator: View

        init {
            parent = itemView.findViewById(R.id.parent)
            name = itemView.findViewById(R.id.toolbarTitle)
            image = itemView.findViewById(R.id.image)
            indicator = itemView.findViewById(R.id.indicator)

        }
    }


}