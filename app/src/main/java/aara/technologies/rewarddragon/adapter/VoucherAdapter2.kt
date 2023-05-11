package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.CouponData
import aara.technologies.rewarddragon.utils.CustomLoader
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class VoucherAdapter2(
    var arrayList: ArrayList<CouponData.CouponDataItem>,
    var context: Context,
    var redeemReward: RedeemReward
) :
    RecyclerView.Adapter<VoucherAdapter2.ViewHolder>() {

    interface RedeemReward {
        fun onRedeem(model: CouponData.CouponDataItem)
    }

    var dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.voucher_adapter_layout2, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]

        Glide
            .with(context)
            .load(model.couponImage)
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .into(holder.image)

        holder.parent.setOnClickListener {

            redeemReward.onRedeem(model)
            // openDialog(model)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var wishlist: ImageView
        var parent: CardView

        init {
            image = itemView.findViewById(R.id.image)
            wishlist = itemView.findViewById(R.id.wishlist)
            parent = itemView.findViewById(R.id.parent)
            wishlist.visibility = View.GONE
        }
    }


}