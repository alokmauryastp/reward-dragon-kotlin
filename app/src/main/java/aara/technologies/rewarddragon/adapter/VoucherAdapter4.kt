package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.VoucherModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.testing.Model.HomePageVoucherRes
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Response

class VoucherAdapter4(
    var arrayList: List<VoucherModel>,
    var context: Context,
    var redeemReward: RedeemReward
) :
    RecyclerView.Adapter<VoucherAdapter4.ViewHolder>() {


    interface RedeemReward {

        fun onRedeem(model: VoucherModel)
    }

    var dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

    private  val TAG = "VoucherAdapter"

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
            .load(model.image)
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .into(holder.image)

        holder.parent.setOnClickListener {
            Log.i(TAG, "onBindViewHolder: ${Gson().toJson(model)}")
            redeemReward.onRedeem(model)


            // openDialog(model)
        }

        holder.wishlist.setOnClickListener {
            if (model.is_added_wishlist == 1) {
                removeWishlist(model.product_voucher_id!!, position)
                model.is_added_wishlist = 0
            } else {
                addToWishlist(model.product_voucher_id!!, position)
                model.is_added_wishlist = 1
            }
        }

        if (model.is_added_wishlist == 1) {
            holder.wishlist.setImageDrawable(context.resources.getDrawable(R.drawable.ic_wishlist2))
        } else {
            holder.wishlist.setImageDrawable(context.resources.getDrawable(R.drawable.ic_wishlist))
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
        }
    }


    private fun addToWishlist(productVoucherId: Int, position: Int) {
        dialog.show()
        val hashmap: HashMap<String, Any> = hashMapOf()
        hashmap["user_profile_id"] = SharedPrefManager(context).user.id.toString()
        hashmap["product_voucher_id"] = productVoucherId
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.addToWishlist(hashmap)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onResponse(
                call: retrofit2.Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                println("addToWishlist")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        notifyItemChanged(position)
//                        image.setImageDrawable(context.resources.getDrawable(R.drawable.ic_wishlist2))
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }
                dialog.dismiss()
            }

            override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                dialog.dismiss()
                println(t.message)

            }
        })

    }

    private fun removeWishlist(productVoucherId: Int, position: Int) {
        dialog.show()
        val hashmap: HashMap<String, Any> = hashMapOf()
        hashmap["user_profile_id"] = SharedPrefManager(context).user.id.toString()
        hashmap["product_voucher_id"] = productVoucherId
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.removeWishlist(hashmap)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onResponse(
                call: retrofit2.Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                println("removeWishlist")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        notifyItemChanged(position)
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }
                dialog.dismiss()
            }

            override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                dialog.dismiss()
                println(t.message)

            }
        })

    }

}