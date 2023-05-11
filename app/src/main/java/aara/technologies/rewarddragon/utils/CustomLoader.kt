package aara.technologies.rewarddragon.utils

import aara.technologies.rewarddragon.R
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface

/**
 * Created by admin on 8/7/2022.
 */
class CustomLoader : Dialog {


    constructor(context: Context?, theme: Int) : super(context!!, theme) {
        // TODO Auto-generated constructor stub
        try {
            setContentView(R.layout.customprogress)
        } catch (e: Exception) {
        }
    }

    constructor(
        context: Context?, cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(
        context!!,
        cancelable,
        cancelListener
    ) {        // TODO Auto-generated constructor stub
    }


}