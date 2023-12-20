import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()

    wordsFile.writeText(
        """
        hello|привет|0
        dog|собака|3
        cat|кошка|1
        thank you|спасибо|1
    """.trimIndent()
    )

    val dictionary = mutableListOf<Word>()
    val listOfLines = wordsFile.readLines()

    listOfLines.forEach {
        val line = it.split("|")
        dictionary.add(
            Word(
                original = line[0],
                translation = line[1],
                correctAnswersCount = line[2].toInt()
            )
        )
    }

    showMenu(dictionary)
}

fun showMenu(dictionary: MutableList<Word>) {
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
            "1" -> learningWords(dictionary)
            "2" -> showStatistics(dictionary)
            "0" -> return
            else -> println("Такого варианта не существует!")
        }
    }
}

fun learningWords(dictionary: MutableList<Word>) {
    while (true) {
        val listOfUnlearnedWords = dictionary.filter { it.correctAnswersCount < 3 }
        if (listOfUnlearnedWords.isEmpty()) {
            println("Слов для изучения больше нет, вы всё выучили!")
            return
        }

        val shuffledListWords = listOfUnlearnedWords.shuffled().take(4)
        val learnWord = shuffledListWords.random()

        println("Слово для изучения: ${learnWord.original}\nВведите правильный перевод (цифрой) или 0 для выхода в меню:")
        for (i in shuffledListWords.indices) {
            println("${i + 1} - ${shuffledListWords[i].translation}")
        }

        val userAnswer = readln().toInt()
        if (userAnswer == 0) {
            return
        } else if (userAnswer - 1 == shuffledListWords.indexOf(learnWord)) {
            println("Правильно!")
        } else {
            println("Вы ошиблись!")
        }
    }
}

fun showStatistics(dictionary: MutableList<Word>) {
    val wordsLearned = dictionary.filter { it.correctAnswersCount >= 3 }.size
    val wordsTotal = dictionary.size
    val percentageRatio = (wordsLearned.toDouble() / wordsTotal * 100).toInt()
    println("Выучено $wordsLearned из $wordsTotal слов | $percentageRatio%")
}


data class Word(
    val original: String,
    val translation: String,
    val correctAnswersCount: Int = 0,
)














