import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()

    wordsFile.writeText(
        """
        hello привет
        dog собака
        cat кошка
    """.trimIndent()
    )

    val listOfLines = wordsFile.readLines()
    listOfLines.forEach { println(it) }
}