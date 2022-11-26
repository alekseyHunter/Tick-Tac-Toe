package my.tick.tack.toe

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import my.tick.tack.toe.databinding.ActivityGameBinding


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    private lateinit var gameField: Array<Array<String>>

    private lateinit var mediaPlayer: MediaPlayer

    private var elapsedMillis = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)

        binding.toGameClose.setOnClickListener {
            onBackPressed()
        }

        binding.toPopupMenu.setOnClickListener {
            showPopupMenu()
        }

        binding.cell11.setOnClickListener {
            nextStepToUser(0, 0)
        }

        binding.cell12.setOnClickListener {
            nextStepToUser(0, 1)
        }

        binding.cell13.setOnClickListener {
            nextStepToUser(0, 2)
        }

        binding.cell21.setOnClickListener {
            nextStepToUser(1, 0)
        }

        binding.cell22.setOnClickListener {
            nextStepToUser(1, 1)
        }

        binding.cell23.setOnClickListener {
            nextStepToUser(1, 2)
        }

        binding.cell31.setOnClickListener {
            nextStepToUser(2, 0)
        }

        binding.cell32.setOnClickListener {
            nextStepToUser(2, 1)
        }

        binding.cell33.setOnClickListener {
            nextStepToUser(2, 2)
        }

        setContentView(binding.root)

        val time = intent.getLongExtra(EXTRA_TIME, 0L)
        val gameField = intent.getStringExtra(EXTRA_GAME_FIELD)

        if(gameField != null && time != 0L && gameField != ""){
            restartGame(time, gameField)
        } else {
            initGameField()
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.test)
        mediaPlayer.isLooping = true

        binding.chronometer.start()
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }
    
    private fun restartGame(time: Long, gameField: String) {
        binding.chronometer.base = SystemClock.elapsedRealtime() - time

        this.gameField = arrayOf()

        val rows = gameField.split("\n")

        for(row in rows){
            val columns = row.split(";")
            this.gameField += columns.toTypedArray()
        }

        this.gameField.forEachIndexed{ i, rows ->
            rows.forEachIndexed { j, column ->
                makeGameFieldUI("$i$j", column)
            }
        }
    }

    private fun convertGameFieldToString(): String {
        val tmpArray = arrayListOf<String>()
        gameField.forEach { tmpArray.add(it.joinToString(separator = ";")) }
        return tmpArray.joinToString(separator = "\n")
    }

    private fun saveGame(time: Long, gameField: String){
        getSharedPreferences("game", MODE_PRIVATE).edit().apply{
            putLong("time", time)
            putString("gameField", gameField)
            apply()
        }
    }

    private fun initGameField(){
        gameField = arrayOf()

        for (i in 0..2) {
            var array = arrayOf<String>()
            for (j in 0..2) {
                array += " "
            }
            gameField += array
        }
    }

    private fun nextStepToUser(row: Int, column: Int){
        if(isEmptyField(row, column)) {
            makeStep(row, column, "X")

            val result = checkWin(row, column, "X")
            if(result.status){
                showGameStatus(STATUS_WIN_PLAYER)
            }

            if(!isFilledGameField()){
                nextStepToAI()
            }
            else {
                showGameStatus(STATUS_DRAW)
            }
        }
        else{
            Toast.makeText(this, "Поле уже заполнено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun nextStepToAI(){
        var randomRow = 0
        var randomColumn = 0

        do {
            randomRow = (0..2).random()
            randomColumn = (0..2).random()
        } while (!isEmptyField(randomRow, randomColumn))

        makeStep(randomRow, randomColumn, "0")

        val result = checkWin(randomRow, randomColumn, "0")
        if(result.status){
            showGameStatus(STATUS_WIN_BOT)
        }
    }

    private fun isEmptyField(row: Int, column: Int): Boolean{
        return gameField[row][column] == " "
    }

    private fun makeStep(row: Int, column: Int, symbol: String){
        gameField[row][column] = symbol

        makeGameFieldUI("$row$column", symbol)
    }

    private fun makeGameFieldUI(position: String, symbol: String){
        val drawable = when (symbol) {
            "X" -> R.drawable.ic_cross
            "0" -> R.drawable.ic_zero
            else -> return
        }

        when(position){
            "00" -> binding.cell11.setImageResource(drawable)
            "01" -> binding.cell12.setImageResource(drawable)
            "02" -> binding.cell13.setImageResource(drawable)
            "10" -> binding.cell21.setImageResource(drawable)
            "11" -> binding.cell22.setImageResource(drawable)
            "12" -> binding.cell23.setImageResource(drawable)
            "20" -> binding.cell31.setImageResource(drawable)
            "21" -> binding.cell32.setImageResource(drawable)
            "22" -> binding.cell33.setImageResource(drawable)
        }
    }

    private fun checkWin(x: Int, y: Int, symbol: String): PlayerInfo{
        var col = 0
        var row = 0
        var diag = 0
        var rdiag=0
        val n = gameField.size

        for(i in 0..2) {
            if (gameField[x][i]==symbol)
                col++
            if (gameField[i][y]==symbol)
                row++
            if (gameField[i][i]==symbol)
                diag++
            if (gameField[i][n - i - 1]==symbol)
                rdiag++
        }

        return if (row == n || col == n || diag == n || rdiag == n)
            PlayerInfo(true, symbol)
        else
            PlayerInfo(false, "")
    }

    private fun isFilledGameField(): Boolean {
        gameField.forEach { strings ->
            if(strings.find { it == " " } != null)
                return false
        }
        return true
    }

    private fun showGameStatus(status: Int){
        binding.chronometer.stop()

        val fbDialogue = Dialog(this@GameActivity, R.style.Theme_MyTickTacToe)
        fbDialogue.window?.setBackgroundDrawable(ColorDrawable(Color.argb(50, 0, 0, 0)))
        fbDialogue.setContentView(R.layout.dialog_popup_status_game)
        fbDialogue.setCancelable(true)

        when (status) {
            STATUS_WIN_BOT -> {
                fbDialogue.findViewById<TextView>(R.id.dialog_text).text = "Вы проиграли!"
                fbDialogue.findViewById<ImageView>(R.id.dialog_image)
                    .setImageResource(R.drawable.status_lose)
            }
            STATUS_WIN_PLAYER -> {
                fbDialogue.findViewById<TextView>(R.id.dialog_text).text = "Вы выиграли!"
                fbDialogue.findViewById<ImageView>(R.id.dialog_image)
                    .setImageResource(R.drawable.status_win)
            }
            STATUS_DRAW -> {
                fbDialogue.findViewById<TextView>(R.id.dialog_text).text = "Ничья!"
                fbDialogue.findViewById<ImageView>(R.id.dialog_image)
                    .setImageResource(R.drawable.status_draw)
            }
        }

        fbDialogue.findViewById<TextView>(R.id.dialog_ok).setOnClickListener {
            fbDialogue.hide()
            onBackPressed()
        }
        fbDialogue.show()
    }

    private fun showPopupMenu() {
        binding.chronometer.stop()

        val elapsedMillis = SystemClock.elapsedRealtime() - binding.chronometer.base

        val fbDialogue = Dialog(this@GameActivity, R.style.Theme_MyTickTacToe)
        fbDialogue.window?.setBackgroundDrawable(ColorDrawable(Color.argb(50, 0, 0, 0)))
        fbDialogue.setContentView(R.layout.dialog_popup_menu)
        fbDialogue.setCancelable(true)

        fbDialogue.findViewById<TextView>(R.id.dialog_continue).setOnClickListener {
            fbDialogue.hide()
            binding.chronometer.base = SystemClock.elapsedRealtime() - elapsedMillis
            binding.chronometer.start()
        }
        fbDialogue.findViewById<TextView>(R.id.dialog_settings).setOnClickListener {
            fbDialogue.hide()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        fbDialogue.findViewById<TextView>(R.id.dialog_exit).setOnClickListener {
            saveGame(elapsedMillis, convertGameFieldToString())
            fbDialogue.hide()
            onBackPressed()
        }

        fbDialogue.show()
    }

    data class PlayerInfo(val status: Boolean, val side: String)

    companion object {
        const val STATUS_WIN_PLAYER = 1
        const val STATUS_WIN_BOT = 2
        const val STATUS_DRAW = 3
    }
}