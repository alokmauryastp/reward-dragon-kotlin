package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.WishlistRecyclerLayoutBinding
import aara.technologies.rewarddragon.model.VoucherModel
import aara.technologies.rewarddragon.utils.CustomLoader
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson

class MyWishlistAdapter(
    var arrayList: ArrayList<VoucherModel>,
    var context: Context,
    var showVoucher: VoucherDetail,
    var onClickRemove: onClickRemoveWishList
) :
    RecyclerView.Adapter<MyWishlistAdapter.ViewHolder>() {

    private val TAG = "MyWishlistAdapter"

    interface VoucherDetail {

        fun showVoucher(
            model: VoucherModel,
            position: Int,
            arrayList: ArrayList<VoucherModel>,
            myWishlistAdapter: MyWishlistAdapter
        )
    }

    var dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.wishlist_recycler_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = arrayList[position]

        holder.binding.toolbarTitle.text = model.name
        holder.binding.description.text = model.description
        holder.binding.amount.text = "\u20b9 " + model.vouchers[0].amount
        Glide
            .with(context)
            .load(model.image)
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .into(holder.binding.image)

        holder.binding.wishlist.setOnClickListener {
          //  removeWishlist(model.product_voucher_id!!, position)

            onClickRemove.onRemoveWishlist(model.product_voucher_id!!, position,arrayList,this)

        }

        holder.binding.root.setOnClickListener {

            Log.i(TAG, "onBindViewHolder: ${Gson().toJson(model)}")
            showVoucher.showVoucher(model,position,arrayList,this)
        }


    }

    interface onClickRemoveWishList {

        fun onRemoveWishlist(
            productVoucherId: Int,
            position: Int,
            arrayList: ArrayList<VoucherModel>,
            myWishlistAdapter: MyWishlistAdapter
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = WishlistRecyclerLayoutBinding.bind(itemView)
    }



}