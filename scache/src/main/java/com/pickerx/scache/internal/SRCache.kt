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
        return get(key, defaultValue)
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return get(key, defaultValue)
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return get(key, defaultValue)
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return get(key, defaultValue)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return get(key, defaultValue)
    }

    override fun getString(key: String, defaultValue: String): String {
        val hashKey = key.hash()
        return mIO.readAsString(hashKey)
    }

    private fun <T> get(key: String, defaultValue: T): T {
        val hashKey = key.hash()
        val v = mIO.readAsString(hashKey)
        return if (v.isEmpty()) defaultValue else v as T
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