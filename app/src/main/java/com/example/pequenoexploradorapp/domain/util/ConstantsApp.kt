package com.example.pequenoexploradorapp.domain.util

class ConstantsApp {
        companion object {
            const val PASSWORD_MAX_NUMBER = 6
            const val PHONE_MAX_NUMBER = 11
            const val NAME_MAX_NUMBER = 25
            const val ERROR_API = "Não foi encontrado dados para esta requisição"
            const val ERROR_RESPONSE = "Não foi possível receber os dados, tente novamente mais tarde"
            const val ERROR_SERVER = "Não foi possível se connectar ao servidor, tente novamente mais tarde"
            const val ERROR_EXCEPTION = "Houve um erro inesperado, tente mais tarde"
            const val ERROR_SERIALIZATION = "Não foi possível exibir os dados, por favor, tente mais tarde"
            const val ERROR_REMOTE_CONFIG = "Erro de conexão, tente novamente mais tarde"
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
            const val DEFAULT_ERROR_REMOVE_DB = "Não foi possível remover a imagem aos favoritos"
            const val EMPTY_FAVOURITE_DB = "Não há imagens adicionadas dos favoritos"
            const val DELETE_FAVOURITE_IMAGE = "Gostaria realmente de apagar essa imagem?"
            const val DELETE_FAVOURITE_IMAGE_YES = "Sim"
            const val SHARE_IMAGE = "Imagem compartilhada pelo aplicativo Pequeno Explorador"
            const val SAVE_IMAGE_OK = "A imagem foi salva na galeria do seu celular"
            const val SAVE_IMAGE_ERROR = "Não foi possível salvar a imagem na galeria do celular"
            const val SAVE_IMAGE_ERROR_DECODE = "Arquivo da imagem não é compátivel"
            const val VIDEO_ERROR_LOAD = "Infelizmente, o video não é compátivel"
            const val DATE_INITIAL_APOD = "16.06.1995"
            const val TAG_FIREBASE_MESSAGING = "FIREBASE CLOUD MESSAGING"
            const val CHANNEL_FIREBASE_CLOUD_MESSAGING = "CHANNEL FIREBASE CLOUD MESSAGING"
        }
}
