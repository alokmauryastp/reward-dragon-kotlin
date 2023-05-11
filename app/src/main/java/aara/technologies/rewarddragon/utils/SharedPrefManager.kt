package aara.technologies.rewarddragon.utils

import aara.technologies.rewarddragon.model.User
import android.content.Context
import android.content.SharedPreferences


//here for this class we are using a singleton pattern
class SharedPrefManager(private var mCtx: Context) {
    //method to let the user login
    //this method will store the user data in shared preferences
    fun userLogin(user: User) {
        val sharedPreferences: SharedPreferences =
            mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_ID, user.id!!)
        editor.putString(KEY_UNIQUE_CODE, user.uniqueCode)
        editor.putString(KEY_USERFIRSTNAME, user.firstName)
        editor.putString(KEY_USERLASTNAME, user.lastName)
        editor.putString(KEY_EMAIL, user.email)
        editor.putString(KEY_MOBILE, user.mobileNo)
        editor.putInt(KEY_ROLE_ID, user.roleId!!)
        editor.putString(KEY_ROLE_NAME, user.roleName)
        editor.putString(KEY_COMPANY_NAME, user.companyName)
        editor.putString(KEY_COMPANY_IMAGE, user.organization!!.organization)
        editor.putString(KEY_ORGANIZATION_CODE, user.organizationCode)
        editor.putString(KEY_GENDER, user.gender)
        editor.putString(KEY_DESIGNATION, user.designation)
        editor.putString(KEY_BASE_LOCATION, user.baseLocation)
        editor.putString(KEY_TEAM_ID, user.teamId.toString())
        editor.putString(KEY_TEAM_NAME, user.teamName)
        editor.putString(KEY_MANAGER_ID, user.managerId.toString())
        editor.putString(KEY_MANAGER_NAME, user.managerName)
        editor.putString(KEY_DEFAULT_LANGUAGE, user.defaultLanguage)
        editor.putString(KEY_MEMBER_SINCE, user.memberSince)
        editor.putString(KEY_LAST_ACTIVE_ON, user.lastActiveOn)
        editor.putBoolean(KEY_IS_VERIFIED, user.isVerifiedByAdmin!!)
        editor.putString(KEY_FIREBASE_TOKEN, user.firebaseToken)
        editor.putString(KEY_CREATE_AT, user.createdAt)
        editor.putString(KEY_UPDATE_AT, user.updatedAt)
        editor.putString(KEY_TOKEN, user.token)
        editor.putString(KEY_AVATAR_IMAGE, user.avatarImage)
        editor.putString(KEY_USER_PROFILE_UPDATED_AT, user.userProfileUpdatedAt)
        editor.apply()
    }

    //this method will checker whether user is already logged in or not
    val isLoggedIn: Boolean
        get() {
            val sharedPreferences: SharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_USERFIRSTNAME, null) != null
        }

    fun getString(key: String): String? {
        val sharedPreferences: SharedPreferences =
            mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    //this method will give the logged in user
    val user: User
        get() {
            val sharedPreferences: SharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return User(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_UNIQUE_CODE, null),
                sharedPreferences.getString(KEY_USERFIRSTNAME, null),
                sharedPreferences.getString(KEY_USERLASTNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_MOBILE, null),
                sharedPreferences.getInt(KEY_ROLE_ID, -1),
                sharedPreferences.getString(KEY_ROLE_NAME, null),
                sharedPreferences.getString(KEY_COMPANY_NAME, null),
                sharedPreferences.getString(KEY_ORGANIZATION_CODE, null),
                sharedPreferences.getString(KEY_GENDER, null),
                sharedPreferences.getString(KEY_DESIGNATION, null),
                sharedPreferences.getString(KEY_BASE_LOCATION, null),
                sharedPreferences.getString(KEY_TEAM_ID, null),
                sharedPreferences.getString(KEY_TEAM_NAME, null),
                sharedPreferences.getString(KEY_MANAGER_ID, null),
                sharedPreferences.getString(KEY_MANAGER_NAME, null),
                sharedPreferences.getString(KEY_DEFAULT_LANGUAGE, null),
                sharedPreferences.getString(KEY_MEMBER_SINCE, null),
                sharedPreferences.getString(KEY_LAST_ACTIVE_ON, null),
                sharedPreferences.getBoolean(KEY_IS_VERIFIED, false),
                sharedPreferences.getString(KEY_FIREBASE_TOKEN, null),
                sharedPreferences.getString(KEY_CREATE_AT, null),
                sharedPreferences.getString(KEY_UPDATE_AT, null),
                sharedPreferences.getString(KEY_TOKEN, null),
                sharedPreferences.getString(KEY_AVATAR_IMAGE, null),
                sharedPreferences.getString(KEY_USER_PROFILE_UPDATED_AT, null),
                null
            )
        }

    //this method will logout the user
    fun logout() {
        val sharedPreferences: SharedPreferences =
            mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
//        mCtx.startActivity(Intent(mCtx, LoginActivity::class.java))
    }

    fun setString(key: String, value: String) {
        val sharedPreferences: SharedPreferences =
            mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setInt(key: String, value: Int) {
        val sharedPreferences: SharedPreferences =
            mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }


    companion object {
        //the constants
        private const val SHARED_PREF_NAME = "rewarddragonsharedpref"
        private const val KEY_ID = "keyuserid"
        const val FIREBASE_TOKEN = "firebase_token";
        private const val KEY_UNIQUE_CODE = "keyuseruniquecode"
        const val KEY_POINT_BALANCE="keypointbalance"
        const val KEY_USERFIRSTNAME = "keyuserfirstname"
        const val KEY_USERLASTNAME = "keyuserlastname"
        private const val KEY_EMAIL = "keyemail"
        private const val KEY_MOBILE = "keymobile"
        private const val KEY_ROLE_ID = "keyroleid"
        private const val KEY_ROLE_NAME = "keyrolename"
        private const val KEY_COMPANY_NAME = "keycompanyname"
        const val KEY_COMPANY_IMAGE = "keycompanyIMAGE"
        private const val KEY_ORGANIZATION_CODE = "keyorganizationcode"
        const val KEY_GENDER = "keygender"
        const val KEY_DESIGNATION = "keydesignation"
        private const val KEY_BASE_LOCATION = "keybaselocation"
        const val KEY_TEAM_ID = "keyteamid"
        const val KEY_TEAM_NAME = "keyteamname"
        const val KEY_MANAGER_ID = "keymanagerid"
        const val KEY_MANAGER_NAME = "keymanagername"
        const val KEY_DEFAULT_LANGUAGE = "keydefaultlanguage"
        private const val KEY_MEMBER_SINCE = "keymembersince"
        private const val KEY_LAST_ACTIVE_ON = "keylastactiveon"
        private const val KEY_IS_VERIFIED = "keyisverified"
        private const val KEY_FIREBASE_TOKEN = "keyfirebasetoken"
        private const val KEY_CREATE_AT = "keycreatedat"
        private const val KEY_UPDATE_AT = "keyupdateat"
        private const val KEY_TOKEN = "keytoken"
        const val KEY_AVATAR_IMAGE = "keyavatarimage"
        const val KEY_USER_PROFILE_UPDATED_AT = "user_profile_updated_at"
        private var mInstance: SharedPrefManager? = null

        @Synchronized
        fun getInstance(context: Context): SharedPrefManager? {
            if (mInstance == null) {
                mInstance = SharedPrefManager(context)
            }
            return mInstance
        }
    }
}