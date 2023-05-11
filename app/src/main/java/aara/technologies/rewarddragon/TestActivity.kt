package aara.technologies.rewarddragon

import aara.technologies.rewarddragon.utils.Constant
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

//import nl.dionsegijn.konfetti.models.Shape
//import nl.dionsegijn.konfetti.models.Size

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        var button: Button = findViewById(R.id.button)
        button.setOnClickListener {
         /*   var alert = Constant.AlertDialog2(this, R.style.ThemeDialogCustom)
            //   alert.getWindow()!!.getAttributes().windowAnimations = R.style.OpenDialogAnim;
            alert.show()*/
        }
    }
}