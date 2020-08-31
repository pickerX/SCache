package com.pickerx.scache.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pickerx.scache.Caches

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

        c.getLive<String>("hello").observe(this) {
            Log.e("SCache", "main observer>>>$it")
        }
    }
}