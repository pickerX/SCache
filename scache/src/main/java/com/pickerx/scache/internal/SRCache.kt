package com.pickerx.scache.internal

import android.content.Context
import com.pickerx.scache.SCache
import com.pickerx.scache.hash
import java.io.File

/**
 * IO cache only
 */
internal class SRCache : SCache {
    companion object {
        const val MAX_SIZE: Long = 1000 * 1000 * 50 // 50 mb
        const val MAX_COUNT = Int.MAX_VALUE
    }

    private lateinit var mIO: IOManager

    fun init(
        ctx: Context,
        cacheName: String = "SuperCache",
        maxSize: Long = MAX_SIZE,
        maxCount: Int = MAX_COUNT
    ) {
        val f = File(ctx.cacheDir, cacheName)
        io(f, maxSize, maxCount)
    }

    private fun io(cacheDir: File, maxSize: Long, maxCount: Int) {
        check(cacheDir.exists() || cacheDir.mkdirs()) { "can't make dirs in " + cacheDir.absolutePath }
        mIO = IOManager(cacheDir, maxSize, maxCount)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val hashKey = key.hash()
        val v = mIO.readAsString(hashKey)
        return if (v.isEmpty()) defaultValue else v.toInt()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        val hashKey = key.hash()
        val v = mIO.readAsString(hashKey)
        return if (v.isEmpty()) defaultValue else v.toLong()
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        val hashKey = key.hash()
        val v = mIO.readAsString(hashKey)
        return if (v.isEmpty()) defaultValue else v.toFloat()
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        val hashKey = key.hash()
        val v = mIO.readAsString(hashKey)
        return if (v.isEmpty()) defaultValue else v.toDouble()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val hashKey = key.hash()
        val v = mIO.readAsString(hashKey)
        return if (v.isEmpty()) defaultValue else v.toBoolean()
    }

    override fun getString(key: String, defaultValue: String): String {
        val hashKey = key.hash()
        return mIO.readAsString(hashKey)
    }

    override fun <T> put(key: String, value: T) {
        val hashKey = key.hash()
        mIO.write(hashKey, value.toString())
    }

    override fun clear() {
        mIO.deleteAll()
    }

    override fun size(): Int = mIO.count()

    override fun contain(key: String): Boolean {
        val hashKey = key.hash()
        val v = mIO.readAsString(hashKey)
        return v.isNotEmpty()
    }
}