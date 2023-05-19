package com.amnpa.puzzlecapture

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {

    lateinit var button: Button
    lateinit var imgView : ImageView
    private val REQ_IMG_CAP = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        imgView = findViewById(R.id.imageView)

        println(OpenCVLoader.initDebug())

        button.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            try{
                startActivityForResult(takePictureIntent, REQ_IMG_CAP)
            } catch(e: ActivityNotFoundException){
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode:Int, data:Intent?){
        if (requestCode == REQ_IMG_CAP && resultCode == RESULT_OK){
            val imgBitmap = data?.extras?.get("data") as Bitmap
            imgView.setImageBitmap(imgBitmap)
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}