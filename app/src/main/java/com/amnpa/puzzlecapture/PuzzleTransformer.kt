package com.amnpa.puzzlecapture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.core.CvType.CV_8UC4
import org.opencv.imgproc.Imgproc.*


class PuzzleTransformer(private val context: Context, og_image: Bitmap) {

    val tiles = mutableListOf<Tile>()
    private val puzzlePatternSrc = R.drawable.puzzle4x6_portrait_clr

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

        val maskROI = mask.submat(Rect(borderLeft, borderTop, borderRight - borderLeft, borderBottom - borderTop))
        val imgPuzzle = img.submat(Rect(borderLeft, borderTop, borderRight - borderLeft, borderBottom - borderTop))

        val resultMat = Mat(imgPuzzle.size(), CV_8UC4)
        Core.bitwise_and(imgPuzzle, imgPuzzle, resultMat, maskROI)
        return Triple(borderLeft, borderTop, resultMat)
    }

    private fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat(bitmap.height, bitmap.width, CV_8UC4)
        Utils.bitmapToMat(bitmap, mat)

        return mat
    }

    private fun matToBitmap(mat: Mat): Bitmap {
        val bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Config.ARGB_8888)
        Utils.matToBitmap(mat, bitmap)
        return bitmap
    }

    private fun transform(imageBitmap: Bitmap, puzzlePatternDir: Int) {
        val image = bitmapToMat(imageBitmap)
        val puzzlePattern = bitmapToMat(BitmapFactory.decodeResource(
            context.resources,
            puzzlePatternDir
        ))

        resize(image, image, Size(puzzlePattern.cols().toDouble(), puzzlePattern.rows().toDouble()))

        val colors = HashSet<Scalar>()

        for (i in 0 until puzzlePattern.rows()) {
            for (j in 0 until puzzlePattern.cols()) {
                colors.add(Scalar(puzzlePattern.get(i, j)))
            }
        }

        for (color in colors) {
            try {
                val puzzle = puzzleByColor(color, puzzlePattern, image)
                val puzzleBitmap = matToBitmap(puzzle.third)
                tiles.add(Tile(puzzle.first, puzzle.second, puzzleBitmap))
            } catch (e: IllegalArgumentException) {
                // Skip the puzzle if it fails
            }
        }
    }
}