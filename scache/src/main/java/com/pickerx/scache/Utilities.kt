package com.pickerx.scache

internal fun String.hash(): Int {
    return hashCode()
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