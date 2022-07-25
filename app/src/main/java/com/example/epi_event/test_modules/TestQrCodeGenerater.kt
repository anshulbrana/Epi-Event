package com.example.epi_event.test_modules

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.epi_event.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class TestQrCodeGenerater : AppCompatActivity() {

    private lateinit var ivQRCode: ImageView
    private lateinit var etData: EditText
    private lateinit var btnCreate: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_generater)

        ivQRCode = findViewById(R.id.activity_qr_code_generater_iv_qr_code)
        etData = findViewById(R.id.activity_qr_code_generater_et_data)
        btnCreate = findViewById(R.id.activity_qr_code_generater_btn_create)

        btnCreate.setOnClickListener {
            val data = etData.text.toString().trim()
            if(data.isEmpty()){
                Toast.makeText(this, "Data cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else{
                val writer = QRCodeWriter()
                try{
                    val bitMatrix = writer.encode("$data\nWorld", BarcodeFormat.QR_CODE, 512, 512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    for(x in 0 until width){
                        for(y in 0 until height){
                            bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }

                    ivQRCode.setImageBitmap(bmp)

                }catch (e: WriterException){
                    e.printStackTrace()
                }

            }
        }
    }
}