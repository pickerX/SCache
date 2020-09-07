package com.pickerx.scache.internal

import java.util.regex.Pattern

class ExpireRule {

    private val mPattern = Pattern.compile((">>>([0-9]+)-([0-9]+)<<<"))

    companion object {
        fun createRule(expireTime: Long): String {
            val now = System.currentTimeMillis()
            return ">>>$now-$expireTime<<<"
        }

        fun createRule(expireTime: Long, data: ByteArray): ByteArray {
            val rule = createRule(expireTime).toByteArray()
            val source = ByteArray(rule.size + data.size)
            System.arraycopy(source, 0, rule, 0, rule.size)
            System.arraycopy(source, rule.size, data, 0, data.size)
            return source
        }
    }

    /**
     * check the value has expire rule
     * if yes, check expire or not, do [expire] when expire, or [notExpire] when not
     * otherwise, execute [notMatch]
     *
     * @param value
     * @param expire expire callback
     * @param notExpire not expire callback
     * @param notMatch  not match callback
     */
    fun match(
        value: String,
        expire: (Long) -> Unit,
        notExpire: (String) -> Unit,
        notMatch: (() -> Unit)? = null
    ) {
        if (value.isEmpty()) return

        val matcher = mPattern.matcher(value)
        val m = matcher.find()

        if (m) {
            val saveTime = matcher.group(1)?.toLong() ?: 0
            val delay = matcher.group(2)?.toLong() ?: 0
            val now = System.currentTimeMillis()

            if ((now - saveTime) / 1000 > delay) {
                expire.invoke(delay)
            } else {
                // get the real value, exclude the expire rule string
                val realValue = value.substringAfter(matcher.group(), value)
                notExpire.invoke(realValue)
            }
        } else {
            notMatch?.invoke()
        }
    }
}