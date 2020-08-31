package com.pickerx.scache

/**
 * Super Cache interface for developer
 *
 */
interface SCache {

    fun getInt(key: String, defaultValue: Int = 0): Int

    fun getLong(key: String, defaultValue: Long = 0): Long

    fun getFloat(key: String, defaultValue: Float = 0f): Float

    fun getDouble(key: String, defaultValue: Double = 0.0): Double

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    fun getString(key: String, defaultValue: String = ""): String

    // fun <T> getLive(key: String): LiveData<T>

    fun <T> put(key: String, value: T)

}