package aara.technologies.rewarddragon.manager

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.activities.WebViewVertActivity
import aara.technologies.rewarddragon.adapter.AvatarAdapter
import aara.technologies.rewarddragon.adapter.AvatarAdapter.Companion.PICK_IMAGE
import aara.technologies.rewarddragon.databinding.ActivityMyProfileBinding
import aara.technologies.rewarddragon.model.AvatarModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.*
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val TAG = "MyProfile"

class MyProfile : AppCompatActivity(), OnRefresh {

    var binding: ActivityMyProfileBinding? = null
    lateinit var progress: CustomLoader
    var context: Activity = this
    var file: File? = null
    var sharedPrefManager: SharedPrefManager? = null;

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        sharedPrefManager = SharedPrefManager.getInstance(context)

        if (sharedPrefManager!!.user.roleId != 1) {
            binding!!.managerNamell.visibility = View.GONE
        }


        binding!!.toolbar.toolbarTitle.text =
            sharedPrefManager!!.user.companyName
        com.bumptech.glide.Glide.with(context).load(
            sharedPrefManager!!
                .getString(aara.technologies.rewarddragon.utils.SharedPrefManager.Companion.KEY_COMPANY_IMAGE)!!
        ).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }

        binding!!.raiseConcern.setOnClickListener {
            if (sharedPrefManager!!.user.roleId == 1) {
                startActivity(Intent(context, MyConcern::class.java))
            } else {
                startActivity(Intent(context, MyConcernManager::class.java))
            }
        }

        progress = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.editProfile.setOnClickListener {
//            (context as Dashboard).addFragment(EditProfile())
            editProfileName()
        }

        binding!!.fullName.text =
            sharedPrefManager!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName
        binding!!.displayName.text =
            sharedPrefManager!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName

        val updatedAt = sharedPrefManager!!.user.userProfileUpdatedAt.toString()
        if (updatedAt.isNotEmpty()) {
            binding!!.time.text = "Data last updated on $updatedAt"
        }

        binding!!.companyName.text = sharedPrefManager!!.user.companyName
        binding!!.orgUniquecode.text =
            sharedPrefManager!!.user.uniqueCode
        binding!!.email.text = sharedPrefManager!!.user.email
        binding!!.mobile.text = sharedPrefManager!!.user.mobileNo
        binding!!.gender.text = sharedPrefManager!!.user.gender
        binding!!.designation.text = sharedPrefManager!!.user.designation
        binding!!.baseLocation.text = sharedPrefManager!!.user.baseLocation
        binding!!.teamName.text = sharedPrefManager!!.user.teamName
        binding!!.managerName.text = sharedPrefManager!!.user.managerName
        binding!!.defaultLanguage.text =
            sharedPrefManager!!.user.defaultLanguage
        binding!!.memberSince.text = sharedPrefManager!!.user.memberSince
        //  Log.i(TAG, "onCreate: last activie on "+sharedPrefManager!!.user.lastActiveOn)
        binding!!.lastActiveOn.text = sharedPrefManager!!.user.lastActiveOn

        //  println("avatar image")
        //  println(sharedPrefManager!!.user.avatarImage)
        binding!!.editAvatar.setOnClickListener {
            showDialog()
        }


        binding!!.privacyPolicy.setOnClickListener {
            privacyPolicy()
        }

