package kr.saintdev.gametrans.libs.network.netlib

import android.content.Context
import android.util.Log
import kr.saintdev.gametrans.libs.util.ForDevelopment
import org.apache.commons.codec.binary.Base64
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class AESSecurity(context: Context) {
    private val keyStr: String? = ForDevelopment.getSecretKey(context)?.substring(0, 16)
    private var keySpec: Key? = null

    init {
        if(this.keyStr != null) {
            val keyBytes = ByteArray(16)
            val b = keyStr.toByteArray()
            var len = b.size
            if (len > keyBytes.size) len = keyBytes.size

            System.arraycopy(b, 0, keyBytes, 0, len)
            this.keySpec = SecretKeySpec(keyBytes, "AES")
        }
    }

    fun encode(str: String) : String? {
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")

        return if(this.keySpec != null && this.keyStr != null) {
            c.init(Cipher.ENCRYPT_MODE, this.keySpec, IvParameterSpec(this.keyStr.toByteArray(charset("UTF-8"))))
            val encrypted = c.doFinal(str.toByteArray(charset("UTF-8")))
            URLEncoder.encode(String(Base64.encodeBase64(encrypted)), "UTF-8")
        } else {
            null
        }
    }

    fun decode(str: String) : String? {
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        return if(this.keySpec != null && this.keyStr != null) {
            c.init(Cipher.DECRYPT_MODE, this.keySpec, IvParameterSpec(this.keyStr.toByteArray(charset("UTF-8"))))
            val byteStr = Base64.decodeBase64(str.toByteArray(charset("UTF-8")))
            String(c.doFinal(byteStr), charset("UTF-8"))
        } else {
            null
        }
    }
}

/**
 * Plain Text -> URLSafe -> Base64 (AES Encode)
 */
fun String.aesEncode(context: Context) : String? {
    val secure = AESSecurity(context)
    return secure.encode(this)
}

/**
 * Base64 (AES Encode) -> Plain text
 */
fun String.aesDecode(context: Context) : String? {
    val secure = AESSecurity(context)
    return secure.decode(this)
}