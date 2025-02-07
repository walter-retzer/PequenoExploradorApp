package com.example.pequenoexploradorapp.data

import androidx.annotation.DrawableRes
import com.example.pequenoexploradorapp.R


data class Planet(
    var name: String,
    @DrawableRes var img: Int,
    var duration: Int,
    var temperature: Int,
    var info: String
)

val planets = listOf(
    Planet(
        name = "Mercurio",
        img = R.drawable.image_mercury,
        temperature = 0,
        duration = 0,
        info = ""
    ),
    Planet(
        name = "Vênus",
        img = R.drawable.image_venus,
        temperature = 0,
        duration = 0,
        info = ""
    ),
    Planet(
        name = "Terra",
        img = R.drawable.image_earth,
        temperature = 0,
        duration = 0,
        info = ""
    ),
    Planet(
        name = "Marte",
        img = R.drawable.image_mars,
        temperature = 0,
        duration = 0,
        info = ""
    ),
    Planet(
        name = "Júpiter",
        img = R.drawable.image_jupiter,
        temperature = 0,
        duration = 0,
        info = ""
    ),
    Planet(
        name = "Saturno",
        img = R.drawable.image_saturn,
        temperature = 0,
        duration = 0,
        info = ""
    ),
    Planet(
        name = "Urano",
        img = R.drawable.image_uranus,
        temperature = 0,
        duration = 0,
        info = ""
    ),
    Planet(
        name = "Netuno",
        img = R.drawable.image_neptune,
        temperature = 0,
        duration = 0,
        info = ""
    )
)