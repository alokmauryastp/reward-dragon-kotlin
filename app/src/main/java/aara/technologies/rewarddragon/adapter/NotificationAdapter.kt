package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.Dashboard
import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.activities.MyCampaigns
import aara.technologies.rewarddragon.activities.MyGameTime
import aara.technologies.rewarddragon.activities.MyJoshForToday
import aara.technologies.rewarddragon.activities.MyLatestChallenge
import aara.technologies.rewarddragon.databinding.NotificationListBinding
import aara.technologies.rewarddragon.manager.MyResources
import aara.technologies.rewarddragon.model.NotificationModel
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(

    private var notifications: List<NotificationModel>,
    var context: Context
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        var binding: NotificationListBinding? = null

        init {
            binding = NotificationListBinding.bind(itemView)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_list, parent, false)
        return ViewHolder(view)
    }

    private val TAG = "NotificationAdapter"

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = notifications[position]

        holder.binding!!.notiDetails.text = model.activity
        holder.binding!!.notiHeading.text = model.heading
        holder.binding!!.radio1.isChecked = model.is_selected
        holder.binding!!.radio1.isClickable = false
        holder.binding!!.points.text = model.created_at
        if (model.redirectional_code != null && model.redirectional_code.isNotEmpty()) {
            holder.binding!!.arrow.visibility = View.VISIBLE
            holder.binding!!.arrow.setOnClickListener {
                Log.i(TAG, "onBindViewHolder: " + model.redirectional_code)
                when (model.redirectional_code.trim()) {
                    "my_leaderboard" -> {
                        context.startActivity(
                            Intent(
                                context,
                                Dashboard::class.java
                            ).putExtra("from", "my_leaderboard")
                        )
                    }
                    "my_josh" -> {
                        context.startActivity(Intent(context, MyJoshForToday::class.java))
                    }
                    "my_wellbeing" -> {
                        context.startActivity(
                            Intent(
                                context,
                                Dashboard::class.java
                            ).putExtra("from", "MyWellBeing")
                        )
                        //  (context as Dashboard).addFragment(MyWellBeing())
                    }
                    "game_for_today" -> {
                        context.startActivity(Intent(context, MyGameTime::class.java))
                    }
                    "my_challenges" -> {
                        context.startActivity(Intent(context, MyLatestChallenge::class.java))
                    }
                    "new_challenge" -> {
                        context.startActivity(
                            Intent(
                                context,
                                MyLatestChallenge::class.java
                            )
                        ) //tested
                    }
                    "my_resources" -> {
                        context.startActivity(Intent(context, MyResources::class.java))
                    }
                    "new_campaign" -> {
                        context.startActivity(Intent(context, MyCampaigns::class.java)) //tested
                    }
                    "home_page" -> {
                        context.startActivity(Intent(context, Dashboard::class.java))
                    }
                    "my_challenge" -> {
                        context.startActivity(Intent(context,MyLatestChallenge::class.java))
                    }
                }
            }
        }


//        holder.binding!!.notiDetails.less


        if (model.is_read) holder.binding!!.star.visibility =
            View.GONE else holder.binding!!.star.visibility = View.VISIBLE
        holder.itemView.setOnClickListener {
            println(position)
            println(model.is_selected)
            if (model.is_selected) {
                holder.binding!!.radio1.isChecked = false
                model.is_selected = false
            } else {
                holder.binding!!.radio1.isChecked = true
                model.is_selected = true
            }
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }


}