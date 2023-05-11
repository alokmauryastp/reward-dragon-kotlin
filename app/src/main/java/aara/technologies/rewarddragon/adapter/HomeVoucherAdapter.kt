package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.CouponDetailsLayoutBinding
import aara.technologies.rewarddragon.model.VoucherModel
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class HomeVoucherAdapter(var arrayList: ArrayList<VoucherModel>, var context: Context) :
    RecyclerView.Adapter<HomeVoucherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_voucher_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]


        Glide
            .with(context)
            .load(model.image)
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .into(holder.image)

        holder.parent.setOnClickListener{
            openDialog(model)
        }


    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var parent: ConstraintLayout

        init {
            image = itemView.findViewById(R.id.image)
            parent = itemView.findViewById(R.id.parent)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun openDialog(model: VoucherModel) {
        val binding = CouponDetailsLayoutBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).create()
        binding.toolbarTitle.text = model.name
        binding.brand.text = model.brand
        binding.validity.text = model.validity
        binding.price.text = "\u20b9 "+model.vouchers[0].amount
        binding.description.text = model.description
        Glide
            .with(context)
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
    }

}