package com.pickerx.scache

class Caches {
    class MemoryCache {
        companion object {
            val mCache: SCache = MCache()
        }
    }

    class IOCache {
        companion object {
            val mCache: SCache = MCache()
        }
    }

    companion object {
        fun get(): SCache {
            return MemoryCache.mCache
        }
    }
}

