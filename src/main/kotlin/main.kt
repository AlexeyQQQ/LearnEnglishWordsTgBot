import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()

    wordsFile.writeText(
        """
        hello|привет|0
        dog|собака|2
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

    dictionary.forEach { println(it) }
}


data class Word(
    val original: String,
    val translation: String,
    val correctAnswersCount: Int = 0,
)