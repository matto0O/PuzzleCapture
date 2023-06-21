package com.amnpa.puzzlecapture

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

class Tile @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Comparable<Tile>, AppCompatImageView(context, attrs, defStyleAttr) {

    init{
        this.isClickable = true
        this.isFocusable = true
    }

    private var anchorX: Int = 0
    private var anchorY: Int = 0
    private var maxX: Int = 0
    private var maxY: Int = 0

    fun getPosition(): Pair<Int, Int>{
        return Pair(anchorX, anchorY)
    }

    fun setPosition(x:Int, y:Int){
        anchorX = x
        anchorY = y
    }

    fun getRangeX(): ClosedRange<Int>{
        return anchorX.rangeTo(maxX)
    }

    fun getRangeY(): ClosedRange<Int>{
        return anchorY.rangeTo(maxY)
    }

    fun setAll(x: Int, y:Int, bm: Bitmap){
        setPosition(x, y)

        maxX = x + bm.width/2
        maxY = y + bm.height/2

        setImageBitmap(bm)
    }

    override fun compareTo(other: Tile): Int {
        return if(anchorY == other.anchorY){
            anchorX - other.anchorX
        } else (anchorY - other.anchorY)
    }

}