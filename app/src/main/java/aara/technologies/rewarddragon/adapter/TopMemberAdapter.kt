package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.RewardPointAdapterBinding
import aara.technologies.rewarddragon.model.TopMemberModel
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class TopMemberAdapter(private var notifications: List<TopMemberModel>, var context: Context) :

    RecyclerView.Adapter<TopMemberAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        var binding: RewardPointAdapterBinding? = null

        init {
            binding = RewardPointAdapterBinding.bind(ItemView)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reward_point_adapter, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = notifications[position]

        holder.binding!!.toolbarTitle.text = model.username
        holder.binding!!.point.text = model.percent.toString()+"%"
        Glide
            .with(context)
            .load(model.avatar_image)
            .placeholder(R.drawable.user)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .into(holder.binding!!.image)

    }

    override fun getItemCount(): Int {
        return notifications.size
    }

}