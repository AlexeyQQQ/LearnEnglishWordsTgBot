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
    println(
        """
        Меню:
        1 - Учить слова
        2 - Статистика
        0 - Выход
    """.trimIndent()
    )
    while (true) {
        println("Введите цифру:")
        when (readln()) {
            "1" -> println("Скоро тут будут слова")
            "2" -> showStatistics(dictionary)
            "0" -> return
            else -> println("Такого варианта не существует!")
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