package com.example.pequenoexploradorapp.util

import androidx.annotation.DrawableRes
import com.example.pequenoexploradorapp.R

sealed class OnBoardingPage(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {
    object First : OnBoardingPage(
        image = R.drawable.image_onboarding1,
        title = "Bem-vindo ao \nPequeno Explorador!",
        description = "Prepare-se para uma viagem incrível pelo universo! Aqui, você vai explorar planetas, estrelas, galáxias e muito mais.\nAperte os cintos e embarque nessa jornada cheia de descobertas e diversão. O cosmos está esperando por você!"
    )

    object Second : OnBoardingPage(
        image = R.drawable.image_onboarding2,
        title = "Seja um Explorador\ndo nosso imenso Universo!",
        description = "Descubra os segredos do universo, conheça galáxias e viaje por mundos desconhecidos.\nOnde aprender sobre o espaço se transforma em uma grande aventura. Vamos explorar o céu juntos!"
    )

    object Third : OnBoardingPage(
        image = R.drawable.image_onboarding3,
        title = "Sua missão \ncomeçará em breve!",
        description = "Você está pronto para uma aventura cósmica? Conheça as incríveis imagens dos Planetas do nosso Sistema Solar.\nVamos começar nossa jornada pelo espaço sideral e descobrir as maravilhas que ele esconde!"
    )
}
