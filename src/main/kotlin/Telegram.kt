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
        val lastUpdateId = parseString(updateIdRegex, updates)?.toInt()

        println(updates)
        println("updateId: $updateId")

        if (lastUpdateId == null) {
            continue
        } else {
            updateId = lastUpdateId + 1
            val text = parseString(messageTextRegex, updates)
            val chatId = parseString(chatIdRegex, updates)?.toInt()

            println("text: $text")
            println("chatId: $chatId")

            if (text == null || chatId == null) {
                continue
            } else {
                tgBotService.sendMessage(chatId, text)
            }
        }
    }
}

fun parseString(regex: Regex, updates: String): String? {
    val matchResult = regex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value
}