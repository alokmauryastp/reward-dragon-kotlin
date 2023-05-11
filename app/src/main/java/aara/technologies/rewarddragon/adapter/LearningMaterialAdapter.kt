package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.activities.PlayVideoActivity
import aara.technologies.rewarddragon.model.LearningMaterialModel
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
import com.bumptech.glide.request.RequestOptions
import java.util.regex.Matcher
import java.util.regex.Pattern


class LearningMaterialAdapter(
    var arrayList: ArrayList<LearningMaterialModel>,
    var context: Context
) :
    RecyclerView.Adapter<LearningMaterialAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.learning_material_adapter, parent, false)
        return ViewHolder(view)
    }

    private val TAG = "LearningMaterialAdapter"
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model: LearningMaterialModel = arrayList[position]

        // val videoId = model.learning_site_url.substring((model.learning_site_url.length - 11), model.learning_site_url.length)
        Log.i(TAG, "onBindViewHolder: url ${model.learning_site_url}")


        var videoId: String? = getYouTubeId(model.learning_site_url)


        holder.parent.setOnClickListener {


            if (model.learning_site_url != null) {
            }
            Log.i(TAG, "onBindViewHolder: url $videoId")
            if (videoId != null) {
                context.startActivity(
                    Intent(context, PlayVideoActivity::class.java)
                        .putExtra("videoId", model.id)
                        .putExtra("from", "learning")
                        .putExtra("link", videoId)
                )
            } else {
                Log.i(TAG, "onBindViewHolder: working")
                //  Toast.makeText(holder.itemView.context, "Video is unavailable!", Toast.LENGTH_SHORT)
            }
        }

        holder.name.text = model.title

        if (model.learning_site_url != null) {
            Glide
                .with(context)
                .load("https://img.youtube.com/vi/$videoId/0.jpg")
                .placeholder(R.mipmap.logo)
                .fitCenter()
                .into(holder.image)
        }


    }

    fun extractYTId(ytUrl: String?): String? {
        var vId: String? = null
        val pattern: Pattern = Pattern.compile(
            "^https?://.*(?:youtu.be/|v/|u/\\w/|watch?v=)([^#&?]*).*$",
            Pattern.CASE_INSENSITIVE
        )
        val matcher: Matcher = pattern.matcher(ytUrl)
        if (matcher.matches()) {
            vId = matcher.group(1)
        }
        return vId
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