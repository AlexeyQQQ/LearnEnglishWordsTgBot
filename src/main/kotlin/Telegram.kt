fun main(args: Array<String>) {

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates = tgBotService.getUpdates(updateId)
        val lastUpdateId = getUpdateId(updates)

        println(updates)
        println("updateId: $updateId")

        if (lastUpdateId == -1) {
            continue
        } else {
            updateId = lastUpdateId + 1
            val text = getMessageText(updates)
            val chatId = getChatId(updates)

            println("text: $text")
            println("chatId: $chatId")

            tgBotService.sendMessage(chatId, text)
        }
    }
}

fun getUpdateId(updates: String): Int {
    val updateIdRegex = "\"update_id\":([0-9]+),".toRegex()
    val matchResult = updateIdRegex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value?.toInt() ?: -1
}

fun getChatId(updates: String): Int {
    val chatIdRegex = "\"chat\":\\{\"id\":([0-9]+),".toRegex()
    val matchResult = chatIdRegex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value?.toInt() ?: -1
}

fun getMessageText(updates: String): String {
    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
    val matchResult = messageTextRegex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value ?: ""
}