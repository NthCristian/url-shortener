package io.nthcristian

private const val BASE62_ALPHABET =
    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun Int.toBase62(): String {
    if (this == 0) return "0"

    var number = this
    val base = 62
    val result = StringBuilder()

    while (number > 0) {
        val remainder = (number % base)
        result.append(BASE62_ALPHABET[remainder])
        number /= base
    }

    return result.reverse().toString()
}

fun String.fromBase62(): Int? {
    var result = 0
    val base = 62

    for (char in this) {
        val value = BASE62_ALPHABET.indexOf(char)

        if (value == -1) {
            return null
        }

        result = result * base + value
    }

    return result
}


fun isValidUrl(url: String): Boolean {
    return try {
        val trimmed = url.trim()
        val uri = java.net.URI(trimmed)

        if (uri.host == "localhost") return false

        val allowedSchemes = setOf("http", "https")

        uri.scheme in allowedSchemes &&
                !uri.host.isNullOrBlank()
    } catch (e: Exception) {
        false
    }
}

