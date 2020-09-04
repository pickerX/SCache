package com.pickerx.scache.internal

import android.content.Context
import android.util.Log
import com.pickerx.scache.SCache
import com.pickerx.scache.hash
import java.io.File
import java.io.Serializable

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
        val cacheDir = File(ctx.cacheDir, cacheName)
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
        var value = mIO.readAsString(hashKey)
        ExpireRule()
            .match(value, {
                // expire
                mIO.remove(hashKey)
                Log.d("SCache", "key:$key has expired, the delay $it")
            }, {
                // not expire
                value = it
            })
        return value
    }

    override fun <T> put(key: String, value: T, delay: Long) {
        val hashKey = key.hash()
        when (value) {
            is Serializable -> {
                val data = mIO.toByteArray(value)
                data?.let {
                    if (delay > 0) {
                        val source = ExpireRule.createRule(delay, it)
                        mIO.write(hashKey, source)
                    } else mIO.write(hashKey, it)
                }
            }
            else -> {
                // basic type
                if (delay > 0) {
                    val rule = ExpireRule.createRule(delay)
                    mIO.write(hashKey, "$rule$value")
                } else mIO.write(hashKey, value.toString())
            }
        }
    }

    override fun clear() = mIO.clear()

    override fun size(): Int = mIO.count()

    override fun contain(key: String): Boolean {
        val hashKey = key.hash()
        return mIO.exist(hashKey)
    }

    override fun remove(key: String): Boolean {
        val hashKey = key.hash()
        return mIO.remove(hashKey)
    }
}