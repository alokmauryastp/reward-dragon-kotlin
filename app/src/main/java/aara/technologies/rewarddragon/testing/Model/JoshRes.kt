package aara.technologies.rewarddragon.testing.Model
import com.google.gson.annotations.SerializedName


data class JoshRes(@SerializedName("count"         ) var count        : Int?                   = null,
                   @SerializedName("msg"           ) var msg          : String?                = null,
                   @SerializedName("data"          ) var data         : Data?                  = Data(),
                   @SerializedName("team_mood"     ) var teamMood     : Int?                   = null,
                   @SerializedName("moodalytics"   ) var moodalytics  : ArrayList<Moodalytics> = arrayListOf(),
                   @SerializedName("response_code" ) var responseCode : Int?                   = null)

{

    data class Data (

        @SerializedName("id"           ) var id          : Int?    = null,
        @SerializedName("created_at"   ) var createdAt   : String? = null,
        @SerializedName("updated_at"   ) var updatedAt   : String? = null,
        @SerializedName("manager_id"   ) var managerId   : Int?    = null,
        @SerializedName("description"  ) var description : String? = null,
        @SerializedName("emoji_point"  ) var emojiPoint  : Int?    = null,
        @SerializedName("user_profile" ) var userProfile : Int?    = null,
        @SerializedName("reason_type"  ) var reasonType  : String? = null

    )


    data class Moodalytics (

        @SerializedName("id"              ) var id            : Int?    = null,
        @SerializedName("created_at"      ) var createdAt     : String? = null,
        @SerializedName("updated_at"      ) var updatedAt     : String? = null,
        @SerializedName("user_profile_id" ) var userProfileId : Int?    = null,
        @SerializedName("reason_type_id"  ) var reasonTypeId  : Int?    = null,
        @SerializedName("manager_id"      ) var managerId     : Int?    = null,
        @SerializedName("description"     ) var description   : String? = null,
        @SerializedName("emoji_point"     ) var emojiPoint    : Int?    = null

    )
}
