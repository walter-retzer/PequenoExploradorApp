package com.example.pequenoexploradorapp.domain.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.absoluteValue

class MaskVisualTransformation(private val mask: String) : VisualTransformation {

    private val specialSymbolsIndices = mask.indices.filter { mask[it] != '#' }

    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        var maskIndex = 0
        text.forEach { char ->
            while (specialSymbolsIndices.contains(maskIndex)) {
                out += mask[maskIndex]
                maskIndex++
            }
            out += char
            maskIndex++
        }
        return TransformedText(AnnotatedString(out), offsetTranslator())
    }

    private fun offsetTranslator() = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val offsetValue = offset.absoluteValue
            if (offsetValue == 0) return 0
            var numberOfHashtags = 0
            val masked = mask.takeWhile {
                if (it == '#') numberOfHashtags++
                numberOfHashtags < offsetValue
            }
            return masked.length + 1
        }

        override fun transformedToOriginal(offset: Int): Int {
            return mask.take(offset.absoluteValue).count { it == '#' }
        }
    }

    companion object {
        const val PHONE = "(##) #####-####"
    }
}

fun String.formattedAsPhone(): String {
    return when (length) {
        11 -> "(" + substring(0, 2) + ") " +
                substring(2, 3) + " " +
                substring(3, 7) + "-" +
                substring(7, length)
        10 -> "(" + substring(0, 2) + ") " +
                substring(2, 6) + "-" +
                substring(6, length)

        14 -> substring(0, 3) + " (" + substring(3, 5) + ") " +
                substring(5, 7) +
                substring(7, 10) + "-" +
                substring(10, length)

        else -> this
    }
}

fun String.formattedDate(): String {
    return try {
        val date = SimpleDateFormat("yyyy-mm-dd", Locale("pt-BR"))
        val initDate = date.parse(this)
        val formatter = SimpleDateFormat("dd/mm/yyyy", Locale("pt-BR"))
        formatter.format(initDate!!)
    } catch (e: Exception) {
        println(e)
        "--/--/----"
    }
}
