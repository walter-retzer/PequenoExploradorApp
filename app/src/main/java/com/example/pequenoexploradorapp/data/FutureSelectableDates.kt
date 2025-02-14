package com.example.pequenoexploradorapp.data

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import com.example.pequenoexploradorapp.domain.util.formattedToMillis
import com.example.pequenoexploradorapp.domain.util.formattedYear


@OptIn(ExperimentalMaterial3Api::class)
class FutureSelectableDates(
    private val dateInitial: String,
    private val dateFinal: String,
) : SelectableDates {
    private val adjustInitialDate = dateInitial.formattedToMillis(1)
    private val adjustEndDate = dateFinal.formattedToMillis()
    private val yearInitial = dateInitial.formattedYear()
    private val yearFinal = dateFinal.formattedYear()

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis in adjustInitialDate..adjustEndDate
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year in yearInitial..yearFinal
    }
}
