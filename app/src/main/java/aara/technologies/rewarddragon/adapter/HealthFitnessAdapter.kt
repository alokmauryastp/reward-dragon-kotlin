package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.LeadershipDataModel
import aara.technologies.rewarddragon.utils.Constant.getYouTubeId
import aara.technologies.rewarddragon.utils.onClickkListener
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class HealthFitnessAdapter(
    var arrayList: ArrayList<LeadershipDataModel>,
    var context: Context,
    var listner: onClickkListener
) :
    RecyclerView.Adapter<HealthFitnessAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.videos_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val model: LeadershipDataModel = arrayList[position]

//        val videoId = model.video_url.substring((model.video_url.length - 11), model.video_url.length)
        var videoId: String? = getYouTubeId(model.video_url)

        print(videoId)
        Glide
            .with(context)
            .load("https://img.youtube.com/vi/$videoId/0.jpg")
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .into(holder.image)

        holder.name.text = model.title
        holder.image.setOnClickListener {
            Log.e("TAG", "onBindViewHolder: " + model.id)
//            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(model.video_url))
//            context.startActivity(browserIntent)

            listner.onCellClickListener(model)

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var name: TextView

        init {
            image = itemView.findViewById(R.id.video)
            name = itemView.findViewById(R.id.toolbarTitle)
        }
    }
}