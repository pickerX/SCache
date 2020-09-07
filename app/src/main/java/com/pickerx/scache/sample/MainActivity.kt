package com.pickerx.scache.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pickerx.scache.Caches
import com.pickerx.scache.SCache
import com.pickerx.scache.checkAndRequestPermission

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!checkAndRequestPermission(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        ) return

        // runTestCases(Caches.io(this))

        runTestCases(Caches.get())
    }

    private fun runTestCases(io: SCache) {
        // basic data
        io.put("int", 11)
        io.put("long", 55555L)
        io.put("float", -221.0f)
        io.put("double", 99.88)
        io.put("boolean", true)
        io.put("string", "hello world")
        // array or class
        val array = listOf("hello", "world", "hello", "kotlin", "hello", "android")
        io.put("array", array)
        // Serializable data
        val data = Data(3, "koikk", false)
        io.put("data", data)
        // data array
        val dataArray = mutableListOf<Data>()
        for (i in 0..10) {
            dataArray.add(Data(i.toLong(), "koik$i", false))
        }
        io.put("dataArray", dataArray)

        val t3 = Thread {
            val notExistKey = io.getDouble("notExistKey")
            val defaultValue = io.getBoolean("defaultValue", false)
            val int = io.getInt("int")
            val long = io.getLong("long")
            val float = io.getFloat("float")
            val double = io.getDouble("double")
            val boolean = io.getBoolean("boolean")
            val string = io.getString("string")
            val arrays = io.getArray<String>("array")
            val datas = io.get<Data>("data")
            val dataArrays = io.getArray<Data>("dataArray")

            Log.e("SCache", "not exist key's value >>>> $notExistKey")
            Log.e("SCache", "defaultValue's >>>> $defaultValue")
            Log.e("SCache", "int's >>>> $int")
            Log.e("SCache", "long's >>>> $long")
            Log.e("SCache", "float's >>>> $float")
            Log.e("SCache", "double's >>>> $double")
            Log.e("SCache", "boolean's >>>> $boolean")
            Log.e("SCache", "string's >>>> $string")
            Log.e("SCache", "array's >>>> $arrays")
            Log.e("SCache", "data's >>>> $datas")
            Log.e("SCache", "data's >>>> $dataArrays")

            io.put("expireKey", "hello world", 10)
            val expireKey = io.getString("expireKey")
            Log.e("SCache", "expireKey's >>>> $expireKey")
            Log.e("SCache", "delay >>>> 11s")
            Thread.sleep(11_000L)
            Log.e("SCache", "expireKey's >>>> ${io.getString("expireKey")}")
        }
        t3.start()
    }
}