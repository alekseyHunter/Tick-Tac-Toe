package my.tick_tac_toe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import my.tick_tac_toe.databinding.ActivityMainBinding

const val EXTRA_TIME = "my.tick_tac_toe.TIME"
const val EXTRA_GAME_FIELD = "my.tick_tac_toe.GAME_FIELD"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyTickTacToe)

        super.onCreate(savedInstanceState)
        println("onCreate()")

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.toNewGame.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        binding.toContinueGame.setOnClickListener {
            val gameInfo = getInfoAboutLastGame()
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra(EXTRA_TIME, gameInfo.time)
                putExtra(EXTRA_GAME_FIELD, gameInfo.gameField)
            }
            startActivity(intent)
        }

        binding.toSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        println("onCreate()")
    }

    override fun onDestroy() {
        super.onDestroy()

        println("onDestroy()")
    }

    override fun onStop() {
        super.onStop()

        println("onStop()")
    }

    override fun onStart() {
        super.onStart()
        println("onStart()")
    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart()")
    }

    override fun onResume() {
        super.onResume()
        println("onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("onPause()")
    }

    private fun getInfoAboutLastGame() : GameInfo {
        with(getSharedPreferences("game", MODE_PRIVATE)){
            val time = getLong("time", 0)
            val gameField = getString("gameField", "")

            return if (gameField != null) {
                GameInfo(time, gameField)
            } else {
                GameInfo(0,"")
            }
        }
    }

    data class GameInfo(val time: Long, val gameField: String)
}