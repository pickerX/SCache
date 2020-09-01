package com.pickerx.scache.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pickerx.scache.Caches
import com.pickerx.scache.checkAndRequestPermission

class MainActivity : AppCompatActivity() {

    private val c = Caches.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        c.put("hello", "world")

        val t1 = Thread {
            val s = c.getString("hello")
            c.put("wukong", "Super3")
            Log.e("SCache", "t1 >>>> $s")
            c.put("hello", "shut the fuck up")
        }

        val t2 = Thread {
            val s = c.getString("wukong")
            val hello = c.getString("hello")
            Log.e("SCache", "t2>>>$s")
            Log.e("SCache", "t2>>>$hello")

            c.put("wukong", "Super3 赛亚人")
        }
        t1.start()
        t2.start()

//        c.getLive<String>("hello").observe(this) {
//            Log.e("SCache", "main observer>>>$it")
//        }

        if (!checkAndRequestPermission(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        ) return

        val io = Caches.io(this)
        io.put("hello", "world")
        io.put("int11", 11)

        val t3 = Thread {
            try {
                val s = io.getBoolean("hello")
                Log.e("SCache", "t3>>>>s $s")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val xxxx = io.getString("xxxx")
            val hello = io.getString("hello")
            val nullFloat = io.getFloat("test")
            val int11 = io.getInt("int11")

            check(nullFloat == 0f) { "no found test key" }
            log(hello)
            log("$int11")
        }
        t3.start()
    }

    private fun log(message: String) {
        Log.e("SCache", ">>>>s $message")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}