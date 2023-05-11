package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.OtherLinkModel
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class OtherLinkAdapter(var arrayList: ArrayList<OtherLinkModel>, var context: Context) :
    RecyclerView.Adapter<OtherLinkAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.other_link_adapter, parent, false)
        return ViewHolder(view)
    }

    private val TAG = "OtherLinkAdapter"
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: OtherLinkModel = arrayList[position]

        Log.i(TAG, "onBindViewHolder: " + model.url)

        holder.url.setOnClickListener {
            try {
                val browserIntent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN,Intent.CATEGORY_APP_BROWSER)
                browserIntent.data = Uri.parse(model.url.trim());
                val chooseIntent = Intent.createChooser(browserIntent, "Choose from below")


                context.startActivity(chooseIntent)


            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "No application can handle this request. Please install a web browser or check your URL.",
                    Toast.LENGTH_LONG
                ).show();
            }

        }


        Glide
            .with(context)
            .load(model.image_data)
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .into(holder.url)

        holder.name.text = model.title

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var url: ImageView
        var name: TextView

        init {
            url = itemView.findViewById(R.id.url)
            name = itemView.findViewById(R.id.toolbarTitle)

        }
    }

}