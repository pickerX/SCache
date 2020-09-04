package com.pickerx.scache

import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

internal fun String.hash(): Int {
    return hashCode()
}

inline fun sCheck(value: Boolean, lazyMessage: () -> Any) {
    if (!value) {
        val message = lazyMessage()
        Log.e("SCache", message.toString())
    }
}

fun AppCompatActivity.isNotGranted(permission: String): Boolean =
    ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED

/**
 * check permission
 */
fun AppCompatActivity.checkAndRequestPermission(
    permissions: Array<String>,
    code: Int = 10088
): Boolean {
    permissions.forEach {
        if (isNotGranted(it)) {
            ActivityCompat.requestPermissions(this, permissions, code)
            return false
        }
    }
    return true
}

internal object ContainerHelpers {

    val EMPTY_INTS = IntArray(0)
    val EMPTY_LONGS = LongArray(0)
    val EMPTY_OBJECTS = arrayOfNulls<Any>(0)

    fun idealIntArraySize(need: Int): Int {
        return idealByteArraySize(need * 4) / 4
    }

    fun idealLongArraySize(need: Int): Int {
        return idealByteArraySize(need * 8) / 8
    }

    fun idealByteArraySize(need: Int): Int {
        for (i in 4..31) if (need <= (1 shl i) - 12) return (1 shl i) - 12
        return need
    }

    fun equal(a: Any?, b: Any): Boolean {
        return a == b || a != null && a == b
    }

    // This is Arrays.binarySearch(), but doesn't do any argument validation.
    fun binarySearch(array: IntArray, size: Int, value: Int): Int {
        var lo = 0
        var hi = size - 1
        while (lo <= hi) {
            val mid = lo + hi ushr 1
            val midVal = array[mid]
            if (midVal < value) {
                lo = mid + 1
            } else if (midVal > value) {
                hi = mid - 1
            } else {
                return mid // value found
            }
        }
        return lo.inv() // value not present
    }

    fun binarySearch(array: LongArray, size: Int, value: Long): Int {
        var lo = 0
        var hi = size - 1
        while (lo <= hi) {
            val mid = lo + hi ushr 1
            val midVal = array[mid]
            if (midVal < value) {
                lo = mid + 1
            } else if (midVal > value) {
                hi = mid - 1
            } else {
                return mid // value found
            }
        }
        return lo.inv() // value not present
    }
}