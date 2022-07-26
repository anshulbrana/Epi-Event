package com.example.epi_event.qr_code

import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class test {
//    class MainActivity : AppCompatActivity() {
//        var btn_save: Button? = null
//        var img: ImageView? = null
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            setContentView(R.layout.activity_main)
//            img = findViewById<View>(R.id.image_view) as ImageView
//            Glide.with(this)
//                .load(image_url)
//                .into(img)
//            btn_save = findViewById<View>(R.id.save_btn) as Button
//            btn_save.setOnClickListener(object : OnClickListener() {
//                fun onClick(view: View?) {
//                    var fileOutputStream: FileOutputStream? = null
//                    val file: File = getdisc()
//                    if (!file.exists() && !file.mkdirs()) {
//                        Toast.makeText(applicationContext,
//                            "sorry can not make dir",
//                            Toast.LENGTH_LONG).show()
//                        return
//                    }
//                    val simpleDateFormat = SimpleDateFormat("yyyymmsshhmmss")
//                    val date: String = simpleDateFormat.format(Date())
//                    val name = "img$date.jpeg"
//                    val file_name: String = file.getAbsolutePath().toString() + "/" + name
//                    val new_file = File(file_name)
//                    try {
//                        fileOutputStream = FileOutputStream(new_file)
//                        val bitmap = viewToBitmap(img, img.getWidth(), img.getHeight())
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
//                        Toast.makeText(applicationContext, "sucses", Toast.LENGTH_LONG).show()
//                        fileOutputStream.flush()
//                        fileOutputStream.close()
//                    } catch (e: FileNotFoundException) {
//                    } catch (e: IOException) {
//                    }
//                    refreshGallary(file)
//                }
//
//                private fun refreshGallary(file: File) {
//                    val i = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//                    i.data = Uri.fromFile(file)
//                    sendBroadcast(i)
//                }
//
//                private fun getdisc(): File {
//                    val file: File =
//                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//                    return File(file, "My Image")
//                }
//            })
//        }
//
//        companion object {
//            private const val image_url =
//                "https://uniqueandrocode.000webhostapp.com/hiren/androidtutorial/demo.png"
//
//            fun viewToBitmap(view: View?, widh: Int, hight: Int): Bitmap {
//                val bitmap = Bitmap.createBitmap(widh, hight, Bitmap.Config.ARGB_8888)
//                val canvas = Canvas(bitmap)
//                view.draw(canvas)
//                return bitmap
//            }
//        }
//    }
}