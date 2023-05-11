package aara.technologies.rewarddragon

import aara.technologies.rewarddragon.utils.Constant
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class BonusDialogActivity : Activity() {
    var apply: Button? = null
    var name: TextInputEditText? = null

    var drawable: Drawable? = null
    var drawable2: Drawable? = null
    var drawable3: Drawable? = null
    var drawable4: Drawable? = null
    var drawable5: Drawable? = null

    lateinit var drawableShape: Shape.DrawableShape
    lateinit var drawableShape2: Shape.DrawableShape
    lateinit var drawableShape3: Shape.DrawableShape
    lateinit var drawableShape4: Shape.DrawableShape
    lateinit var drawableShape5: Shape.DrawableShape
    lateinit var konfettiView: KonfettiView

    //  public static TextView city;
    var state: TextInputEditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bonus_alert)

        this.setFinishOnTouchOutside(false);


        konfettiView = findViewById(R.id.viewKonfetti)
        konfettiView.bringToFront()
        konfettiView.visibility = View.VISIBLE

        drawable = getDrawable(R.drawable.ic_firecracker1)
        drawableShape = Shape.DrawableShape(drawable!!, true)
        drawable2 = getDrawable(R.drawable.ic_firecrackers2)
        drawableShape2 = Shape.DrawableShape(drawable2!!, true)
        drawable3 = getDrawable(R.drawable.ic_fireworks3)
        drawableShape3 = Shape.DrawableShape(drawable3!!, true)
        drawable4 = getDrawable(R.drawable.ic_fireworks4)
        drawableShape4 = Shape.DrawableShape(drawable3!!, true)
        drawable5 = getDrawable(R.drawable.ic_fireworks5)
        drawableShape5 = Shape.DrawableShape(drawable3!!, true)


        var points = findViewById<TextView>(R.id.points)
        var message = findViewById<TextView>(R.id.messge_txt)
        points.text = intent.getStringExtra(Constant.REWARDPOINTS)
        message.text = intent.getStringExtra(Constant.REWARDMESSAGE)

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
                    drawableShape,
                    drawableShape2,
                    drawableShape3,
                    drawableShape4,
                    drawableShape5
                ),
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).perSecond(1000),
                position = Position.Relative(0.5, 0.3)
            )
        )

        Handler(Looper.myLooper()!!).postDelayed({
            setResult(RESULT_OK)
            finish()
        }, 3000)
    }

}