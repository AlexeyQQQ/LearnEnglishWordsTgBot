import java.io.File

data class Word(
    val original: String,
    val translation: String,
    var correctAnswersCount: Int = 0,
)

data class Statistics(
    val wordsLearned: Int,
    val wordsTotal: Int,
    val percentageRatio: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)


class LearnWordsTrainer(
    private val answerToStudy: Int = 3,
    private val countOfQuestionWords: Int = 4,
) {
    var question: Question? = null
    private val dictionary = loadDictionary()

    fun getNextQuestion(): Question? {
        var listOfUnlearnedWords = dictionary.filter { it.correctAnswersCount < answerToStudy }
        if (listOfUnlearnedWords.isEmpty()) {
            return null
        }

        if (listOfUnlearnedWords.size < countOfQuestionWords) {
            val listOfLearnedWords = dictionary
                .filter { it.correctAnswersCount >= answerToStudy }
                .shuffled()
                .take(countOfQuestionWords - listOfUnlearnedWords.size)
            listOfUnlearnedWords = listOfUnlearnedWords + listOfLearnedWords
        }

        val shuffledListWords = listOfUnlearnedWords.shuffled().take(countOfQuestionWords)
        var correctAnswer: Word
        do {
            correctAnswer = shuffledListWords.random()
        } while (correctAnswer.correctAnswersCount >= answerToStudy)

        question = Question(shuffledListWords, correctAnswer)
        return question
    }

    fun checkAnswer(userAnswer: Int?): Boolean {
        return question?.let {
            if (userAnswer == it.variants.indexOf(it.correctAnswer) + 1) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else {
                false
            }
        } ?: false
    }

    fun showStatistics(): Statistics {
        val wordsLearned = dictionary.filter { it.correctAnswersCount >= answerToStudy }.size
        val wordsTotal = dictionary.size
        val percentageRatio = (wordsLearned.toDouble() / wordsTotal * 100).toInt()
        return Statistics(wordsLearned, wordsTotal, percentageRatio)
    }

    private fun loadDictionary(): List<Word> {
        val wordsFile = File("words.txt")
        val dictionary = mutableListOf<Word>()
        val listOfLines = wordsFile.readLines()

        listOfLines.forEach {
            val line = it.split("|")
            dictionary.add(
                Word(
                    original = line[0],
                    translation = line[1],
                    correctAnswersCount = line[2].toIntOrNull() ?: 0
                )
            )
        }

        return dictionary
    }

    private fun saveDictionary(dictionary: List<Word>) {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        dictionary.forEach {
            wordsFile.appendText("${it.original}|${it.translation}|${it.correctAnswersCount}\n")
        }
    }
}