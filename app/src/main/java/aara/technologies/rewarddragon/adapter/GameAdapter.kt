package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.activities.WebViewActivity
import aara.technologies.rewarddragon.databinding.PlayGameTimeLayoutBinding
import aara.technologies.rewarddragon.model.GameModel
import aara.technologies.rewarddragon.utils.Constant.gameClickable
import aara.technologies.rewarddragon.utils.OnRefresh
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson

class GameAdapter(
    var arrayList: ArrayList<GameModel>,
    var context2: Activity,
    var onRefresh: OnRefresh
) :
    RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    var context: Activity = context2;


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_adapter, parent, false)
        return ViewHolder(view)
    }
    private  val TAG = "GameAdapter"
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]

        holder.parent.setOnClickListener {

            openDialog(model)



        }
        Glide
            .with(context)
            .load(model.logo)
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(300, 300))
            .fitCenter()
            .into(holder.image)

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

    @SuppressLint("SetTextI18n")
    private fun openDialog(model: GameModel) {
     //   Log.i(TAG, "onBindViewHolder: "+Gson().toJson(model))
        val binding = PlayGameTimeLayoutBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.toolbarTitle.text = model.title
//        binding.purpose.text = model.purpose
//        binding.benefits.text = model.benefits
        binding.bonusPoints.text = model.points.toString() + " Points"
        Glide
            .with(context)
            .load(model.logo)
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .error(R.mipmap.logo)
            .into(binding.image)
        binding.play.setOnClickListener {
            if (gameClickable) {
                dialog.dismiss()
                println(model.game_url)
                context.startActivityForResult(
                    Intent(
                        context,
                        WebViewActivity::class.java
                    )
                        .putExtra("link", model.game_url)
                        .putExtra("gameId", model.id), 454
                )
            } else {
                Toast.makeText(context, "You can play after next available time", Toast.LENGTH_LONG)
                    .show()
            }
        }
        dialog.setView(binding.root)
        dialog.setOnCancelListener {
            onRefresh.refresh()
        }
        dialog.show()
    }

}