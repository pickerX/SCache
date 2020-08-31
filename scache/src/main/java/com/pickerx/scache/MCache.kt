package com.pickerx.scache

import android.util.Log
import androidx.collection.SparseArrayCompat
import java.util.*

/**
 * Memory only cache
 */
class MCache(private val maxSize: Int = 1000) : SCache {
    // basic cache
    private val intCache = SparseArrayCompat<Int>(10)
    private val stringCache = SparseArrayCompat<String>(10)
    private val booleanCache = SparseArrayCompat<Boolean>(10)

    // init when need
    private val longCache = lazy { SparseArrayCompat<Long>(10) }
    private val floatCache = lazy { SparseArrayCompat<Float>(10) }
    private val doubleCache = lazy { SparseArrayCompat<Double>(10) }

//    private val mLiveData: SparseArrayCompat<LiveData<*>> = SparseArrayCompat(30)
//    private val handler = Handler()

    private val mLruCache: LinkedHashMap<String, Any> =
        LinkedHashMap(20, 0.75f, true)
    private var size: Int = 0

    private var evictionCount: Int = 0
    private var createCount: Int = 0
    private val hitCount = 0
    private val missCount = 0

    private fun trimToSize(maxSize: Int) {
        while (true) {
            var key: String
            var value: Any
            synchronized(this) {
                check(!(size < 0 || mLruCache.isEmpty() && size != 0)) {
                    (javaClass.name
                            + ".sizeOf() is reporting inconsistent results!")
                }
                if (size <= maxSize || mLruCache.isEmpty()) {
                    return
                }
                val toEvict: Map.Entry<String, Any> = mLruCache.entries.iterator().next()
                key = toEvict.key
                value = toEvict.value

                val hash = key.hash()
                getSparse(hash)?.remove(hash)
                Log.d("MCache", "remove:$key, $value")

                mLruCache.remove(key)
                size -= safeSizeOf(key, value)
                evictionCount++
            }
        }
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val hash = key.hash()
        mLruCache[key] = hash
        return intCache.get(hash, defaultValue)
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        val hash = key.hash()
        mLruCache[key] = hash
        return longCache.value[hash, defaultValue]
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        val hash = key.hash()
        mLruCache[key] = hash
        return floatCache.value[hash, defaultValue]
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        val hash = key.hash()
        mLruCache[key] = hash
        return doubleCache.value[hash, defaultValue]
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val hash = key.hash()
        mLruCache[key] = hash
        return booleanCache[hash, defaultValue]
    }

    override fun getString(key: String, defaultValue: String): String {
        val hash = key.hash()
        mLruCache[key] = hash
        return stringCache[hash, defaultValue]
    }

//    override fun <T> getLive(key: String): LiveData<T> {
//        val data = mLiveData.get(key.hash(), MutableLiveData<T>())
//
//        return data as LiveData<T>
//    }

    override fun <T> put(key: String, value: T) {
        val hash = key.hash()
        size += safeSizeOf(key, value as Any)

        var previous: Any?
        when (value) {
            is Int -> {
                previous = intCache.get(hash)
                intCache.put(hash, value)
            }
            is Long -> {
                previous = longCache.value.get(hash)
                longCache.value.put(hash, value)
            }
            is Float -> {
                previous = floatCache.value.get(hash)
                floatCache.value.put(hash, value)
            }
            is Double -> {
                previous = doubleCache.value.get(hash)
                doubleCache.value.put(hash, value)
            }
            is String -> {
                previous = stringCache.get(hash)
                stringCache.put(hash, value)
            }
            is Boolean -> {
                previous = booleanCache.get(hash)
                booleanCache.put(hash, value)
            }
            else -> {
                throw IllegalArgumentException("Unknown value type:${value!!::class.java}")
            }
        }
        previous?.let { size -= safeSizeOf(key, it) }
        trimToSize(maxSize)
    }


    private fun getSparse(hash: Int): SparseArrayCompat<*>? {
        return when {
            intCache.containsKey(hash) -> intCache
            booleanCache.containsKey(hash) -> booleanCache
            stringCache.containsKey(hash) -> stringCache
            longCache.value.containsKey(hash) -> longCache.value
            floatCache.value.containsKey(hash) -> floatCache.value
            doubleCache.value.containsKey(hash) -> doubleCache.value
            else -> null
        }
    }

    private fun contains(hash: Int): Boolean = getSparse(hash) != null

    private fun safeSizeOf(key: String, value: Any): Int {
        val result: Int = sizeOf(key, value)
        check(result >= 0) { "Negative size: $key=$value" }
        return result
    }

    private fun sizeOf(key: String, value: Any): Int {
        return 1
    }
}