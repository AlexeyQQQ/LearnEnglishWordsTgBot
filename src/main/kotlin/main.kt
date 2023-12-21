import java.io.File

const val ANSWERS_TO_STUDY = 3
const val NUMBER_POSSIBLE_ANSWERS = 4

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()
    createDictionaryFile(wordsFile)

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

fun showMenu(dictionary: List<Word>) {
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

fun learningWords(dictionary: List<Word>) {
    while (true) {
        if (dictionary.size < NUMBER_POSSIBLE_ANSWERS) {
            println("В вашем словаре слишком мало слов, добавьте хотя бы $NUMBER_POSSIBLE_ANSWERS!")
            return
        }

        val listOfUnlearnedWords = dictionary.filter { it.correctAnswersCount < ANSWERS_TO_STUDY }.toMutableList()
        if (listOfUnlearnedWords.isEmpty()) {
            println("Слов для изучения больше нет, вы всё выучили!")
            return
        }

        if (listOfUnlearnedWords.size < NUMBER_POSSIBLE_ANSWERS) {
            val listOfLearnedWords = dictionary.filter { it.correctAnswersCount >= ANSWERS_TO_STUDY }.shuffled()
            for (i in 1..NUMBER_POSSIBLE_ANSWERS - listOfUnlearnedWords.size) {
                listOfUnlearnedWords.add(listOfLearnedWords[i - 1])
            }
        }

        val shuffledListWords = listOfUnlearnedWords.shuffled().take(NUMBER_POSSIBLE_ANSWERS)
        var learnWord: Word
        do {
            learnWord = shuffledListWords.random()
        } while (learnWord.correctAnswersCount >= ANSWERS_TO_STUDY)

        println("Слово для изучения: ${learnWord.original}")
        for (i in shuffledListWords.indices) {
            println("${i + 1} - ${shuffledListWords[i].translation}")
        }
        println("0 - Выйти в меню")

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

fun showStatistics(dictionary: List<Word>) {
    val wordsLearned = dictionary.filter { it.correctAnswersCount >= ANSWERS_TO_STUDY }.size
    val wordsTotal = dictionary.size
    val percentageRatio = (wordsLearned.toDouble() / wordsTotal * 100).toInt()
    println("Выучено $wordsLearned из $wordsTotal слов | $percentageRatio%")
}

fun createDictionaryFile(wordsFile: File) {
    wordsFile.writeText(
        """
        hello|привет|3
        dog|собака|3
        cat|кошка|3
        thank you|спасибо|3
        text|текст|3
        news|новость|3
        word|слово|3
        letter|письмо|0
        message|сообщение|0
        note|заметка|0
    """.trimIndent()
    )
}


data class Word(
    val original: String,
    val translation: String,
    val correctAnswersCount: Int = 0,
)
