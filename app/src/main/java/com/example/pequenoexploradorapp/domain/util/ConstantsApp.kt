package com.example.pequenoexploradorapp.domain.util

class ConstantsApp {
        companion object {
            const val PASSWORD_MAX_NUMBER = 6
            const val PHONE_MAX_NUMBER = 11
            const val NAME_MAX_NUMBER = 25
            const val ERROR_API = "Não foi possível exibir os dados, por favor, tente mais tarde."
            const val ERROR_SERVER = "Não foi possível se connectar ao Servidor de Imagens da Nasa."
            const val ERROR_WITHOUT_NETWORK = "Não há internet disponível, verifique seu WIFI ou Dados conectado."
            const val ERROR_WITHOUT_INTERNET = "Seu dispositivo está sem acesso a internet"
            const val ERROR_SIGN_IN = "Não foi possível acessar a sua conta, por favor, tente mais tarde."
            const val SUCCESS_SIGN_IN = "Login realizado com Sucesso!"
            const val ERROR_DELETE_ACCOUNT = "Não foi possível deletar a sua conta, por favor, tente mais tarde."
            const val ERROR_SIGN_OUT = "Não foi possível sair da sua conta, por favor, tente mais tarde."
            const val SUCCESS_DELETE_ACCOUNT = "Sua conta foi excluída com sucesso."
            const val ERROR_CREATE_ACCOUNT = "Não foi possível criar a sua conta, por favor, tente mais tarde."
            const val SUCCESS_CREATE_ACCOUNT = "Conta criada com Sucesso! Iremos direcionar ao menu do aplicativo."
            const val MESSAGE_DELETE_ACCOUNT = "Deseja realmente excluir a conta?"
            const val MESSAGE_SIGN_OUT_ACCOUNT = "Deseja realmente sair da conta?"
            const val SUCCESS_RESET_PASSWORD = "Em breve receberá um email para cadastrar uma nova senha."
            const val ERROR_RESET_PASSWORD = "Verifique o email digitado."
            const val DEFAULT_NOTIFICATION = "Você receberá novas notificações em breve."
        }
}
