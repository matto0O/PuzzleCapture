package com.amnpa.puzzlecapture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.core.CvType.CV_8UC4
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.cvtColor
import kotlin.system.exitProcess


class PuzzleTransformer(private val context: Context, og_image: Bitmap) {

    private val tiles = mutableListOf<Tile>()
    private val puzzlePatternSrc = R.drawable.puzzle5x4_clr

    init {
        transform(og_image, puzzlePatternSrc)
    }


    private fun puzzleByColor(color: Scalar, puzzlePattern: Mat, img: Mat): Triple<Int, Int, Mat> {
        val mask = Mat()
        Core.inRange(puzzlePattern, color, color, mask)
        val elems = Mat()
        Core.findNonZero(mask, elems)
        var borderTop = img.rows()
        var borderBottom = 0
        var borderLeft = img.cols()
        var borderRight = 0
        for (row in 0 until elems.rows()) {
            for (col in 0 until elems.cols()) {
                val x = elems.get(row, col)[0]
                val y = elems.get(row, col)[1]
                if (borderTop > y) borderTop = y.toInt()
                else if (borderBottom < y) borderBottom = y.toInt()
                if (borderLeft > x) borderLeft = x.toInt()
                else if (borderRight < x) borderRight = x.toInt()
            }
        }

        if (borderRight - borderLeft > 0.5 * img.cols()) {
            throw IllegalArgumentException()
        }

        Log.v("dbg- borderTop",borderTop.toString())
        Log.v("dbg- borderBottom", borderBottom.toString())
        Log.v("dbg- borderLeft",borderLeft.toString())
        Log.v("dbg- borderRight",borderRight.toString())
        Log.v("dbg- horizontalDiff",(borderRight - borderLeft).toString())
        Log.v("dbg- verticalDiff",(borderBottom - borderTop).toString())

        Log.v("dbg- eot", "eot")

        val maskROI = mask.submat(Rect(borderLeft, borderTop, borderRight - borderLeft, borderBottom - borderTop))
        val imgPuzzle = img.submat(Rect(borderLeft, borderTop, borderRight - borderLeft, borderBottom - borderTop))

        cvtColor(imgPuzzle, imgPuzzle, Imgproc.COLOR_RGB2RGBA)
        imgPuzzle.copyTo(Mat(imgPuzzle.size(), CV_8UC4), maskROI)

        return Triple(borderLeft, borderTop, imgPuzzle)
    }

    private fun bitmapToMat(bitmap: Bitmap): Mat {
        // Create a new Mat object with the same dimensions and type as the bitmap
        val mat = Mat(bitmap.height, bitmap.width, CV_8UC4)

        // Convert the bitmap to Mat
        Utils.bitmapToMat(bitmap, mat)

        // If the bitmap has an alpha channel, convert it to a 3-channel Mat
        if (bitmap.hasAlpha()) {
            cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB)
        }

        return mat
    }

    private fun matToBitmap(mat: Mat): Bitmap {
        // Create a new Bitmap object with the same dimensions and configuration as the Mat
        val bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Config.ARGB_8888)

        // Convert the Mat to Bitmap
        Utils.matToBitmap(mat, bitmap)

        return bitmap
    }

    private fun transform(imageBitmap: Bitmap, puzzlePatternDir: Int) {
        val image = bitmapToMat(imageBitmap)
        val puzzlePattern = bitmapToMat(BitmapFactory.decodeResource(
            context.resources,
            puzzlePatternDir
        ))

        Imgproc.resize(image, image, Size(puzzlePattern.cols().toDouble(), puzzlePattern.rows().toDouble()))

        val colors = HashSet<Scalar>()

        for (i in 0 until puzzlePattern.rows()) {
            for (j in 0 until puzzlePattern.cols()) {
                colors.add(Scalar(puzzlePattern.get(i, j)))
            }
        }

        Log.v("dbg- color cnt", colors.size.toString())

        for (color in colors) {
            try {
                val puzzle = puzzleByColor(color, puzzlePattern, image)
                val puzzleBitmap = matToBitmap(puzzle.third)
                tiles.add(Tile(puzzle.first, puzzle.second, puzzleBitmap))
            } catch (e: IllegalArgumentException) {
                // Skip the puzzle if it fails
            }
        }
        Log.v("dbg- tiles size", tiles.size.toString())
    }
}