package aara.technologies.rewarddragon.utils

//import nl.dionsegijn.konfetti.core.Party
//import nl.dionsegijn.konfetti.models.Size
import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.DenominationSelect
import aara.technologies.rewarddragon.adapter.VoucherDenominationAdapter
import aara.technologies.rewarddragon.databinding.CouponDetailsLayoutBinding
import aara.technologies.rewarddragon.model.ConcernModel
import aara.technologies.rewarddragon.model.VoucherModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.properties.Delegates


object Constant {

    private const val TAG = "Constant"

    const val NOTIFICATION_ID = 100
    const val NOTIFICATION_ID_BIG_IMAGE = 101
    const val PUSH_NOTIFICATION = "pushNotification"


    var token: String = ""

    var loginRewardObject: JSONObject? = null
    var loginWelcomeObject: JSONObject? = null
    var androidId: String = ""


    var gameClickable: Boolean = false

    @kotlin.jvm.JvmField
    var REWARDMESSAGE: String = "reward_message"

    @kotlin.jvm.JvmField
    var REWARDPOINTS: String = "reward_points"

    @JvmField
    var fromPlayVideoActivity: Boolean = false

    @JvmField
    var bonusPoint: String? = null

    @JvmField
    var bonusMessage: String? = null


    @JvmField
    val openList: ArrayList<ConcernModel> = ArrayList()

    lateinit var onRefresh: OnRefresh

    @JvmField
    val closeList: ArrayList<ConcernModel> = ArrayList()

    @JvmField
     var baseUrl = "http://api.reward-dragon.com:8000/"
//      var baseUrl = "http://demoserver.aaratechnologies.in:8000/"
//    var baseUrl = "http://45.64.156.214:8000/"

    @JvmField
    var userType = "userType"

    @JvmField
    var manager = "2"

    @JvmField
    var user = "1"

    @JvmField
    var width = 0

    fun loadAvatarImage(context: Context, imageView: ImageView) {
        //   println(SharedPrefManager.getInstance(context)!!.user.avatarImage)
        Glide.with(context)
            .load(SharedPrefManager.getInstance(context)!!.user.avatarImage)
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .error(R.mipmap.logo)
            .into(imageView)
    }

    fun getYouTubeId(youTubeUrl: String): String? {
        val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(youTubeUrl)
        return if (matcher.find()) {
            matcher.group()
        } else {
            null
        }
    }


