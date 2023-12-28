fun main(args: Array<String>) {

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    var updateId = 0

    val updateIdRegex = "\"update_id\":([0-9]+),".toRegex()
    val chatIdRegex = "\"chat\":\\{\"id\":([0-9]+),".toRegex()
    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates = tgBotService.getUpdates(updateId)
        val lastUpdateId = getId(updateIdRegex, updates)

        println(updates)
        println("updateId: $updateId")

        if (lastUpdateId == -1) {
            continue
        } else {
            updateId = lastUpdateId + 1
            val text = getMessageText(messageTextRegex, updates)
            val chatId = getId(chatIdRegex, updates)

            println("text: $text")
            println("chatId: $chatId")

            tgBotService.sendMessage(chatId, text)
        }
    }
}

fun getId(regex: Regex, updates: String): Int {
    val matchResult = regex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value?.toInt() ?: -1
}

fun getMessageText(regex: Regex, updates: String): String {
    val matchResult = regex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value ?: ""
}