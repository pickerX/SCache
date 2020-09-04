package com.pickerx.scache.internal

import com.pickerx.scache.ContainerHelpers

internal class SSRArray(var initialCapacity: Int) : Cloneable {

    private val DELETED = Any()
    private var mGarbage = false

    private var mKeys: IntArray
    private var mValues: Array<Any?>
    private var mSize = 0

    /**
     * Creates a new SparseArray containing no mappings.
     */
    constructor() : this(10)

    /**
     * Creates a new SparseArray containing no mappings that will not
     * require any additional memory allocation to store the specified
     * number of mappings.  If you supply an initial capacity of 0, the
     * sparse array will be initialized with a light-weight representation
     * not requiring any additional array allocations.
     */
    init {
        if (initialCapacity == 0) {
            mKeys = ContainerHelpers.EMPTY_INTS
            mValues = ContainerHelpers.EMPTY_OBJECTS
        } else {
            initialCapacity = ContainerHelpers.idealIntArraySize(initialCapacity)
            mKeys = IntArray(initialCapacity)
            mValues = arrayOfNulls(initialCapacity)
        }
    }

    override fun clone(): SSRArray {
        val clone: SSRArray
        try {
            clone = super.clone() as SSRArray
            clone.mKeys = mKeys.clone()
            clone.mValues = mValues.clone()
        } catch (e: CloneNotSupportedException) {
            throw AssertionError(e) // Cannot happen as we implement Cloneable.
        }
        return clone
    }

    /**
     * Gets the Object mapped from the specified key, or `null`
     * if no such mapping has been made.
     */
    // See inline comment.
    operator fun <E> get(key: Int): E? {
        // We pass null as the default to a function which isn't explicitly annotated as nullable.
        // Not marking the function as nullable should allow us to eventually propagate the generic
        // parameter's nullability to the caller. If we were to mark it as nullable now, we would
        // also be forced to mark the return type of that method as nullable which harms the case
        // where you are passing in a non-null default value.
        return get(key, null)
    }

    /**
     * Gets the Object mapped from the specified key, or the specified Object
     * if no such mapping has been made.
     */
    operator fun <E> get(key: Int, valueIfKeyNotFound: E?): E? {
        val i = ContainerHelpers.binarySearch(mKeys, mSize, key)
        return if (i < 0 || mValues[i] === DELETED) {
            valueIfKeyNotFound
        } else {
            mValues[i] as E?
        }
    }

    /**
     * Removes the mapping from the specified key, if there was any.
     */
    fun remove(key: Int): Boolean {
        val i = ContainerHelpers.binarySearch(mKeys, mSize, key)
        if (i >= 0) {
            if (mValues[i] !== DELETED) {
                mValues[i] = DELETED
                mGarbage = true
                return true
            }
        }
        return false
    }

    /**
     * Remove an existing key from the array map only if it is currently mapped to `value`.
     * @param key The key of the mapping to remove.
     * @param value The value expected to be mapped to the key.
     * @return Returns true if the mapping was removed.
     */
    fun <E> remove(key: Int, value: Any?): Boolean {
        val index = indexOfKey(key)
        if (index >= 0) {
            val mapValue: E? = valueAt(index)
            if (value == mapValue || value != null && value == mapValue) {
                removeAt(index)
                return true
            }
        }
        return false
    }

    /**
     * Removes the mapping at the specified index.
     */
    fun removeAt(index: Int) {
        if (mValues[index] !== DELETED) {
            mValues[index] = DELETED
            mGarbage = true
        }
    }

    /**
     * Remove a range of mappings as a batch.
     *
     * @param index Index to begin at
     * @param size Number of mappings to remove
     */
    fun removeAtRange(index: Int, size: Int) {
        val end = Math.min(mSize, index + size)
        for (i in index until end) {
            removeAt(i)
        }
    }

