import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class TelegramBotService(
    private val botToken: String,
) {

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$TG_BASE_URL/bot$botToken/getUpdates?offset=$updateId"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(chatId: Int, text: String): String {
        val encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8)
        val urlSendMessage = "$TG_BASE_URL/bot$botToken/sendMessage?chat_id=$chatId&text=$encodedText"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendQuestion(chatId: Int, question: Question): String {
        val urlSendQuestion = "$TG_BASE_URL/bot$botToken/sendMessage"
        var keyboardBody = ""

        question.variants.forEachIndexed { index, word ->
            keyboardBody += """
                [
                    {
                        "text": "${word.translation}",
                        "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + index}"
                    }
                ]
            """.trimIndent()
            if (index + 1 < question.variants.size) keyboardBody += ","
        }

        val sendQuestionBody = """
            {
                "chat_id": $chatId,
                "text": "${question.correctAnswer.original}",
                "reply_markup": {
                    "inline_keyboard": [
                        $keyboardBody
                    ]
                }
            }
        """.trimIndent()

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(urlSendQuestion))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendQuestionBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(chatId: Int): String {
        val urlSendMenu = "$TG_BASE_URL/bot$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Учить слова",
                                "callback_data": "learn_words_menu"
                            }
                        ],
                        [
                            {
                                "text": "Статистика",
                                "callback_data": "statistics_menu"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(urlSendMenu))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    companion object {
        private const val TG_BASE_URL = "https://api.telegram.org"
    }
}