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
import kotlin.random.Random


class PuzzleActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)

        val chronometer = findViewById<Chronometer>(R.id.chronometer)
        chronometer.base=SystemClock.elapsedRealtime()
        chronometer.start()

        val puzzleBackground = findViewById<ImageView>(R.id.puzzleBackground)
        val puzzleStartingDock = findViewById<ImageView>(R.id.puzzleStart)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        val layoutParams = parentCoordinatorLayout.layoutParams
        layoutParams.height = ((height-64) * 0.8).toInt()
        layoutParams.width = (width * 0.8).toInt()
        parentCoordinatorLayout.layoutParams = layoutParams

        puzzleBackground.layoutParams.height = (layoutParams.height * 0.75).toInt()
        puzzleStartingDock.layoutParams.height = (layoutParams.height * 0.20).toInt()
        val puzzleDockPos = IntArray(2)
        puzzleStartingDock.getLocationInWindow(puzzleDockPos)

        val imgBitmap = this.intent.extras!!.getParcelable<Bitmap>("bitmap")
        val pt = PuzzleTransformer(
            this,
            imgBitmap!!,
            puzzleBackground.layoutParams.height.toDouble()
        )
        val tiles = pt.tiles
        tiles.sort()

        for (tile in tiles){
//            Log.v("dbg- order", "$i - y:${tiles[i].img_y} x:${tiles[i].img_x}"
//            Log.v("dbg- order", "$i - y:${puzzleDockPos[1]} x:${puzzleDockPos[0]}")
            tile.top = (puzzleDockPos[1] * 1.2).toInt()
            tile.left = Random.nextInt(layoutParams.width) + puzzleDockPos[0]
            parentCoordinatorLayout.addView(tile)
            parentCoordinatorLayout.addDraggableChild(tile)
        }


    }
}