    /**
     * Replace the mapping for `key` only if it is already mapped to a value.
     * @param key The key of the mapping to replace.
     * @param value The value to store for the given key.
     * @return Returns the previous mapped value or null.
     */
    fun <E> replace(key: Int, value: E?): E? {
        val index = indexOfKey(key)
        if (index >= 0) {
            val oldValue: E? = mValues[index] as E?
            mValues[index] = value
            return oldValue
        }
        return null
    }

    /**
     * Replace the mapping for `key` only if it is already mapped to a value.
     *
     * @param key The key of the mapping to replace.
     * @param oldValue The value expected to be mapped to the key.
     * @param newValue The value to store for the given key.
     * @return Returns true if the value was replaced.
     */
    fun <E> replace(key: Int, oldValue: E?, newValue: E?): Boolean {
        val index = indexOfKey(key)
        if (index >= 0) {
            val mapValue = mValues[index]
            if (mapValue === oldValue || oldValue != null && oldValue == mapValue) {
                mValues[index] = newValue
                return true
            }
        }
        return false
    }

    private fun gc() {
        // Log.e("SparseArray", "gc start with " + mSize);
        val n = mSize
        var o = 0
        val keys = mKeys
        val values = mValues
        for (i in 0 until n) {
            val v = values[i]
            if (v !== DELETED) {
                if (i != o) {
                    keys[o] = keys[i]
                    values[o] = v
                    values[i] = null
                }
                o++
            }
        }
        mGarbage = false
        mSize = o

        // Log.e("SparseArray", "gc end with " + mSize);
    }

    /**
     * Adds a mapping from the specified key to the specified value,
     * replacing the previous mapping from the specified key if there
     * was one.
     */
    fun <E> put(key: Int, value: E?) {
        var i = ContainerHelpers.binarySearch(mKeys, mSize, key)
        if (i >= 0) {
            mValues[i] = value
        } else {
            i = i.inv()
            if (i < mSize && mValues[i] === DELETED) {
                mKeys[i] = key
                mValues[i] = value
                return
            }
            if (mGarbage && mSize >= mKeys.size) {
                gc()

                // Search again because indices may have changed.
                i = ContainerHelpers.binarySearch(mKeys, mSize, key).inv()
            }
            if (mSize >= mKeys.size) {
                val n = ContainerHelpers.idealIntArraySize(mSize + 1)
                val nkeys = IntArray(n)
                val nvalues = arrayOfNulls<Any>(n)

                // Log.e("SparseArray", "grow " + mKeys.length + " to " + n);
                System.arraycopy(mKeys, 0, nkeys, 0, mKeys.size)
                System.arraycopy(mValues, 0, nvalues, 0, mValues.size)
                mKeys = nkeys
                mValues = nvalues
            }
            if (mSize - i != 0) {
                // Log.e("SparseArray", "move " + (mSize - i));
                System.arraycopy(mKeys, i, mKeys, i + 1, mSize - i)
                System.arraycopy(mValues, i, mValues, i + 1, mSize - i)
            }
            mKeys[i] = key
            mValues[i] = value
            mSize++
        }
    }

    /**
     * Copies all of the mappings from the `other` to this map. The effect of this call is
     * equivalent to that of calling [.put] on this map once for each mapping
     * from key to value in `other`.
     */
    fun <E> putAll(other: SSRArray) {
        var i = 0
        val size = other.size()
        while (i < size) {
            put(other.keyAt(i), other.valueAt<E>(i))
            i++
        }
    }

    /**
     * Add a new value to the array map only if the key does not already have a value or it is
     * mapped to `null`.
     * @param key The key under which to store the value.
     * @param value The value to store for the given key.
     * @return Returns the value that was stored for the given key, or null if there
     * was no such key.
     */
    fun <E> putIfAbsent(key: Int, value: E?): E? {
        val mapValue: E? = get(key)
        if (mapValue == null) {
            put(key, value)
        }
        return mapValue
    }

    /**
     * Returns the number of key-value mappings that this SparseArray
     * currently stores.
     */
    fun size(): Int {
        if (mGarbage) {
            gc()
        }
        return mSize
    }

    /**
     * Return true if size() is 0.
     * @return true if size() is 0.
     */
    fun isEmpty(): Boolean {
        return size() == 0
    }

