package com.example.world_of_dinosaurs_extented.data.remote

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.experimental.xor

/**
 * TC3-HMAC-SHA256 signature implementation for Tencent Cloud API
 * Reference: https://cloud.tencent.com/document/product/214/30601
 */
object TencentSigner {

    private const val ALGORITHM = "HmacSHA256"
    private const val ENCODING = "UTF-8"
    private const val SERVICE = "iai" // AI service
    private const val HOST = "iai.tencentcloudapi.com"
    private const val METHOD = "POST"
    private const val ACTION = "DetectLabel"
    private const val VERSION = "2018-03-01"
    private const val REGION = "ap-guangzhou"

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * Generate signed headers for Tencent Cloud API request
     */
    fun sign(
        secretId: String,
        secretKey: String,
        payload: String
    ): Map<String, String> {
        val now = Date()
        val timestamp = now.time / 1000
        val date = dateFormat.format(now)
        val timestampStr = timestampFormat.format(now)

        // Step 1: Hash the request payload
        val hashedPayload = sha256Hex(payload)

        // Step 2: Build canonical request string
        val canonicalQueryString = ""
        val canonicalUri = "/"
        val canonicalHeaders = "content-type:application/json; charset=utf-8\n" +
            "host:$HOST\n"
        val signedHeaders = "content-type;host"
        val canonicalRequest = "$METHOD\n$canonicalUri\n$canonicalQueryString\n" +
            "$canonicalHeaders\n$signedHeaders\n$hashedPayload"

        // Step 3: Hash the canonical request
        val hashedCanonicalRequest = sha256Hex(canonicalRequest)

        // Step 4: Build string to sign
        val algorithm = "TC3-HMAC-SHA256"
        val credentialScope = "$date/$SERVICE/tc3_request"
        val stringToSign = "$algorithm\n$timestampStr\n$credentialScope\n$hashedCanonicalRequest"

        // Step 5: Calculate signature
        val signature = calculateSignature(secretKey, date, stringToSign)

        // Step 6: Build authorization header
        val authorization = "$algorithm Credential=$secretKey/$credentialScope, " +
            "SignedHeaders=$signedHeaders, Signature=$signature"

        return mapOf(
            "Authorization" to authorization,
            "Content-Type" to "application/json; charset=utf-8",
            "Host" to HOST,
            "X-TC-Action" to ACTION,
            "X-TC-Timestamp" to timestamp.toString(),
            "X-TC-Version" to VERSION,
            "X-TC-Region" to REGION
        )
    }

    private fun calculateSignature(
        secretKey: String,
        date: String,
        stringToSign: String
    ): String {
        val secretDate = hmacSha256(utf8Bytes("TC3$secretKey"), utf8Bytes(date))
        val secretService = hmacSha256(secretDate, utf8Bytes(SERVICE))
        val secretSigning = hmacSha256(secretService, utf8Bytes("tc3_request"))
        return hmacSha256Hex(secretSigning, utf8Bytes(stringToSign))
    }

    private fun sha256Hex(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(utf8Bytes(input))
        return bytesToHex(hash)
    }

    private fun hmacSha256(key: ByteArray, input: ByteArray): ByteArray {
        val mac = Mac.getInstance(ALGORITHM)
        mac.init(SecretKeySpec(key, ALGORITHM))
        return mac.doFinal(input)
    }

    private fun hmacSha256Hex(key: ByteArray, input: ByteArray): String {
        return bytesToHex(hmacSha256(key, input))
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789abcdef".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = hexArray[v ushr 4]
            hexChars[i * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun utf8Bytes(input: String): ByteArray {
        return input.toByteArray(StandardCharsets.UTF_8)
    }

    /**
     * URL encode a string
     */
    fun urlEncode(input: String): String {
        return URLEncoder.encode(input, ENCODING)
            .replace("+", "%20")
            .replace("*", "%2A")
            .replace("%7E", "~")
    }
}