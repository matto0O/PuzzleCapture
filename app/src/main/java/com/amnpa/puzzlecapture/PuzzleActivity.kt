package com.amnpa.puzzlecapture

import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Chronometer
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_puzzle.*


class PuzzleActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)

        val chronometer = findViewById<Chronometer>(R.id.chronometer)
        chronometer.base=SystemClock.elapsedRealtime()
        chronometer.start()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        val layoutParams = parentCoordinatorLayout.layoutParams
        Log.v("dbg- h", layoutParams.height.toString())
        Log.v("dbg- w", layoutParams.width.toString())
        layoutParams.height = ((height-64) * 0.8).toInt()
        layoutParams.width = (width * 0.8).toInt()
        parentCoordinatorLayout.layoutParams = layoutParams
        Log.v("dbg- h", layoutParams.height.toString())
        Log.v("dbg- w", layoutParams.width.toString())

        val imgBitmap = this.intent.extras!!.getParcelable<Bitmap>("bitmap")
        val pt = PuzzleTransformer(this, imgBitmap!!)
        val tiles = pt.tiles
        tiles.sort()

        for (i in 0 until tiles.size){
            val imageView = ImageView(this)
            val usedBitmap = tiles[i].bitmap
            imageView.setImageBitmap(usedBitmap)
            imageView.isClickable = true
            imageView.isFocusable = true
            parentCoordinatorLayout.addView(imageView)
            parentCoordinatorLayout.addDraggableChild(imageView)
        }


    }
}