package aara.technologies.rewarddragon

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment


class KpiPointDialog(contentLayoutId: Int) : DialogFragment(contentLayoutId) {


    override fun onCreateDialog(@Nullable savedInstanceState: Bundle?): Dialog {
        if (arguments != null) {
            if (requireArguments().getBoolean("notAlertDialog")) {
                return super.onCreateDialog(savedInstanceState)
            }
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("Alert Dialog")
        builder.setMessage("Alert Dialog inside DialogFragment")
        builder.setPositiveButton("Ok",
            DialogInterface.OnClickListener { dialog, which -> dismiss() })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dismiss() })
        return builder.create()
    }
}