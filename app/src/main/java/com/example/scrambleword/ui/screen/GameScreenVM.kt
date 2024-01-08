package com.example.scrambleword.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.scrambleword.data.MAX_NO_OF_WORDS
import com.example.scrambleword.data.ScoreIncrease
import com.example.scrambleword.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameScreenVM :ViewModel(){

    //declarations
    //app state
    private val _gameUiState= MutableStateFlow(GameUiState())
    val gameUiState=_gameUiState.asStateFlow()

    var userGuess by mutableStateOf("")

    //guessed words
    private var guessedWords = mutableSetOf<String>()

    private lateinit var currentWord:String

    init{
        resetGame()
    }

    fun resetGame(){
        guessedWords.clear()
        _gameUiState.value= GameUiState(currentScrambledWord = pickRandomWordAndReshuffle())
    }

    fun skipWord() {
        updateGameState(_gameUiState.value.score)
        // Reset user guess
        updateUserGuess("")
    }

    fun updateUserGuess(guess:String){
        userGuess=guess
    }

    fun checkUserGuess(){
        if(userGuess.equals(currentWord,ignoreCase = true)){
            val updatedScore=_gameUiState.value.score.plus(ScoreIncrease)
            updateGameState(updatedScore)
            userGuess=""
        }
    }

    fun updateGameState(newScore:Int){
        if (guessedWords.size == MAX_NO_OF_WORDS){
            //Last round in the game, update isGameOver to true, don't pick a new word
            _gameUiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = newScore,
                    isGameOver = true
                )
            }
        } else{
            // Normal round in the game
            _gameUiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndReshuffle(),
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = newScore
                )
            }
        }
    }

    //function to shuffle the word
    private fun shuffleWord(word:String):String{
        val tempWord=word.toCharArray()
        tempWord.shuffle()
        if(String(tempWord)==word){
            shuffleWord(word)
        }
        return String(tempWord)
    }

    //function to pick a random word,shuffle its characters and return it
    private fun pickRandomWordAndReshuffle():String{

        currentWord= allWords.random()

        if(guessedWords.contains(currentWord)){
            pickRandomWordAndReshuffle()
        }
        return shuffleWord(currentWord)
    }


}