package com.example.project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.project1.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    var p1Score : Int = 0 //full overall score of p1
    var p2Score :Int = 0 //overall score of p2
    var p1Turn : Boolean = false //indicates if it is p1's turn
    var p2Turn : Boolean = false //indicates if it is p2's turn
    var turnTotal :Int = 0
    var D1 = 0 //value of the first rolled die
    var D2 = 0 //value of the second rolled die

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.p1TotalText.text = "Player 1 Total: 0"
        binding.p2TotalText.text = "Player 2 Total: 0"
        binding.currentPText.text = "Current Player: P1"
        binding.turnTotalText.text = "Turn Total: 0"
        binding.rollButton.text = "Roll Dice"
        binding.holdButton.text = "Hold"
        binding.die1.setImageResource(R.drawable.dieq)
        binding.die2.setImageResource(R.drawable.dieq)
        p1Turn = true
    }

    fun RollListener(view:View){
        binding.errorText.text = " "
        binding.holdButton.isClickable = true


        var rolls = GenerateRoll()
        D1 = rolls[0]
        D2 = rolls[1]
        ChangeDiceImg(D1, D2)
        if(p1Turn == true) {
            binding.d1Val.text = "P1 Die#1: " + D1.toString()
            binding.d2Val.text = "P1 Die#2: " + D2.toString()
        }
        else if(p2Turn == true) {
            binding.d1Val.text = "P2 Die#1: " + D1.toString()
            binding.d2Val.text = "P2 Die#2: " + D2.toString()
        }

        if (p1Turn == true && p2Turn == false){
            binding.currentPText.text = "Current Player: P1"

            var keepTurn = Rules(D1, D2, p1Score, 1)
            binding.turnTotalText.text = "Turn Total: " + turnTotal.toString()
            if(keepTurn == false) //player 1's turn is over
                EndTurn()
        }
        else if (p2Turn == true && p1Turn == false){
            binding.currentPText.text = "Current Player: P2"

            var keepTurn = Rules(D1, D2, p2Score, 2)
            binding.turnTotalText.text = "Turn Total: " + turnTotal.toString()
            if(keepTurn == false) //player 1's turn is over
                EndTurn()
        }
    }

    fun HoldListener(view:View) {
        binding.errorText.text = " "
        if (p1Turn == true && p2Turn == false){
            binding.errorText.text = "Player chose to hold."
            CheckGameOver(p1Score, 1) //checks if the game is over once a turn ends -- makes the game no longer playable
            EndTurn()
        }
        else if (p2Turn == true && p1Turn == false){
            binding.errorText.text = "Player chose to hold."
            CheckGameOver(p2Score, 2) //checks if the game is over once a turn ends -- makes the game no longer playable
            EndTurn()
        }
    }

    fun GenerateRoll(): List<Int> {
        var d1: Int = Random.nextInt(1, 7)
        var d2: Int = Random.nextInt(1, 7)
        val dice = listOf<Int>(d1, d2)
        return dice
    }

    fun Rules(die1: Int, die2: Int, pScore: Int, pNum: Int): Boolean { //return keepTurn -- oncreate will use if statement to decide to switch turns
        var keepTurn: Boolean = true
        if (die1 == 1 && die2 == 1) { //rule#2
            keepTurn = false
            if (pNum == 1)
                p1Score = 0
            else if (pNum == 2)
                p2Score = 0
            turnTotal = 0
            binding.errorText.text = "Oops - you got snake eyes :( Your turn is over. So is your score, tragic. Next person rolls! (Sorry, P" + pNum.toString() + ")"
            //turn ends; player's score (aScore - can pass in p1 or p2Score) is erased
        }
        else if ((die1 == 1 && die2 != 1) || (die1 != 1 && die2 == 1)) { //rule#1
            keepTurn = false
            turnTotal = 0
            binding.errorText.text = "Oops - you rolled a 1. Your turn is over. Next person rolls! (Sorry, P" + pNum.toString() + ")"
            //add nothing
        }
        else if (die1 == die2) { //rule#5
            keepTurn = true
            turnTotal = turnTotal + die1 + die2
            binding.errorText.text = "Great! You have to roll again (P" + pNum.toString() + ")."
            binding.holdButton.isClickable = false
            //re roll is required  //add die1 and die2 to aScore
        }
        else if (die1 != 1 && die2 != 1) { //rule#3
            keepTurn = true
            turnTotal = turnTotal + die1 + die2
            binding.errorText.text = "Great! You can roll again (P" + pNum.toString() + ")."
            //add die1 and die2 to aScore  //rereoll not required; can hold
        }
        return keepTurn
    }

    //Runs thru the steps for ending a turn
        //switch player turns; updates p1 total; resets turn total;  update p1 total, current player, turn total texts;  also checks if anyone has won
    fun EndTurn() {
        if(p1Turn == true && p2Turn == false) {
            p1Score += turnTotal //turn total is finally added to player total
            turnTotal = 0 //turn total must be reset at the end of a turn
            p1Turn = false
            p2Turn = true
            binding.p1TotalText.text = "Player 1 Total: " + p1Score.toString()
            binding.currentPText.text = "Current Player: P2"
            binding.turnTotalText.text = "Turn Total: 0"
        }
        else if (p2Turn == true && p1Turn == false) {
            p2Score += turnTotal //turn total is finally added to player total
            turnTotal = 0 //turn total must be reset at the end of a turn
            p2Turn = false
            p1Turn = true
            binding.p2TotalText.text = "Player 2 Total: " + p2Score.toString()
            binding.currentPText.text = "Current Player: P1"
            binding.turnTotalText.text = "Turn Total: 0"
        }
    }

    //checks if the game is over once a turn ends -- makes the game no longer playable
    fun CheckGameOver(playerScore : Int, pNum : Int) : Boolean{
        var gameOver : Boolean = false
        if((playerScore + turnTotal) >= 50){
            gameOver = true
            binding.holdButton.isClickable = false
            binding.rollButton.isClickable = false
            binding.errorText.text = "Player " + pNum.toString() + " wins! Score: " + (playerScore + turnTotal).toString()
        }
        return gameOver
    }

    fun ChangeDiceImg(dval : Int, d2val : Int){
        if (dval == 1)
            binding.die1.setImageResource(R.drawable.die1)
        else if (dval == 2)
            binding.die1.setImageResource(R.drawable.die2)
        else if (dval == 3)
            binding.die1.setImageResource(R.drawable.die3)
        else if (dval == 4)
            binding.die1.setImageResource(R.drawable.die4)
        else if (dval == 5)
            binding.die1.setImageResource(R.drawable.die5)
        else if (dval == 6)
            binding.die1.setImageResource(R.drawable.die6)

        if (d2val == 1)
            binding.die2.setImageResource(R.drawable.die1)
        else if (d2val == 2)
            binding.die2.setImageResource(R.drawable.die2)
        else if (d2val == 3)
            binding.die2.setImageResource(R.drawable.die3)
        else if (d2val == 4)
            binding.die2.setImageResource(R.drawable.die4)
        else if (d2val == 5)
            binding.die2.setImageResource(R.drawable.die5)
        else if (d2val == 6)
            binding.die2.setImageResource(R.drawable.die6)
    }
}