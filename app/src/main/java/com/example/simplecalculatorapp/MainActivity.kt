package com.example.simplecalculatorapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import java.util.Stack

class MainActivity : AppCompatActivity() {

    private var lastNumeric: Boolean = false
    private var lastDot: Boolean = false
    private var stateError: Boolean = false
    private var lastOperation: Char = ' '

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textInput = findViewById<EditText>(R.id.TextInput)
        val result = findViewById<EditText>(R.id.Result)
        val clear = findViewById<Button>(R.id.AC)
        val one = findViewById<Button>(R.id.one)
        val two = findViewById<Button>(R.id.two)
        val three = findViewById<Button>(R.id.three)
        val four = findViewById<Button>(R.id.four)
        val five = findViewById<Button>(R.id.five)
        val six = findViewById<Button>(R.id.six)
        val seven = findViewById<Button>(R.id.seven)
        val eight = findViewById<Button>(R.id.eight)
        val nine = findViewById<Button>(R.id.nine)
        val zero = findViewById<Button>(R.id.zero)
        val decimalPoint = findViewById<Button>(R.id.dot)
        val equal = findViewById<Button>(R.id.equals)
        val division = findViewById<Button>(R.id.divide)
        val multiplication = findViewById<Button>(R.id.multiply)
        val addition = findViewById<Button>(R.id.add)
        val subtraction = findViewById<Button>(R.id.subtract)
        val plusMinus = findViewById<Button>(R.id.plusMinus)
        val percent = findViewById<Button>(R.id.percent)
        val delete = findViewById<Button>(R.id.cancel)

        clear.setOnClickListener {
            textInput.setText("")
            result.setText("")
            stateError = false
            lastNumeric = false
            lastDot = false
        }

        val numberListener = { view: Button ->
            if (stateError) {
                textInput.setText((view.text).toString())
                stateError = false
            } else {
                textInput.append((view.text).toString())
            }
            lastNumeric = true
            lastOperation = ' '
        }

        one.setOnClickListener {
            numberListener(it as Button)
        }
        two.setOnClickListener {
            numberListener(it as Button)
        }
        three.setOnClickListener {
            numberListener(it as Button)
        }
        four.setOnClickListener {
            numberListener(it as Button)
        }
        five.setOnClickListener {
            numberListener(it as Button)
        }
        six.setOnClickListener {
            numberListener(it as Button)
        }
        seven.setOnClickListener {
            numberListener(it as Button)
        }
        eight.setOnClickListener {
            numberListener(it as Button)
        }
        nine.setOnClickListener {
            numberListener(it as Button)
        }
        zero.setOnClickListener {
            numberListener(it as Button)
        }

        decimalPoint.setOnClickListener {
            if (lastNumeric && !lastDot) {
                textInput.append(".")
                lastNumeric = false
                lastDot = true
            }
        }

        val operatorListener = { view: Button ->
            if (lastNumeric && !stateError) {
                textInput.append((view.text).toString())
                lastNumeric = false
                lastDot = false
                lastOperation = (view.text).toString()[0]
            }
        }

        addition.setOnClickListener { operatorListener(it as Button) }
        subtraction.setOnClickListener { operatorListener(it as Button) }
        multiplication.setOnClickListener { operatorListener(it as Button) }
        division.setOnClickListener { operatorListener(it as Button) }

        plusMinus.setOnClickListener {
            if (textInput.text.isNotEmpty()) {
                try {
                    val value = textInput.text.toString().toDouble()
                    textInput.setText((value * -1).toString())
                } catch (e: Exception) {
                    stateError = true
                }
            }
        }


        delete.setOnClickListener {
            val currentText = textInput.text.toString()
            if (currentText.isNotEmpty()) {
                textInput.setText(currentText.dropLast(1))
                textInput.setSelection(textInput.text.length)
                // Update lastNumeric and lastDot states
                lastNumeric = textInput.text.isNotEmpty() && textInput.text.last().isDigit()
                lastDot = textInput.text.contains(".")
                stateError = false
            }
        }


        percent.setOnClickListener {
            if (textInput.text.isNotEmpty()) {
                try {
                    val value = textInput.text.toString().toDouble()
                    textInput.setText((value / 100).toString())
                    lastNumeric = true
                } catch (e: Exception) {
                    stateError = true
                }
            }
        }

        equal.setOnClickListener {
            if (lastNumeric && !stateError) {
                try {
                    val expression = textInput.text.toString()
                    val resultValue = evaluate(expression)
                    result.setText(resultValue.toString())
                } catch (e: Exception) {
                    stateError = true
                }
            }
        }
    }

//    private fun evaluate(expression: String): Double {
//        val parts = expression.split("[-+*/]".toRegex()).toTypedArray()
//        val operator = expression.find { it in "+-*/" } ?: return parts[0].toDouble()
//        val left = parts[0].toDouble()
//        val right = parts[1].toDouble()
//        return when (operator) {
//            '+' -> left + right
//            '-' -> left - right
//            '*' -> left * right
//            '/' -> left / right
//            else -> 0.0
//        }
//    }
//}


    fun evaluate(expression: String): Double {
        val operatorStack = Stack<Char>()
        val valueStack = Stack<Double>()

        var index = 0
        while (index < expression.length) {
            when (val ch = expression[index]) {
                in '0'..'9' -> {
                    val sb = StringBuilder()
                    while (index < expression.length && (expression[index] in '0'..'9' || expression[index] == '.')) {
                        sb.append(expression[index])
                        index++
                    }
                    valueStack.push(sb.toString().toDouble())
                    index-- // adjust for the next loop increment
                }

                '+', '-', '*', '/' -> {
                    while (!operatorStack.isEmpty() && hasPrecedence(ch, operatorStack.peek())) {
                        valueStack.push(
                            applyOperator(
                                operatorStack.pop(),
                                valueStack.pop(),
                                valueStack.pop()
                            )
                        )
                    }
                    operatorStack.push(ch)
                }
            }
            index++
        }

        while (!operatorStack.isEmpty()) {
            valueStack.push(applyOperator(operatorStack.pop(), valueStack.pop(), valueStack.pop()))
        }

        return valueStack.pop()
    }

    fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') return false
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-')
    }

    fun applyOperator(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> if (b != 0.0) a / b else throw ArithmeticException("Cannot divide by zero")
            else -> 0.0
        }
    }

}