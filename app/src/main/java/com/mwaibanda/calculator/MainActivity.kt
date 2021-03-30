package com.mwaibanda.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.mwaibanda.calculator.ui.theme.CalculatorTheme
import androidx.compose.ui.unit. *
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.mwaibanda.calculator.ui.theme.Shapes
import java.text.DecimalFormat
import java.math.RoundingMode
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorApp {
                ScreenContent()
            }
        }
    }
}
class CalculatorViewModel : ViewModel() {
    val inputLiveDate: LiveData<String>
        get() = input
    private val input = MutableLiveData<String>()
    private var intValue1 = 0
    private var intValue2 = 0
    private var floatValue1 = 0.0
    private var floatValue2 = 0.0
    private var result = ""
    private var isDecimal = false
    private var operator = ""
    private var questionWasAnswered = false

    val calculatorButtons = arrayOf(
        arrayOf("%", "±", "π", "×"),
        arrayOf("7", "8", "9", "÷"),
        arrayOf("4", "5", "6", "-"),
        arrayOf("1", "2", "3", "+"),
        arrayOf("0", ".", "<", "="),
    )

    fun processInput(button: String) {
        if (input.value == "0" || questionWasAnswered) {
            input.value = ""
            if (questionWasAnswered) {
                input.value = result.toString()
            }
        }
        when (button) {
            "×", "÷", "-", "+", "%" -> setIntialValue(button)
            "=" -> equality()
            "<" -> backspace()
            "±" -> negativePositive()
            "π" -> println("***********Pi***********")
            "." -> decimal()
            else -> {
                if (questionWasAnswered) {
                    input.value = ""
                    var userInput =  "${input.value}$button"
                    input.value = userInput.replace("null", "")
                    questionWasAnswered = false
                }  else {
                    var userInput = "${input.value}$button"
                    input.value = userInput.replace("null", "")
                }
            }
        }
    }
    private fun setIntialValue(button: String) {
        val nonNullString = input.value?.replace("null", "")
        if (isDecimal) {
            floatValue1 = nonNullString!!.toDouble()
        } else {
            intValue1 = nonNullString!!.toInt()
        }
        println("********** Value1: 5 ****************")
        operator = button
        println("********** Operator: ${button} ****************")
        input.value = ""
    }
    private fun equality() {
        val nonNullString = input.value?.replace("null", "")
        if (isDecimal) {
            floatValue2 = nonNullString!!.toDouble()
        } else {
            intValue2 = nonNullString!!.toInt()
        }

        println("********** Value2: ${if (isDecimal) floatValue2 else intValue2} ****************")
        when (operator) {
            "×" -> result =  if (isDecimal) "${floatValue1 * floatValue2}" else "${intValue1 * intValue2}"
            "÷" -> result = if (isDecimal) "${floatValue1 / floatValue2}" else "${intValue1 / intValue2}"
            "-" -> result = if (isDecimal) "${floatValue1 - floatValue2}" else "${intValue1 - intValue2}"
            "+" -> result = if (isDecimal) "${floatValue1 + floatValue2}" else "${intValue1 + intValue2}"
            "%" -> result = if (isDecimal) "${floatValue1 % floatValue2}" else "${intValue1 % intValue2}"

        }
        var decimalformattrer = DecimalFormat("#.##")
        decimalformattrer.roundingMode = RoundingMode.CEILING
        input.value = "${decimalformattrer.format(result.toDouble())}"
        questionWasAnswered = true
        operator = ""
    }
    private fun backspace() {
        input.value = input.value?.dropLast(1)
    }
    private fun negativePositive() {
        if (input.value?.first() == '-') {
            input.value = input.value?.drop(1)
        } else {
            input.value = "-" + input.value
        }
    }
    private fun decimal() {
        if (input.value?.last() == '.') {
            input.value = input.value?.dropLast(1)
            isDecimal = false
        } else {
            input.value = input.value + "."
            isDecimal = true
        }
    }
    fun returnBackgroundColor(button: String): Color {
        var buttonColor = Color.Transparent
        val specialButtons = arrayOf("%", "±", "π", ".")
        if (button.toIntOrNull() != null) {
            buttonColor = Color.DarkGray
        } else if(specialButtons.contains(button)){
            buttonColor = Color.LightGray
        } else {
            if (button == operator && operator != "=") {
                buttonColor = Color.White
            } else {
                buttonColor = Color(red = 245, green = 158, blue = 66)
            }
        }
        return  buttonColor
    }
    fun returnForegroundColor(button: String): Color {
        var foregroundColor = Color.White
        if (button == operator && operator != "=") {
            foregroundColor = Color(red = 245, green = 158, blue = 66)
        }
        return foregroundColor
    }
}

@Composable
fun CalculatorApp(content: @Composable () -> Unit){
    CalculatorTheme {
        Surface(color = Color.Black) {
            content()
        }
    }
}
@Composable
fun ScreenContent(model: CalculatorViewModel = viewModel()){
    val text by model.inputLiveDate.observeAsState("0")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 10.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(text = text,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                fontSize = 90.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(24.dp)
            )
        }
    Column(modifier = Modifier
                    .padding(bottom = 50.dp)
                    .padding(horizontal = 10.dp)
    ) {
        for (row in model.calculatorButtons) {
            Row (modifier = Modifier.padding(bottom = 10.dp)){
                for (button in row) {
                    Button(onClick = {model.processInput(button) },
                           shape = RoundedCornerShape(50),
                           colors = ButtonDefaults.buttonColors(
                               backgroundColor = model.returnBackgroundColor(button),
                               contentColor = Color.White
                           ),
                           modifier = Modifier.padding(7.dp),
                        ) {
                        Text(
                            text = button,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = model.returnForegroundColor(button),
                            modifier = Modifier
                                .padding(14.dp)
                                .size(width = 20.dp, height = 35.dp)
                        )
                    }
                }
            }
        }
    }
}
}



@Preview(showBackground = true, name = "CalculatorApp")
@Composable
fun DefaultPreview() {
    CalculatorApp {
        ScreenContent()
    }
}



