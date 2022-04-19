package com.example.foodroller

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.foodroller.databinding.ActivityMainBinding
import java.util.*

object GlobalData {
    var foods = 11
}

// TODO: Add a grid of toggle-able buttons for each food option and update functionality

// Roll a dice using a button and display the result as text + image
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("Main", "Beginning main activity.")

        // Initialize
        // Create menu of Foods
        val menu = arrayOf(
            Food("Get Cookin'", R.drawable.cook),
            Food("Burgers", R.drawable.burger),
            Food("Burritos", R.drawable.burrito),
            Food("Chinese", R.drawable.chinese),
            Food("Gyros", R.drawable.greek),
            Food("Indian", R.drawable.indian),
            Food("Pho", R.drawable.pho),
            Food("Pizza", R.drawable.pizza),
            Food("Ramen", R.drawable.ramen),
            Food("Sushi", R.drawable.sushi),
            Food("Tacos", R.drawable.taco),
            Food("Thai", R.drawable.thai)
            )
        GlobalData.foods = menu.size - 1
        Log.i("Main", "Created array of Food objects.")
        setStrings()
        binding.centerImage.setImageResource(R.drawable.start)

        // Button functionality
        binding.rollButton.setOnClickListener {
            Log.i("RollButton", "Sending toast then rolling dice.")
            Toast.makeText(this, "Dinner time!", Toast.LENGTH_SHORT).show()
            // Roll the dice
            val rollValue = rollDice(menu)
            Log.i("RollButton", "Successfully returned $rollValue from roll.")

            binding.rollButton.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({binding.rollButton.isEnabled = true}, 2500)
        }
        Log.i("Main", "Button listener set.")
    }

    private fun rollDice(menu: Array<Food>): Int {
        // Update strings if needed
        val pref = getSharedPreferences("MyPref", 0)
        val edit = pref.edit()

        // Stores modified value to Shared Preferences
        var cookChance = binding.inputChanceValue.text.toString().toIntOrNull()
        if(cookChance == null || cookChance < 0)
        {
            cookChance = 0
        } else if (cookChance > 10000) {
                edit.putString("unluckyChance", "10000")
        } else
            edit.putString("unluckyChance", binding.inputChanceValue.text.toString())
        edit.apply()

        // Get a dice roll
        var rollVal = Dice(GlobalData.foods + cookChance).roll()
        Log.i("Roller", "Rolled a $rollVal.")
        // Any roll > total # food options means they got unlucky
        // Set the value to 0 -> they have to stay home and cook
        if(rollVal > GlobalData.foods) {
            Log.i("Roller", "Setting value to 0 (cook).")
            rollVal = 0
        }

        // Display image of food
        val diceImage = binding.centerImage
        diceImage.setImageResource(menu[rollVal].image)
        diceImage.contentDescription = menu[rollVal].name

        // Display text
        val rollDisplay = binding.rollValue
        rollDisplay.text = menu[rollVal].name
        Log.i("Roller", "Set resources for: ${menu[rollVal].name}")

        return rollVal
    }

    // Set the strings used in the app to the appropriate values
    // Uses Shared Preferences to store user-dependent strings
    private fun setStrings() {
        val pref = getSharedPreferences("MyPref", 0)
        val inputChance = binding.inputChanceValue
        val currentFoodCount = binding.currentFoodsCount

        // Load strings
        val chanceString = pref.getString("unluckyChance", "0")
        inputChance.setText(chanceString)
        currentFoodCount.text = GlobalData.foods.toString()
    }
}

class Food(val name: String, val image: Int) {}

// Dice class, can have any number of sides
class Dice(private val numSides: Int) {
    // Return random number between 1 and the max # of sides
    // Number of sides must be >= 1
    fun roll(): Int {
        Log.i("Dice", "Rolling dice with $numSides sides.")
        return (1..numSides).random()
    }
}