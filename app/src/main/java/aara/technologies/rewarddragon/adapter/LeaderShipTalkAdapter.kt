package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.activities.PlayVideoActivity
import aara.technologies.rewarddragon.model.LeadershipTalkModel
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.regex.Pattern


class LeaderShipTalkAdapter(
    var arrayList: ArrayList<LeadershipTalkModel>,
    var context: Context
) :
    RecyclerView.Adapter<LeaderShipTalkAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.learning_material_adapter, parent, false)
        return ViewHolder(view)
    }

    private val TAG = "LearningMaterialAdapter"
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model: LeadershipTalkModel = arrayList[position]

        // val videoId = model.learning_site_url.substring((model.learning_site_url.length - 11), model.learning_site_url.length)
        Log.i(TAG, "LeaderShipTalkAdapter: url ${model.videoUrl}")


        var videoId: String? = model.videoUrl?.let { getYouTubeId(it) }

        holder.parent.setOnClickListener {


            if (model.videoUrl != null) {
            }
            Log.i(TAG, "onBindViewHolder: url $videoId")
            if (videoId != null) {
                context.startActivity(
                    Intent(context, PlayVideoActivity::class.java)
                        .putExtra("videoId", model.id)
                        .putExtra("from", "")
                        .putExtra("link", videoId)
                )
            } else {
                Log.i(TAG, "onBindViewHolder: working")
                //  Toast.makeText(holder.itemView.context, "Video is unavailable!", Toast.LENGTH_SHORT)
            }
        }

        holder.name.text = model.title

        if (model.videoUrl != null) {
            Glide
                .with(context)
                .load("https://img.youtube.com/vi/$videoId/0.jpg")
                .placeholder(R.mipmap.logo)
                .fitCenter()
                .into(holder.image)
        }
    }

    private fun getYouTubeId(youTubeUrl: String): String? {
        val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(youTubeUrl)
        return if (matcher.find()) {
            matcher.group()
        } else {
            null
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parent: ConstraintLayout
        var image: ImageView
        var name: TextView

        init {
            parent = itemView.findViewById(R.id.parent)
            image = itemView.findViewById(R.id.image)
            name = itemView.findViewById(R.id.toolbarTitle)

        }
    }

}