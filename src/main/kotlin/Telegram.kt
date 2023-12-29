const val START_BOT = "/start"
const val LEARN_WORDS_MENU = "learn_words_menu"
const val STATISTICS_MENU = "statistics_menu"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

fun main(args: Array<String>) {

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    var updateId = 0

    val updateIdRegex = "\"update_id\":([0-9]+),".toRegex()
    val chatIdRegex = "\"chat\":\\{\"id\":([0-9]+),".toRegex()
    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
    val dataRegex = "\"data\":\"(.+?)\"".toRegex()

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Ошибка при загрузке словаря")
        return
    }

    while (true) {
        Thread.sleep(2000)
        val updates = tgBotService.getUpdates(updateId)
        val lastUpdateId = parseString(updateIdRegex, updates)?.toInt()

        println(updates)
        println("updateId: $updateId")

        lastUpdateId?.let {
            updateId = lastUpdateId + 1
            val text = parseString(messageTextRegex, updates)
            val chatId = parseString(chatIdRegex, updates)?.toInt()
            val data = parseString(dataRegex, updates)

            println("text: $text")
            println("chatId: $chatId\n")

            if (text?.lowercase() == START_BOT && chatId != null) {
                tgBotService.sendMenu(chatId)
            }

            if (data?.lowercase() == LEARN_WORDS_MENU && chatId != null) {
                checkNextQuestionAndSend(tgBotService, trainer, chatId)
            }

            if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true && chatId != null) {
                val userAnswer = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
                if (trainer.checkAnswer(userAnswer + 1)) {
                    tgBotService.sendMessage(chatId, "Правильно!")
                } else {
                    tgBotService.sendMessage(
                        chatId, "Вы ошиблись! Правильный ответ:" +
                                " ${trainer.question?.correctAnswer?.original} " +
                                "- ${trainer.question?.correctAnswer?.translation}."
                    )
                }
                checkNextQuestionAndSend(tgBotService, trainer, chatId)
            }

            if (data?.lowercase() == STATISTICS_MENU && chatId != null) {
                val statistics = trainer.showStatistics()
                val statisticsString = "Выучено ${statistics.wordsLearned} из ${statistics.wordsTotal} слов " +
                        "| ${statistics.percentageRatio}%"
                tgBotService.sendMessage(chatId, statisticsString)
            }
        }
    }
}

fun checkNextQuestionAndSend(tgBotService: TelegramBotService, trainer: LearnWordsTrainer, chatId: Int) {
    val question = trainer.getNextQuestion()
    question?.let {
        tgBotService.sendQuestion(chatId, question)
    } ?: tgBotService.sendMessage(chatId, "Слов для изучения нет, вы всё выучили!")
}

fun parseString(regex: Regex, updates: String): String? {
    val matchResult = regex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value
}