package ru.mygames.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.activity.OnBackPressedDispatcher
import ru.mygames.tictactoe.databinding.ActivitySettingsBinding

const val PREF_SOUND = "ru.mygames.tictactoe.SOUND"
const val PREF_LEVEL = "ru.mygames.tictactoe.LEVEL"
const val PREF_RULES = "ru.mygames.tictactoe.RULES"

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsBinding: ActivitySettingsBinding

    private var currentLevel : Int = 0
    private var currentSoundVolume: Int = 0
    private var currentRules: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)

        val currentSettings = getCurrentSettings()

        currentLevel = currentSettings.level
        currentSoundVolume = currentSettings.sound
        currentRules = currentSettings.rules

        if(currentLevel == 0){
            settingsBinding.prevLvl.visibility = View.INVISIBLE
        } else if (currentLevel == 2) {
            settingsBinding.nextLvl.visibility = View.INVISIBLE
        }

        settingsBinding.infoLevel.text = resources.getStringArray(R.array.level)[currentLevel]
        settingsBinding.soundBar.progress = currentSoundVolume

        when(currentSettings.rules){
            1 -> settingsBinding.checkBoxHorizontal.isChecked = true
            2 -> settingsBinding.checkBoxVertical.isChecked = true
            3 -> {
                settingsBinding.checkBoxHorizontal.isChecked = true
                settingsBinding.checkBoxVertical.isChecked = true
            }
            4 -> settingsBinding.checkBoxDiagonal.isChecked = true
            5 -> {
                settingsBinding.checkBoxDiagonal.isChecked = true
                settingsBinding.checkBoxHorizontal.isChecked = true
            }
            6 -> {
                settingsBinding.checkBoxDiagonal.isChecked = true
                settingsBinding.checkBoxVertical.isChecked = true
            }
            7 -> {
                settingsBinding.checkBoxHorizontal.isChecked = true
                settingsBinding.checkBoxVertical.isChecked = true
                settingsBinding.checkBoxDiagonal.isChecked = true
            }
        }

        settingsBinding.prevLvl.setOnClickListener {
            currentLevel--

            if(currentLevel == 0){
                settingsBinding.prevLvl.visibility = View.INVISIBLE
            } else if (currentLevel == 1) {
                settingsBinding.nextLvl.visibility = View.VISIBLE
            }

            updateLevel(currentLevel)
            settingsBinding.infoLevel.text = resources.getStringArray(R.array.level)[currentLevel]
        }

        settingsBinding.nextLvl.setOnClickListener {
            currentLevel++

            if(currentLevel == 2){
                settingsBinding.nextLvl.visibility = View.INVISIBLE
            } else if (currentLevel == 1) {
                settingsBinding.prevLvl.visibility = View.VISIBLE
            }

            updateLevel(currentLevel)
            settingsBinding.infoLevel.text = resources.getStringArray(R.array.level)[currentLevel]
        }

        settingsBinding.soundBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                currentSoundVolume = progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                updateVolumeSound(currentSoundVolume)
            }

        })

        settingsBinding.checkBoxHorizontal.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                currentRules += 1
            } else {
                currentRules -= 1
            }

            updateRules(currentRules)
        }

        settingsBinding.checkBoxVertical.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                currentRules += 2
            } else {
                currentRules -= 2
            }

            updateRules(currentRules)
        }

        settingsBinding.checkBoxDiagonal.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                currentRules += 4
            } else {
                currentRules -= 4
            }

            updateRules(currentRules)
        }

        settingsBinding.toBack.setOnClickListener {
            setResult(RESULT_OK)
            onBackPressedDispatcher.onBackPressed()
        }

        setContentView(settingsBinding.root)
    }

    private fun updateVolumeSound(volume: Int){
        getSharedPreferences("game", MODE_PRIVATE).edit().apply{
            putInt(PREF_SOUND, volume)
            apply()
        }
        setResult(RESULT_OK)
    }

    private fun updateLevel(level: Int){
        getSharedPreferences("game", MODE_PRIVATE).edit().apply {
            putInt(PREF_LEVEL, level)
            apply()
        }
        setResult(RESULT_OK)
    }

    private fun updateRules(rules: Int){
        getSharedPreferences("game", MODE_PRIVATE).edit().apply {
            putInt(PREF_RULES, rules)
            apply()
        }
        setResult(RESULT_OK)
    }

    private fun getCurrentSettings(): GameSettings {
        this.getSharedPreferences("game", MODE_PRIVATE).apply {

            val sound = getInt(PREF_SOUND, 100)
            val level = getInt(PREF_LEVEL, 1)
            val rules = getInt(PREF_RULES, 7)

            return GameSettings(sound, level, rules)
        }
    }

    data class GameSettings(val sound: Int, val level: Int, val rules: Int)
}