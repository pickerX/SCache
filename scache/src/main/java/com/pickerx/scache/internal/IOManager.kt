package com.pickerx.scache.internal

import java.io.*

class IOManager(
    private val cacheDir: File,
    private val maxSize: Long,
    private val maxCount: Int
) {


    /**
     * convert Serializable object to byteArray
     */
    fun toByteArray(value: Serializable): ByteArray? {
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(value)
            return baos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                oos?.close()
                baos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun write(hashKey: Int, data: String) {
        val file: File = newFile(hashKey)
        write(file, data)
    }

    fun write(hashKey: Int, data: ByteArray) {
        val file: File = newFile(hashKey)
        write(file, data)
    }

    /**
     * read key's value as String
     */
    fun readAsString(hashKey: Int): String {
        val file = newFile(hashKey)
        val bytes = read(file)
        return String(bytes)
    }

    /**
     * read byteArray data
     */
    private fun read(file: File): ByteArray {
        if (!file.exists()) return ByteArray(0)
        
        val ins = FileInputStream(file)
        val bytes = ins.readBytes()
        ins.close()
        return bytes
    }

    /**
     * write byteArray data
     */
    private fun write(file: File, data: ByteArray) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            out.write(data)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.flush()
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            // TODO 更新缓存
        }
    }

    /**
     * write string data
     */
    private fun write(file: File, data: String) {
        var out: BufferedWriter? = null
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(file)
            out = BufferedWriter(fileWriter, 1024)
            out.write(data)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                out?.flush()
                out?.close()
                fileWriter?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun newFile(key: Int): File {
        return File(cacheDir, key.toString())
    }

    fun count(): Int {
        return 1
    }

    fun deleteAll() {


    }
}