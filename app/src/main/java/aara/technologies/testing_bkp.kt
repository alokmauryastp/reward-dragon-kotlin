package aara.technologies

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.DenominationSelect
import aara.technologies.rewarddragon.adapter.VoucherDenominationAdapter
import aara.technologies.rewarddragon.databinding.CouponDetailsLayoutBinding
import aara.technologies.rewarddragon.model.VoucherModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.OnBonusDialogDismissInterface
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class testing_bkp {


/*
    @SuppressLint("SetTextI18n")
    private fun openDialog(model: VoucherModel) {


        val binding = CouponDetailsLayoutBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).create()
        binding!!.denominationRecycler.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        binding.toolbarTitle.text = model.name
        binding.brand.text = model.brand
        binding.validity.text = model.validity
        binding.description.text = model.description
        binding.description.movementMethod = ScrollingMovementMethod()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        binding.claimReward.setOnClickListener {

            if (binding.qtySpinner.selectedItem != null) {
                var qty: String? = binding.qtySpinner.selectedItem.toString()
                //  Log.i(TAG, "openDialog: ${model.vouchers.size}")
                claimReward(
                    model.product_voucher_id,
                    voucherAmountId,
                    amount,
                    qty!!.toInt(),
                    dialog
                )
            }

        }

        var items: ArrayList<String> = ArrayList()

        val sortedList = model.vouchers.sortedBy { it.redeemValue?.toDouble() }

        binding!!.denominationRecycler.adapter =
            VoucherDenominationAdapter(sortedList, context, object : DenominationSelect {
                override fun onSelect(
                    id: Int?,
                    amount: String?,
                    redeemValue: String?,
                    quantity: Int?,
                    voucherAmountId: Int?
                ) {

                    if (items != null) {
                        items.clear()
                    }
                    for (i in 1..quantity!!) {
                        //   print(i);
                        items.add(i.toString())
                    }

                    val adapter = ArrayAdapter(context, R.layout.simple_spinner_item2, items)
                    adapter.setDropDownViewResource(R.layout.selectable_dialog_items)
                    binding.qtySpinner.adapter = adapter

                    binding.qtySpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                // Toast.makeText(context, items[position], Toast.LENGTH_SHORT).show()
                                this@MyReward.amount = amount!!.toDouble()

                                binding.price.text =
                                    "\u20b9 ${amount.toDouble() * items[position].toDouble()}"

                                this@MyReward.voucherAmountId = voucherAmountId!!

                                discountAmount =
                                    redeemValue!!.toDouble() * items[position].toDouble()

                                binding.discountPrice.text =
                                    Html.fromHtml("<strike> ₹ $discountAmount </strike>") *//*=" <strike> \u20b9 $discountAmount </strike>"*//*


                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                        }

                }

            })


        // Log.i(TAG, "openDialog: " + items[1])


        Glide.with(context)
            .load(model.image)
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .error(R.mipmap.logo)
            .into(binding.image)
        dialog.setView(binding.root)
        dialog.setOnCancelListener {
        }
        dialog.show()
    }*/






/*    private fun claimReward(
        product_voucher_id: Int?,
        voucher_amount_id: Int?,
        redeem_value: Double,
        quantity: Int?,
        dialog: AlertDialog
    ) {
        runOnUiThread { this.dialog.show() }


        val hashmap: HashMap<String, String> = hashMapOf()
        hashmap["total_user_amount"] = binding!!.pointBalance.text.toString()
        hashmap["user_profile_id"] = sharedPrefManager.user.id.toString()
        hashmap["product_voucher_id"] = product_voucher_id.toString()
        hashmap["voucher_amount_id"] = voucher_amount_id.toString()
        hashmap["redeem_value"] = redeem_value.toString()
        hashmap["quantity"] = quantity.toString()
        hashmap["unique_code"] = sharedPrefManager.user.uniqueCode.toString()


        //      Log.i(TAG, "claimReward hashmap: $hashmap")


        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.claimReward(hashmap)
        Log.i(TAG, "claimReward:req " + Gson().toJson(call.request()))
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    try {
                        Log.i(TAG, "onResponse:1 " + Gson().toJson(response.body()))
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        //  val jsonObj = jsonObject.getJSONObject("message")
                        //   Log.i(TAG, "onResponse:claimReward ${jsonObj.getString("message")}")
                        val resCode = jsonObject.getInt("response_code")
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT)
                            .show()
                        if (resCode == 200) {


                            var isNullObj = jsonObject.isNull("reward_points_data")



                            if (isNullObj) {
                                Toast.makeText(
                                    context,
                                    jsonObject.getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                var checkObjLength =
                                    jsonObject.getJSONObject("reward_points_data").length();

                                Log.i(
                                    TAG,
                                    "onResponse: reward_points_data: $isNullObj  length $checkObjLength"
                                )
                                if (checkObjLength > 0) {
                                    getRewardPoints()
                                    showBonusDialog(jsonObject)
                                } else {
                                    Toast.makeText(
                                        context,
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            dialog.dismiss()


                        }
                    } catch (e: java.lang.Exception) {
                        Log.i(TAG, "onResponse:2 " + e.message)
                    }


                } else {

                    Toast.makeText(
                        context,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()

                }

                this@MyReward.dialog.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                this@MyReward.dialog.dismiss()
                Toast.makeText(
                    context,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()

            }

        })

    }*/



/*    var voucherAmountId by Delegates.notNull<Int>()
    var amount: Double = 0.0
    var discountAmount: Double = 0.0


    @Throws(JSONException::class)
    private fun showBonusDialog(obj: JSONObject) {
        val obj2: JSONObject = obj.getJSONObject("reward_points_data")
        Log.i(TAG, "onResponse: $obj2")
        if (obj2.length() > 0) {
            val points = obj2.getString("reward_points")
            val message = obj2.getString("reward_message")

            if (points.toInt() > 0) {
                var alert = Constant.AlertDialog2(
                    context,
                    R.style.ThemeDialogCustom,
                    points,
                    message, listener = object : OnBonusDialogDismissInterface {
                        override fun onDismiss(boolean: Boolean) {

                        }
                    })
                alert.show()

            }

            Log.i(TAG, "onResponse: reward_points $points reward_message $message")

        } else {
            Log.i(TAG, "onResponse: reward_points null")

        }
    }*/
}