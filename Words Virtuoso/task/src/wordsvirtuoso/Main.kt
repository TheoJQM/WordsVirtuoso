package wordsvirtuoso

import java.io.File

class Virtuoso {
    private var file = File("")

    fun play() {
        file = println("Input the words file:").run { File(readln()) }
        when {
            !file.exists() -> println("Error: The words file ${file.name} doesn't exist.")
            else -> checkContent()
        }
    }

    private fun checkContent() {
        var incorrectWords = 0
        val words = file.readLines()
        for (word in words) {
            when {
                word.length != 5 || !Regex("""\p{Alpha}+""").matches(word.trim()) ||
                word.toSet().size != 5-> incorrectWords++
            }
        }
        if (incorrectWords == 0) {
            println("All words are valid!")
        } else {
            println("Warning: $incorrectWords invalid words were found in the ${file.name} file.")
        }
    }

}

fun main() {
    val myGame = Virtuoso()
    myGame.play()
}

