package com.pickerx.scache

import com.pickerx.scache.internal.SRCache
import com.pickerx.scache.internal.SSRCache

class Caches {
    class MemoryCache {
        companion object {
            val cache: SCache = SSRCache()
        }
    }

    class IOCache {
        companion object {
            val cache: SCache = SRCache()
        }
    }

    companion object {
        fun get(): SCache {
            return MemoryCache.cache
        }

        fun io(): SCache {
            return IOCache.cache
        }
    }
}

