package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.activities.PlayVideoActivity
import aara.technologies.rewarddragon.model.CompanySiteModel
import aara.technologies.rewarddragon.utils.Constant.getYouTubeId
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class InspairedLivingAdapter(var arrayList: ArrayList<CompanySiteModel>, var context: Context) :
    RecyclerView.Adapter<InspairedLivingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.company_site_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: CompanySiteModel = arrayList[position]
        var url: String = model.site_url


        var videoId: String? = getYouTubeId(url)


        Glide
            .with(context)
            .load("https://img.youtube.com/vi/$videoId/0.jpg")
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .into(holder.image)

        holder.parent.setOnClickListener {
//            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//            context.startActivity(browserIntent)

            if (videoId != null)
                context.startActivity(
                    Intent(context, PlayVideoActivity::class.java)
                        .putExtra("videoId", model.id)
                        .putExtra("from", "inspiredLiving")
                        .putExtra("link", videoId)
                )
        }

//        Glide
//            .with(context)
//            .load(model.image_data)
//            .placeholder(R.mipmap.logo)
//            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
//            .fitCenter()
//            .into(holder.image)

        holder.name.text = model.title

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