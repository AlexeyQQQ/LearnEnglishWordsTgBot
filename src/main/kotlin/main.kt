fun main() {
    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Ошибка при загрузке словаря")
        return
    }

    while (true) {
        println(
            """
            Меню:
            1 - Учить слова
            2 - Статистика
            0 - Выход
            Введите цифру:
        """.trimIndent()
        )
        when (readln()) {
            "1" -> learningWords(trainer)
            "2" -> showStatistics(trainer)
            "0" -> return
            else -> println("Такого варианта не существует!")
        }
    }
}

fun learningWords(trainer: LearnWordsTrainer) {
    while (true) {
        val question = trainer.getNextQuestion()
        if (question == null) {
            println("Слов для изучения нет, вы всё выучили!")
            return
        }

        println("Слово для изучения: ${question.correctAnswer.original}")
        for (i in question.variants.indices) {
            println("${i + 1} - ${question.variants[i].translation}")
        }
        println("0 - Выйти в меню")

        val userAnswer = readln().toIntOrNull()
        if (userAnswer == 0) return

        if (trainer.checkAnswer(userAnswer)) {
            println("Правильно!\n")
        } else {
            println("Вы ошиблись! Правильный ответ: ${question.correctAnswer.translation}.\n")
        }
    }
}

fun showStatistics(trainer: LearnWordsTrainer) {
    val statistics = trainer.showStatistics()
    println("Выучено ${statistics.wordsLearned} из ${statistics.wordsTotal} слов | ${statistics.percentageRatio}%")
}