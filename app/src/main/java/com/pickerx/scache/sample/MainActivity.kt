package com.pickerx.scache.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pickerx.scache.Caches
import com.pickerx.scache.checkAndRequestPermission

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // memoryCache()

        ioCache()
    }

    private fun ioCache() {
        if (!checkAndRequestPermission(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        ) return

        val io = Caches.io(this)
        // basic data
        io.put("int", 11)
        io.put("long", 55555L)
        io.put("float", -221.0f)
        io.put("double", 99.88)
        io.put("boolean", true)
        io.put("string", "hello world")
        io.put("expireKey", "hello world", 10)
        // array or class
        val array = listOf("hello", "world", "hello", "kotlin", "hello", "android")
        io.put("array", array)

        val data = Data(3, "koikk", false)
        io.put("data", data)

        val t3 = Thread {
            val notExistKey = io.getDouble("notExistKey")
            val defaultValue = io.getBoolean("defaultValue", false)
            val int = io.getInt("int")
            val long = io.getLong("long")
            val float = io.getFloat("float")
            val double = io.getDouble("double")
            val boolean = io.getBoolean("boolean")
            val string = io.getString("string")
            val array = io.getString("array")
            val expireKey = io.getString("expireKey")

            Log.e("SCache", "not exist key's value >>>> $notExistKey")
            Log.e("SCache", "defaultValue's >>>> $defaultValue")
            Log.e("SCache", "int's >>>> $int")
            Log.e("SCache", "long's >>>> $long")
            Log.e("SCache", "float's >>>> $float")
            Log.e("SCache", "double's >>>> $double")
            Log.e("SCache", "boolean's >>>> $boolean")
            Log.e("SCache", "string's >>>> $string")
            Log.e("SCache", "array's >>>> $array")
            Log.e("SCache", "expireKey's >>>> $expireKey")
            Log.e("SCache", "delay >>>> 11s")
            Thread.sleep(11_000L)
            Log.e("SCache", "expireKey's >>>> ${io.getString("expireKey")}")
        }
        t3.start()
    }

    private fun memoryCache() {
        val c = Caches.get()
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
    }
}