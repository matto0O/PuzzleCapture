package com.amnpa.puzzlecapture

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.activity_puzzle.*

class PuzzleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)

        val imgBitmap = this.intent.extras!!.getParcelable<Bitmap>("bitmap")
        val pt = PuzzleTransformer(this, imgBitmap!!)
        val tiles = pt.tiles
        tiles.sort()

        for (i in 0 until tiles.size){
//            val card = MaterialCardView(this)
//            card.isClickable = true
//            card.isFocusable = true

            val imageView = ImageView(this)
            val usedBitmap = tiles[i].bitmap
            imageView.setImageBitmap(usedBitmap)
            imageView.isClickable = true
            imageView.isFocusable = true
            parentCoordinatorLayout.addView(imageView)
            parentCoordinatorLayout.addDraggableChild(imageView)

//            card.addView(imageView)
//
//            parentCoordinatorLayout.addView(card)
//            parentCoordinatorLayout.addDraggableChild(card)
        }

    }
}