//        return binding!!.root
    }


    private fun privacyPolicy() {
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getPrivacyPolicy()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {


                Log.i(TAG, "onResponse: ${response.body()}")

                if (response.code() == 200) {

                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")

                        if (resCode == 200) {

                            var array = jsonObject.getJSONArray("privacy_policy_data")


                            if (array.length() > 0) {

                                for (i in 0 until array.length()) {
                                    val item = array.getJSONObject(i)
                                    Log.i(
                                        TAG,
                                        "onResponse: termcondition " + item
                                            .getString("url_link")
                                    )

                                    startActivity(
                                        Intent(context, WebViewVertActivity::class.java).putExtra(
                                            "link",
                                            item.getString("url_link")
                                        )
                                    )
                                }

                            }


                        } else {
                            Toast.makeText(
                                context,
                                jsonObject.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    } catch (e: Exception) {

                        Log.i(TAG, "onResponse:error ${e.message}")


                    }
                }

            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                TODO("Not yet implemented")
            }


        })

    }


    var recycler: RecyclerView? = null
    var dialog: Dialog? = null
    var dialog1: Dialog? = null
    var llparent: LinearLayout? = null


    private fun showDialog() {
        dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.avatar_dialog)
        dialog?.setOnCancelListener {
            println("dialog cancel")
            loadAvatarImage(context, binding!!.imageView)
        }
        recycler = dialog?.findViewById(R.id.recyclerView)
        llparent = dialog?.findViewById(R.id.llparent)
        recycler?.layoutManager =
            GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        getAvatarImageList(dialog!!, llparent)
    }

    private fun editProfileName() {

        dialog1 = Dialog(context, R.style.Theme_Dialog)
        dialog1?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1?.setContentView(R.layout.edit_profile_layout)
        val firstName = dialog1?.findViewById(R.id.first_name) as TextView
        val lastName = dialog1?.findViewById(R.id.last_name) as TextView
        val update = dialog1?.findViewById(R.id.update) as TextView
        firstName.text = sharedPrefManager!!.user.firstName.toString()
        lastName.text = sharedPrefManager!!.user.lastName.toString()
        update.setOnClickListener {
            if (firstName.text.toString().isEmpty()) {
                firstName.requestFocus()
                firstName.error = "Required"
            } else if (lastName.text.toString().isEmpty()) {
                lastName.requestFocus()
                lastName.error = "Required"
            } else (
                    updateProfile(firstName.text.toString(), lastName.text.toString())
                    )
        }
        dialog1?.show()
    }


    private fun updateProfile(firstName: String, lastName: String) {
        progress.show()
        val map: HashMap<String, String> = hashMapOf()
        map["first_name"] = firstName
        map["last_name"] = lastName
        println(map)
        println(sharedPrefManager!!.user.id.toString())
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.updateProfile(
            sharedPrefManager!!.user.id.toString(),
            map
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                println(response.code())
                println("updateProfile " + response.body())
                if (response.code() == 200) {

                    binding!!.displayName.text = "$firstName $lastName"
                    binding!!.fullName.text = "$firstName $lastName"
                    sharedPrefManager!!
                        .setString(SharedPrefManager.KEY_USERFIRSTNAME, firstName)
                    sharedPrefManager!!
                        .setString(SharedPrefManager.KEY_USERLASTNAME, lastName)
                    val obj = JSONObject(Gson().toJson(response.body()))

                    showBonusDialog(obj)

                    sharedPrefManager!!.setString(
                        SharedPrefManager.KEY_USER_PROFILE_UPDATED_AT,
                        obj.getString("user_profile_updated_at")
                    )
                    dialog1?.dismiss()
//                    (context as Dashboard).addFragment(MyProfile())
                    // startActivity(Intent(context, MyProfile::class.java))
                }
                progress.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progress.dismiss()
                println(t.message)
            }
        })

    }

    private fun getAvatarImageList(dialog: Dialog, llparent: LinearLayout?) {
        progress.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getAvatarImageList()
        call.enqueue(object : Callback<JsonObject>, CellClickListener {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getAvatarImageList")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val turnsType = object : TypeToken<ArrayList<AvatarModel>>() {}.type
                    var list: ArrayList<AvatarModel> = ArrayList()

                    list = Gson().fromJson(obj.getJSONArray("avatar_images").toString(), turnsType)

                    list.add(
                        0, AvatarModel("", 0, "", 1, "")
                    )
                    println(list.size)
                    recycler?.adapter =
                        AvatarAdapter(list, context, this, this@MyProfile)
                    dialog.show()
                }
                progress.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progress.dismiss()
                Log.i(TAG, "onFailure: getAvatarImageList " + t.message)
                println(t.message)
            }

            override fun onCellClickListener(position: Int, model: AvatarModel) {

                if (position == 0) {

                    when {
                        ContextCompat.checkSelfPermission(
                            this@MyProfile,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            val intent = Intent()
                            intent.type = "image/*"
                            intent.action = Intent.ACTION_GET_CONTENT
                            context.startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                PICK_IMAGE
                            )

                        }

                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this@MyProfile,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) -> {
                            showSnackbar(
                                llparent!!,
                                getString(R.string.permission_required),
                                Snackbar.LENGTH_INDEFINITE,
                                "ok"
                            ) {
                                requestPermissionLauncher.launch(
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                            }
                        }

                        else -> {
                            requestPermissionLauncher.launch(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        }
                    }

                } else {
                    updateAvatarImage(model.id)
                }
            }
        })
    }


    private fun updateAvatarImage(avatarId: Int) {
        dialog?.show()
        val map: HashMap<String, Any> = hashMapOf()
        map["user_profile_id"] = sharedPrefManager!!.user.id.toString()
        map["avatar_image_id"] = avatarId
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.updateAvatarImage(map)
        println(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getWellBeingList")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))

                    showBonusDialog(obj)

                    val image = obj.getJSONObject("data").getString("image")
                    var updated_at = obj.getJSONObject("data").getString("updated_at")
                    sharedPrefManager!!
                        .setString(SharedPrefManager.KEY_AVATAR_IMAGE, image)
                    sharedPrefManager!!.setString(
                        SharedPrefManager.KEY_USER_PROFILE_UPDATED_AT,
                        updated_at
                    )
                    Toast.makeText(context, "Profile Updated!", Toast.LENGTH_LONG).show()
                    refresh()
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                dialog?.dismiss()
            }
        })
    }

    private fun uploadProfileImage(user_profile_id: String, image: File) {
        dialog?.show()

        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "user_image", image.name,
                RequestBody.create(
                    MediaType.parse("application/octet-stream"),
                    image
                )
            )
            .addFormDataPart("user_profile_id", user_profile_id)
            .build()

        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.uploadProfileImage(requestBody)
        Log.i(
            TAG,
            "uploadProfileImage: req $user_profile_id file $image \n ${Gson().toJson(call.request())}"
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                Log.i(TAG, "onResponse: " + response.body() + " --> code " + response)
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    Log.i(TAG, "uploadProfileImage: ")

                    showBonusDialog2(obj)

                    val image = obj.getString("user_image")
                    val updated_at = obj.getString("user_profile_updated_at")
                    //  Log.i(TAG, "onResponse: "+image)
                    //  val image = obj.getJSONObject("user_image").getString("image")
                    sharedPrefManager!!
                        .setString(SharedPrefManager.KEY_AVATAR_IMAGE, image)
                    sharedPrefManager!!.setString(
                        SharedPrefManager.KEY_USER_PROFILE_UPDATED_AT,
                        updated_at
                    )

                    Toast.makeText(context, "Profile Updated!", Toast.LENGTH_LONG).show()
                    refresh()
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.i(TAG, "onFailure: uploadProfileImage " + t.message)
                println(t.message)
                dialog?.dismiss()
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            var uri: Uri? = data!!.data
            val filePath = getPathFromUri(this, uri!!)

            Log.i(TAG, "onActivityResult: $filePath  intent uri $uri")

            file = File(filePath)
            uploadProfileImage(sharedPrefManager!!.user.id.toString(), file!!)
        }
        Log.i(TAG, "onActivityResult: uri $data   $resultCode")

    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                context.startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    PICK_IMAGE
                )
            } else {
                Log.i("Permission: ", "Denied")
            }
        }


    @Throws(JSONException::class)
    private fun showBonusDialog(obj: JSONObject) {
        val obj2: JSONObject = obj.getJSONObject("reward_points_data")
        Log.i(TAG, "onResponse: $obj2")
        if (obj2.length() > 0) {
            val points = obj2.getString("reward_points")
            val message = obj2.getString("reward_message")

            if (points.toInt() > 0) {
                var alert = Constant.AlertDialog2(
                    this@MyProfile,
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


    @Throws(JSONException::class)
    private fun showBonusDialog2(obj: JSONObject) {
        val obj2: JSONObject = obj.getJSONObject("data")
        Log.i(TAG, "onResponse: $obj2")
        if (obj2.length() > 0) {
            val points = obj2.getString("reward_points")
            val message = obj2.getString("reward_message")

            if (points.toInt() > 0) {
                var alert = Constant.AlertDialog2(
                    this@MyProfile,
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

    override fun onResume() {
        super.onResume()
        loadAvatarImage(applicationContext, binding!!.imageView)
    }

    override fun refresh() {
        dialog?.dismiss()
        loadAvatarImage(context, binding!!.imageView)
        val updatedAt =
            sharedPrefManager!!.user.userProfileUpdatedAt.toString()
        if (updatedAt.isNotEmpty()) {
            binding!!.time.text = "Data last updated on $updatedAt"
        }
    }

    override fun onStop() {
        super.onStop()
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            //  Log.i(TAG, "getPathFromUri: working1")
            if (isExternalStorageDocument(uri)) {
                //  Log.i(TAG, "getPathFromUri: isExternalStorageDocument")
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                Log.i(TAG, "getPathFromUri: isDownloadsDocument")
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)

            } else if (isMediaDocument(uri)) {

                //     Log.i(TAG, "getPathFromUri: isMediaDocument")
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]

                //   Log.i(TAG, "getPathFromUri: type " + type)
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    //  Log.i(TAG, "getPathFromUri: image" + contentUri)
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                Log.i(TAG, "getPathFromUri: selection " + selectionArgs[0])
                Log.i(
                    TAG,
                    "getPathFromUri: return " + getDataColumn(
                        context,
                        contentUri,
                        selection,
                        selectionArgs
                    )
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            var cursor: Cursor? = null
            return try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(uri, proj, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor!!.moveToFirst()
                Log.i(TAG, "getPathFromUri: working2 ${cursor!!.getString(column_index)}")

                cursor!!.getString(column_index)
            } finally {
                cursor?.close()
            }
        }
        return ""
    }

    fun showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }.show()
        } else {
            snackbar.show()
        }
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}