    class AlertDialog3(
        context: Context,
        theme_Translucent_NoTitleBar: Int,
        listener: getDateInterface
    ) : Dialog(context, theme_Translucent_NoTitleBar) {

        var listener: getDateInterface? = listener;
        var changeStatus: Boolean = false
        var selectedDate: String? = null


        companion object {
            var getDateBtn: Button? = null
            var clearBtn: Button? = null
            var calendarView: CalendarView? = null
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.calenderdialog)
            setCanceledOnTouchOutside(true)
            setCancelable(true)

            calendarView = findViewById(R.id.calendarView)
            getDateBtn = findViewById(R.id.button6)
            clearBtn = findViewById(R.id.button5)


            getDateBtn?.setOnClickListener {
                if (!changeStatus) {
                    var date = calendarView?.date
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val selectedDate: String = sdf.format(Date(date!!))
                    Log.i(TAG, "onCreate: $selectedDate")
                    this.selectedDate = selectedDate
                }
                listener!!.getData(this.selectedDate!!, "DATE")
                dismiss()
            }

            clearBtn?.setOnClickListener {
                listener!!.getData("", "CLEAR")
                dismiss()
            }

            calendarView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
                var date = "${month + 1}/${dayOfMonth}/$year"
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val selectedDate: String = sdf.format(Date(date!!))
                Log.i(TAG, "onCreate setOnDateChangeListener: $selectedDate")
                this.selectedDate = selectedDate
                changeStatus = true
            }
        }
    }

    class AlertDialog2(
        context: Context,
        theme_Translucent_NoTitleBar: Int,
        points: String,
        message: String,
        listener: OnBonusDialogDismissInterface
    ) :
        Dialog(context, theme_Translucent_NoTitleBar) {
        var drawable: Drawable? = null
        var drawable2: Drawable? = null
        var drawable3: Drawable? = null
        var drawable4: Drawable? = null
        var drawable5: Drawable? = null

        var listener: OnBonusDialogDismissInterface? = listener;
        val points: String = points
        val message: String = message;


        companion object {
            var apply: Button? = null
            var name: TextInputEditText? = null
            lateinit var drawableShape: Shape.DrawableShape
            lateinit var drawableShape2: Shape.DrawableShape
            lateinit var drawableShape3: Shape.DrawableShape
            lateinit var drawableShape4: Shape.DrawableShape
            lateinit var drawableShape5: Shape.DrawableShape
            lateinit var konfettiView: KonfettiView

            //  public static TextView city;
            var state: TextInputEditText? = null
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.bonus_alert)

            setCanceledOnTouchOutside(true)
            setCancelable(true)
            konfettiView = findViewById(R.id.viewKonfetti)
            konfettiView.bringToFront()
            konfettiView.visibility = View.VISIBLE

            drawable = context.getDrawable(R.drawable.ic_firecracker1)
            drawableShape = Shape.DrawableShape(drawable!!, true)
            drawable2 = context.getDrawable(R.drawable.ic_firecrackers2)
            drawableShape2 = Shape.DrawableShape(drawable2!!, true)
            drawable3 = context.getDrawable(R.drawable.ic_fireworks3)
            drawableShape3 = Shape.DrawableShape(drawable3!!, true)
            drawable4 = context.getDrawable(R.drawable.ic_fireworks4)
            drawableShape4 = Shape.DrawableShape(drawable3!!, true)
            drawable5 = context.getDrawable(R.drawable.ic_fireworks5)
            drawableShape5 = Shape.DrawableShape(drawable3!!, true)


            var points = findViewById<TextView>(R.id.points)
            var message = findViewById<TextView>(R.id.messge_txt)
            points.text = this.points
            message.text = this.message

            konfettiView.start(
                Party(
                    speed = -1f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    spread = 360,
                    size = listOf(Size.LARGE),
                    fadeOutEnabled = true,
                    timeToLive = 2000,
                    delay = 300,
                    shapes = listOf(
                        drawableShape, drawableShape2, drawableShape3, drawableShape4,
                        drawableShape5
                    ),
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).perSecond(1000),
                    position = Position.Relative(0.5, 0.3)
                )
            )

            val handler = Handler()
            val runnable = Runnable {
                if (isShowing) {
                    dismiss()
                }
            }
            setOnDismissListener {
                handler.removeCallbacks(runnable);
                listener!!.onDismiss(true)

            }

            handler.postDelayed(runnable, 3000);
        }
    }


    var voucherAmountId by Delegates.notNull<Int>()
    var amount: Double = 0.0
    var discountAmount: Double = 0.0


    interface RedeemRewardCallback {

        fun onRedeemed(str: Boolean)
    }

    @SuppressLint("SetTextI18n")
    fun showCouponDialog(
        model: VoucherModel,
        context: Activity,
        testMethodCallback: RedeemRewardCallback
    ) {
        val binding = CouponDetailsLayoutBinding.inflate(LayoutInflater.from(context))
        var dialog = AlertDialog.Builder(context).create()
        this.dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)
        var sharedPrefManager = SharedPrefManager.getInstance(context)

        binding.toolbarTitle.text = model.name
        binding.brand.text = model.brand
        binding.validity.text = model.validity
        binding.description.text = model.description
        binding.description.movementMethod = ScrollingMovementMethod()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val sortedList = model.vouchers.sortedBy { it.redeemValue?.toDouble() }

        var items: ArrayList<String> = ArrayList()

        binding.denominationRecycler.adapter = VoucherDenominationAdapter(
            sortedList,
            context,
            object : DenominationSelect {
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
                    val adapter =
                        ArrayAdapter(context, R.layout.simple_spinner_item2, items)
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
                                this@Constant.amount = amount!!.toDouble()

                                binding.price.text =
                                    "\u20b9 ${amount.toDouble() * items[position].toDouble()}"

                                this@Constant.voucherAmountId = voucherAmountId!!

                                discountAmount =
                                    redeemValue!!.toDouble() * items[position].toDouble()


                                binding.discountPrice.text =
                                    Html.fromHtml("<strike> ₹ $discountAmount </strike>")

                                //binding.discountPrice.text = Html.fromHtml("<strike> ₹ $discountAmount </strike>") =" <strike> \u20b9 $discountAmount </strike>"
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }
            })

        binding.claimReward.setOnClickListener {


            var qty: String? = binding.qtySpinner.selectedItem.toString()
            Log.i(TAG, "openDialog: ${model.vouchers.size}")
            claimReward(
                model.product_voucher_id,
                voucherAmountId,
                amount,
                qty!!.toInt(),
                this.dialog,
                context,
                sharedPrefManager,
                testMethodCallback,
                dialog
            )
        }
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
    }


    private fun claimReward(
        product_voucher_id: Int?,
        voucher_amount_id: Int?,
        redeem_value: Double,
        quantity: Int?,
        customLoader: CustomLoader,
        context: Context,
        sharedPrefManager: SharedPrefManager?,
        testMethodCallback: RedeemRewardCallback,
        alertDialog: AlertDialog
    ) {
        customLoader.show()
        val hashmap: java.util.HashMap<String, String> = hashMapOf()
        hashmap["user_profile_id"] = sharedPrefManager?.user?.id.toString()
        hashmap["product_voucher_id"] = product_voucher_id.toString()
        hashmap["voucher_amount_id"] = voucher_amount_id.toString()
        hashmap["redeem_value"] = redeem_value.toString()
        hashmap["total_user_amount"] = sharedPrefManager?.getString(SharedPrefManager.KEY_POINT_BALANCE)!!
        hashmap["quantity"] = quantity.toString()
        hashmap["unique_code"] = sharedPrefManager!!.user.uniqueCode.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.claimReward(hashmap)
        Log.i(TAG, "claimReward:req " + Gson().toJson(call.request()))
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.i(TAG, "onResponse rd: " + Gson().toJson(response.body()))

                alertDialog.dismiss()
                customLoader.dismiss()
                print(response.code().toString())
                print(response.body().toString())
                if (response.code() == 200) {

                    try {
                        Log.i(TAG, "onResponse: " + Gson().toJson(response.body()))
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        //  val jsonObj = jsonObject.getJSONObject("message")
                        //   Log.i(TAG, "onResponse:claimReward ${jsonObj.getString("message")}")
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {

                            val map3: java.util.HashMap<String, String> = java.util.HashMap()
                            map3["user_profile_id"] = sharedPrefManager!!.user.id.toString()


                            var isNullObj = jsonObject.isNull("reward_points_data")

                            if (isNullObj) {
                                testMethodCallback.onRedeemed(false)
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

                                    AlertDialog.Builder(context)
                                        .setIcon(R.drawable.ic_baseline_check_circle_24)
                                        .setTitle("Success!")
                                        .setMessage("Voucher Purchase Successful, details available on email and My Coupons section")
                                        .setPositiveButton("OK") { _, _ ->
                                            testMethodCallback.onRedeemed(true)
                                            // viewModel.getRewardPoints(map3)
                                            showBonusDialog(jsonObject, context)
                                        }
                                        .show()

                                } else {
                                    testMethodCallback.onRedeemed(false)
                                    Toast.makeText(
                                        context,
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }



                            customLoader.dismiss()


                        }
                    } catch (e: java.lang.Exception) {

                        Log.i(TAG, "onResponse: " + e.message)
                    }


                } else {

                    Toast.makeText(
                        context,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.i(TAG, "onFailure: ${t.message}  ")
                customLoader.dismiss()
                alertDialog.dismiss()

                Log.i(TAG, "onFailure: ${t.message}  ")

                Toast.makeText(
                    context,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()

            }

        })

    }

    @Throws(JSONException::class)
    private fun showBonusDialog(obj: JSONObject, context: Context) {
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
    }


    lateinit var dialog: CustomLoader


    fun getStringFormatted(dateString: String): String {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(Date(dateString.replace("-".toRegex(), "/")))
    }

    fun randomFloatBetween(min: Int, max: Int): Float {
        val r = Random()
        return min + r.nextFloat() * (max - min)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysToGo(): String {
        val today: LocalDate = LocalDate.now()
        val endOfMonth: LocalDate = today.withDayOfMonth(today.lengthOfMonth())
        return ChronoUnit.DAYS.between(today, endOfMonth).toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysCounter(date: String): String {
        val today: LocalDate = LocalDate.now()
        val end = LocalDate.parse(date)
        return ChronoUnit.DAYS.between(end, today).toString()
    }

    fun isValidEmail(str: String) =
        !TextUtils.isEmpty(str) && !Patterns.EMAIL_ADDRESS.matcher(str).matches()

    fun getTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault())
        return sdf.format(Date())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convert12to24format(time: String): String {
        return LocalTime.parse( // Class representing a time-of-day value without a date and without a time zone.
            time,  // Your `String` input text.
            DateTimeFormatter.ofPattern( // Define a formatting pattern to match your input text.
                "hh:mm:ss a",
                Locale.US // `Locale` determines the human language and cultural norms used in localization. Needed here to translate the `AM` & `PM` value.
            ) // Returns a `DateTimeFormatter` object.
        ) // Return a `LocalTime` object.
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"))

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convert24to12format(time: String): String {
        return LocalTime.parse( // Class representing a time-of-day value without a date and without a time zone.
            time,  // Your `String` input text.
            DateTimeFormatter.ofPattern( // Define a formatting pattern to match your input text.
                "HH:mm:ss",
                Locale.US // `Locale` determines the human language and cultural norms used in localization. Needed here to translate the `AM` & `PM` value.
            ) // Returns a `DateTimeFormatter` object.
        ) // Return a `LocalTime` object.
            .format(DateTimeFormatter.ofPattern("hh:mm a"))
    }

    fun getTime(time: String): String {
        return when (time) {
            "Today" -> "today"
            "Time Period" -> "time_period_all"
            "Yesterday" -> "yesterday"
            "WTD" -> "wtd"
            "MTD" -> "mtd"
            "YTD" -> "ytd"
            "ALL" -> "time_period_all"
            else -> {
                "time_period_all"
            }
        }
    }

    fun convertHHSStoSeconds(time: String): Int {
        println(time)
        //time in mm:ss
        val units = time.split(":").toTypedArray() //will break the string up into an array
        val minutes = units[0].toInt() //first element
        val seconds = units[1].toInt() //second element
        println(minutes)
        println(seconds)
        return 60 * minutes + seconds
    }

    fun getMoodText(point: Int): String {
        when (point) {
            5 -> {
                return "Awesome"
            }
            4 -> {
                return "Good"
            }
            3 -> {
                return "Okay"
            }
            2 -> {
                return "Upset"
            }
            1 -> {
                return "Stressed"
            }
        }
        return ""
    }

    fun getGroup(time: String): String {
        return when (time) {
            "Group" -> "group_all"
            "Top 10" -> "top_10"
            "Bottom 10" -> "bottom_10"
            "ALL" -> "group_all"
            else -> {
                "top_10"
            }
        }
    }

    fun getCurrentTime(): String {
        val c = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val amPm: String

        when {
            hour == 0 -> {
                hour += 12
                amPm = "AM"
            }
            hour == 12 -> amPm = "PM"
            hour > 12 -> {
                hour -= 12
                amPm = "PM"
            }
            else -> amPm = "AM"
        }
        return "$hour:$minute $amPm"
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun test(): NavigableMap<Long, String> {
        val suffixes: NavigableMap<Long, String> = TreeMap()
        suffixes[1_000L] = "k"
        suffixes[1_000_000L] = "M"
        suffixes[1_000_000_000L] = "G"
        suffixes[1_000_000_000_000L] = "T"
        suffixes[1_000_000_000_000_000L] = "P"
        suffixes[1_000_000_000_000_000_000L] = "E"
        return suffixes
    }

    fun format(value: Long): String {
        //Long.MIN_VALUE == -Long.MIN_VALUE, so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1)
        if (value < 0) return "-" + format(-value)
        if (value < 1000) return value.toString() //deal with easy case
        val (divideBy, suffix) = test().floorEntry(value)!!
        val truncated = value / (divideBy / 10) //the number part of the output times 10
        val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
        return if (hasDecimal) (truncated / 10.0).toString() + suffix else (truncated / 10).toString() + suffix
    }


}

