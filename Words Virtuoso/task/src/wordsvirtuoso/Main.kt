package wordsvirtuoso

import java.io.File

const val green = "\u001B[48:5:10m%s\u001B[0m"
const val yellow = "\u001B[48:5:11m%s\u001B[0m"
const val grey = "\u001B[48:5:7m%s\u001B[0m"
const val azure = "\u001B[48:5:14m%s\u001B[0m"

class Virtuoso(args: Array<String>) {
    private var wordsFile = File("")
    private var candidateFile = File("")
    private var exit = true

    private val tries = mutableListOf<String>()
    private var unusedLetters = mutableSetOf<String>()
    private var nbGuess = 0
    private var time: Long = 0

    init {
        if (args.size != 2) {
            println("Error: Wrong number of arguments.")
        } else {
            wordsFile = File(args[0])
            candidateFile = File(args[1])
            checkFiles()
        }
    }

    private fun checkFiles() {
        val goodWordsFile = checkExistence(wordsFile, "words")
        val goodCandidateFile = checkExistence(candidateFile, "candidate words")

        if (goodCandidateFile && goodWordsFile) {
            val nbExcludedWord = checkCandidateWordIncluded()
            if (nbExcludedWord == 0) {
                println("Words Virtuoso\n")
                play()
            } else {
                println("Error: $nbExcludedWord candidate words are not included in the ${wordsFile.name} file.")
            }
        }
    }

    private fun checkExistence(file: File, fileType: String): Boolean {
        return if (file.exists()) {
            checkContent(file)
        } else {
            println("Error: The $fileType file ${file.name} doesn't exist.")
            false
        }
    }

    private fun checkContent(file: File): Boolean {
        val words = file.readLines()
        val incorrectWords =
            words.count { it.length != 5 || !Regex("""\p{Alpha}+""").matches(it.trim()) || it.toSet().size != 5 }

        if (incorrectWords == 0) {
            return true
        } else {
            println("Error: $incorrectWords invalid words were found in the ${file.name} file.")
            return false
        }
    }

    private fun checkCandidateWordIncluded() = candidateFile.readLines().map { it.lowercase() }.count { it !in wordsFile.readLines().map { word -> word.lowercase() } }



    private fun play() {
        val secretWord = candidateFile.readLines().random()

        time = System.currentTimeMillis()
        while (exit) {
            val guess = println("Input a 5-letter word:").run { readln() }
            nbGuess++
            checkUserGuess(guess, secretWord)
        }
    }

    private fun checkUserGuess(guess: String, secretWord: String) {
        when {
            guess == "exit" -> exit(false)
            guess.length != 5 -> println("The input isn't a 5-letter word.")
            !Regex("""\p{Alpha}+""").matches(guess.trim()) -> println("One or more letters of the input aren't valid.")
            guess.toSet().size != 5  -> println("The input has duplicate letters.")
            guess.lowercase() !in wordsFile.readLines() -> println("The input word isn't included in my words list.")
            else -> compareSecretAndGuess(guess, secretWord)
        }
    }

    private fun compareSecretAndGuess(guess: String, secretWord: String) {
        var response = ""
        for (index in guess.indices) {
            val letter = guess[index]
            response += when (letter) {
                secretWord[index] -> { green.format(letter.uppercase())  }
                in secretWord -> yellow.format(letter.lowercase())
                else ->  addWrongLetter(letter.toString())
            }
        }
        tries.add(response)
        println("\n${tries.joinToString("\n")}\n")

        val wrongLetters = unusedLetters.joinToString("")
        if (guess.lowercase() == secretWord) exit() else println("${azure.format(wrongLetters)}\n")
    }

    private fun addWrongLetter(letter: String): String {
        unusedLetters.add(letter.uppercase())
        unusedLetters = unusedLetters.sorted().toMutableSet()
        return grey.format(letter.uppercase())
    }

    private fun exit(wordGuessed: Boolean = true) {
        exit = false
        val timerStop = System.currentTimeMillis()
        time = (timerStop - time) / 1000

        if (!wordGuessed) {
            println("\nThe game is over.")
        } else {
            println("Correct!")
            if (nbGuess == 1) {
                println("Amazing luck! The solution was found at once.")
            } else {
                println("The solution was found after $nbGuess tries in $time seconds.")
            }
        }
    }
}

fun main(args: Array<String>) {
    Virtuoso(args)
}