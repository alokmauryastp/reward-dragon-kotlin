package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.AvatarModel
import aara.technologies.rewarddragon.utils.CellClickListener
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnRefresh
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AvatarAdapter(
    var arrayList: ArrayList<AvatarModel>,
    var context: Activity,
    private val cellClickListener: CellClickListener,
    refresh: OnRefresh
) :
    RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {
    var dialog: CustomLoader? = null
    private var avatar = ""
    var onRefresh: OnRefresh? = null

    companion object {
        var PICK_IMAGE: Int = 1212
    }


    init {
        onRefresh = refresh
        dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)
        avatar = SharedPrefManager.getInstance(context)!!.user.avatarImage.toString()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.avatar_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]

        holder.parent.setOnClickListener {
            cellClickListener.onCellClickListener(position, model)
        }

        if (position == 0) {
            holder.label.setPadding(0,0,0,20)
            holder.image.setPadding(35,35,35,0)
            Glide
                .with(context)
                .load(R.drawable.ic_upload)
                .apply(RequestOptions().override(100, 100))
                .fitCenter()
                .into(holder.image)
            holder.label.visibility = View.VISIBLE
        } else {
            holder.image.setPadding(35,35,35,35)
            Glide
                .with(context)
                .load(model.image)
                .placeholder(R.mipmap.logo)
                .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
                .fitCenter()
                .into(holder.image)
        }

        /*   println("avatar")
           println(avatar)
           println("model.image")
           println(model.image)*/
        if (avatar == model.image) {
            holder.checked.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parent: RelativeLayout
        var image: ImageView
        var checked: ImageView
        var label: TextView

        init {
            parent = itemView.findViewById(R.id.parent)
            image = itemView.findViewById(R.id.image)
            checked = itemView.findViewById(R.id.checked)
            label = itemView.findViewById(R.id.label)

        }
    }


}