    /**
     * Given an index in the range `0...size()-1`, returns
     * the key from the `index`th key-value mapping that this
     * SparseArray stores.
     */
    fun keyAt(index: Int): Int {
        if (mGarbage) {
            gc()
        }
        return mKeys[index]
    }

    /**
     * Given an index in the range `0...size()-1`, returns
     * the value from the `index`th key-value mapping that this
     * SparseArray stores.
     */
    fun <E> valueAt(index: Int): E? {
        if (mGarbage) {
            gc()
        }
        return mValues[index] as E?
    }

    /**
     * Given an index in the range `0...size()-1`, sets a new
     * value for the `index`th key-value mapping that this
     * SparseArray stores.
     */
    fun <E> setValueAt(index: Int, value: E?) {
        if (mGarbage) {
            gc()
        }
        mValues[index] = value
    }

    /**
     * Returns the index for which [.keyAt] would return the
     * specified key, or a negative number if the specified
     * key is not mapped.
     */
    fun indexOfKey(key: Int): Int {
        if (mGarbage) {
            gc()
        }
        return ContainerHelpers.binarySearch(mKeys, mSize, key)
    }

    /**
     * Returns an index for which [.valueAt] would return the
     * specified key, or a negative number if no keys map to the
     * specified value.
     *
     * Beware that this is a linear search, unlike lookups by key,
     * and that multiple keys can map to the same value and this will
     * find only one of them.
     *
     * Note also that unlike most collections' `indexOf` methods,
     * this method compares values using `==` rather than `equals`.
     */
    fun <E> indexOfValue(value: E): Int {
        if (mGarbage) {
            gc()
        }
        for (i in 0 until mSize) if (mValues[i] === value) return i
        return -1
    }

    /** Returns true if the specified key is mapped.  */
    fun containsKey(key: Int): Boolean {
        return indexOfKey(key) >= 0
    }

    /** Returns true if the specified value is mapped from any key.  */
    fun <E> containsValue(value: E): Boolean {
        return indexOfValue(value) >= 0
    }

    /**
     * Removes all key-value mappings from this SparseArray.
     */
    fun clear() {
        val n = mSize
        val values = mValues
        for (i in 0 until n) {
            values[i] = null
        }
        mSize = 0
        mGarbage = false
    }

    /**
     * Puts a key/value pair into the array, optimizing for the case where
     * the key is greater than all existing keys in the array.
     */
    fun <E> append(key: Int, value: E?) {
        if (mSize != 0 && key <= mKeys[mSize - 1]) {
            put(key, value)
            return
        }
        if (mGarbage && mSize >= mKeys.size) {
            gc()
        }
        val pos = mSize
        if (pos >= mKeys.size) {
            val n = ContainerHelpers.idealIntArraySize(pos + 1)
            val nkeys = IntArray(n)
            val nvalues = arrayOfNulls<Any>(n)

            // Log.e("SparseArray", "grow " + mKeys.length + " to " + n);
            System.arraycopy(mKeys, 0, nkeys, 0, mKeys.size)
            System.arraycopy(mValues, 0, nvalues, 0, mValues.size)
            mKeys = nkeys
            mValues = nvalues
        }
        mKeys[pos] = key
        mValues[pos] = value
        mSize = pos + 1
    }

    /**
     * {@inheritDoc}
     *
     *
     * This implementation composes a string by iterating over its mappings. If
     * this map contains itself as a value, the string "(this Map)"
     * will appear in its place.
     */
    override fun toString(): String {
        if (size() <= 0) {
            return "{}"
        }
        val buffer = StringBuilder(mSize * 28)
        buffer.append('{')
        for (i in 0 until mSize) {
            if (i > 0) {
                buffer.append(", ")
            }
            val key = keyAt(i)
            buffer.append(key)
            buffer.append('=')
            val value: Any? = valueAt(i)
            if (value !== this) {
                buffer.append(value)
            } else {
                buffer.append("(this Map)")
            }
        }
        buffer.append('}')
        return buffer.toString()
    }
}