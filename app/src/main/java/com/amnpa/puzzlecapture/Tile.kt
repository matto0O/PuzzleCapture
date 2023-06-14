package com.amnpa.puzzlecapture

import android.graphics.Bitmap

data class Tile (val img_x: Int, val img_y: Int, val bitmap: Bitmap) : Comparable<Tile>{
    override fun compareTo(other: Tile): Int {
        return if(img_y == other.img_y){
            img_x - other.img_x
        } else (img_y - other.img_y)
    }

}