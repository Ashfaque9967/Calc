package com.example.calc

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import com.example.calc.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import soup.neumorphism.NeumorphButton
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var Expression : Expression

    private var canAddOperator = false
    private var canAddDecimal = true
    private var nextToOperator = false
    private var equalpressed = false
    private var showResult = false



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.clrbtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Button hold
                    binding.clrbtn.setTextColor(Color.RED)
                    binding.clrbtn.setShapeType(1)
                    clear()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // Button unhold
                    binding.clrbtn.setTextColor(Color.rgb(0, 48, 143))
                    binding.clrbtn.setShapeType(0)
                    true
                }
                else -> false
            }
        }

        binding.inputTV.movementMethod = ScrollingMovementMethod()
        binding.inputTV.isActivated = true
        binding.inputTV.isPressed = true

    }

    fun clear() {

        binding.inputTV.text.clear()
        binding.resultTV.text.clear()

        canAddOperator = false
        canAddDecimal = true
        showResult = false
        nextToOperator = false


    }

    fun backspace(view: View) {
        val length = binding.inputTV.length()

        if (length > 0) {
            val last = binding.inputTV.text.toString().last()

            if (last.isDigit()) {
                canAddDecimal = true
                canAddOperator = false

                    if (length>1){
                        val secondLast = binding.inputTV.text.toString()[length - 2]
                        if (secondLast.isDigit()){
                            canAddOperator = true
                        }
                        if (secondLast == '+' || secondLast == '-' || secondLast == '/' || secondLast == '*' || secondLast == '^'){
                            nextToOperator = true
                        }
                        if (secondLast == '.'){
                            canAddDecimal = false
                        }
                    }
            } else if (last == '+' || last == '-' || last == '/' || last == '*' || last == '^') {
                canAddOperator = true
                nextToOperator = false
                if (length>1){
                    val secondLast = binding.inputTV.text.toString()[length - 2]

                    if (secondLast.isDigit()){
                        canAddOperator = true
                    }
                    if (secondLast == '+' || secondLast == '-' || secondLast == '/' || secondLast == '*' || secondLast == '^'){
                        nextToOperator = true
                    }
                    if (secondLast == '.' || secondLast-1 == '.'|| secondLast-2 == '.'){
                        canAddDecimal = false
                    }
                }

            } else if (last == '.') {
                canAddDecimal = true
                canAddOperator = true
            }

            binding.inputTV.text = binding.inputTV.text.delete(length - 1, length)
        }

        try {
            val lastChar = binding.inputTV.text.toString().last()
            if (lastChar.isDigit()) {
                equalto()
            }
        } catch (e: Exception) {
            binding.resultTV.text.clear()
            Log.e("last char error", e.toString())
        }
    }

    fun EqualTo(view: View) {

        if (!(binding.resultTV.text.isEmpty())){
            binding.inputTV.setText(binding.resultTV.text)
            binding.resultTV.text.clear()
        }
        else {
            binding.inputTV.setText(binding.inputTV.text)
        }
        equalpressed = true

        canAddOperator = false
        canAddDecimal = true
        showResult = false
        nextToOperator = false


    }

    fun numberAction(view: View)
    {
        if (view is Button){

            if (equalpressed){
                clear()
                equalpressed = false
            }
            if (binding.inputTV.length()>10){
            }

            if (canAddDecimal && view.text == "."){
                if ((binding.inputTV.length() == 0 || nextToOperator)){
                    binding.inputTV.append("0.")
                    nextToOperator = false
                    canAddDecimal = false
                }
                else{
                    binding.inputTV.append((view as Button).text)
                    canAddDecimal = false
                    canAddOperator = false
                }
            }
            if (view.text != "."){
                binding.inputTV.append(view.text)
                nextToOperator = false
                canAddOperator = true
            }
            if (binding.inputTV.length()>0 && showResult){
                equalto()
            }

        }

    }

    fun OprationAction(view: View)
    {

        if (view is Button && canAddOperator){

            if (equalpressed){
                clear()
                equalpressed = false
            }

            binding.inputTV.append(view.text)

            nextToOperator = true
            canAddOperator = false
            canAddDecimal = true
            showResult = true

        }

    }

    fun equalto(){


        val text = binding.inputTV.text.toString()
        val check1 = text.replace("÷", "/")
        val check2 = check1.replace("×", "*")

        Expression = ExpressionBuilder(check2).build()

        try {

            val result = Expression.evaluate().toString().toBigDecimal()
            val check = "%.2f".format(result)
            var finalResult = check
            if (check.endsWith(".00"))
                finalResult = check.removeSuffix(".00")
            binding.resultTV.setText(finalResult)
            if (finalResult.length>17){
                binding.resultTV.setText("INFINITY")
            }
        }catch (e : Exception){
            Log.e("Evaluation error!!!", e.toString())
        }
    }


}
