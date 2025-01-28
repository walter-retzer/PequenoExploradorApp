package com.example.pequenoexploradorapp.domain.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
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
        val date = SimpleDateFormat("yyyy-MM-dd", Locale("pt-BR"))
        val initDate = date.parse(this)
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale("pt-BR"))
        formatter.format(initDate!!)
    } catch (e: Exception) {
        println(e)
        "--/--/----"
    }
}

fun String.formattedRequestDateApi(): String {
    return try {
        val date = SimpleDateFormat("dd/MM/yyyy", Locale("pt-BR"))
        val initDate = date.parse(this)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale("pt-BR"))
        formatter.format(initDate!!)
    } catch (e: Exception) {
        println(e)
        ""
    }
}

fun Long.toBrazilianDateFormat(
    pattern: String = "dd/MM/yyyy"
): String {
    val date = Date(this)
    val formatter = SimpleDateFormat(
        pattern, Locale("pt-br")
    ).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
    return formatter.format(date)
}

fun String.toHttpsPrefix(): String =
    if (isNotEmpty() && !startsWith("https://") && !startsWith("http://")) {
        "https://$this"
    } else if (startsWith("http://")) {
        replace("http://", "https://")
    } else this

fun String.formattedYear(): Int {
    return try {
        val date = SimpleDateFormat("dd.MM.yyyy", Locale("pt-BR"))
        val initDate = date.parse(this)
        val formatter = SimpleDateFormat("yyyy", Locale("pt-BR"))
        formatter.format(initDate!!).toString().toInt()
    } catch (e: Exception) {
        println(e)
        LocalDate.now().year
    }
}

fun String.formattedMonth(): Int {
    return try {
        val date = SimpleDateFormat("dd.MM.yyyy", Locale("pt-BR"))
        val initDate = date.parse(this)
        val formatter = SimpleDateFormat("MM", Locale("pt-BR"))
        formatter.format(initDate!!).toString().toInt()
    } catch (e: Exception) {
        println(e)
        LocalDate.now().monthValue
    }
}

fun String.formattedDayMonth(): Int {
    return try {
        val date = SimpleDateFormat("dd.MM.yyyy", Locale("pt-BR"))
        val initDate = date.parse(this)
        val formatter = SimpleDateFormat("dd", Locale("pt-BR")).apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }
        formatter.format(initDate!!).toString().toInt()
    } catch (e: Exception) {
        println(e)
        LocalDate.now().dayOfMonth
    }
}

fun String.formattedToMillis(adjustDataPickerInitialDate: Int = 0): Long {
    val day = this.formattedDayMonth() - adjustDataPickerInitialDate
    val month = this.formattedMonth() - 1
    val year = this.formattedYear()

    val date = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
    }
    return date.timeInMillis
}

fun List<FavouriteImageToSave>.formattedHeadText(): String {
    return if (this.size == 1) "Encontrado 1 imagem favorita"
    else if (this.isEmpty()) ""
    else "Encontrado ${this.size} imagens favoritas"
}
