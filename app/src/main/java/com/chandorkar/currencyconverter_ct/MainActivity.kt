package com.chandorkar.currencyconverter_ct

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.time.temporal.ValueRange

class MainActivity : AppCompatActivity() {

    var baseCurrency = "INR"
    var convertedToCurrency = "USD"
    var conversionRate = 0f

    lateinit var et_firstConversion: EditText

    lateinit var et_secondConversion: EditText

    lateinit var btnConvert: Button
    lateinit var mainLayout: ConstraintLayout
    lateinit var txtTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et_firstConversion = findViewById<EditText>(R.id.et_firstConversion)

        et_secondConversion = findViewById<EditText>(R.id.et_secondConversion)

        txtTime = findViewById<TextView>(R.id.txtTime)
        btnConvert = findViewById<Button>(R.id.btnConvert)
        mainLayout = findViewById<ConstraintLayout>(R.id.mainLayout)


        spinnerSetup()
        textChangedStuff()

        btnConvert.setOnClickListener{

            if(et_firstConversion!!.length() == 0){
                Toast.makeText(applicationContext, "Type a value", Toast.LENGTH_SHORT).show()
            }else{
                hideKeyboard()
                getApiResult()

            }

        }

        val layoutInflater = findViewById<View>(R.id.networkError)
        val networkConnection= NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) { 
                //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                layoutInflater.visibility= View.GONE
                mainLayout.visibility=View.VISIBLE
            } else {
                Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show()
                layoutInflater.visibility= View.VISIBLE
                mainLayout.visibility=View.GONE
            }
        }

    }

    fun Activity.hideKeyboard(){
            hideKeyboard(currentFocus?: View(this))
    }

    fun Context.hideKeyboard(view: View){
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
    }

    private fun textChangedStuff() {
        et_firstConversion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    getApiResult()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Type a value", Toast.LENGTH_SHORT).show()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "OnTextChanged")
            }

        })

    }

    private fun getApiResult() {
        if (et_firstConversion != null && et_firstConversion.text.isNotEmpty() && et_firstConversion.text.isNotBlank()) {

            var API = "https://v6.exchangerate-api.com/v6/b3ae77aa44e3f74ffef30d6a/latest/$baseCurrency"

            if (baseCurrency == convertedToCurrency) {
                Toast.makeText(
                    applicationContext,
                    "Please pick a currency to convert",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                GlobalScope.launch(Dispatchers.IO) {

                    try {

                        val apiResult = URL(API).readText()
                        val jsonObject = JSONObject(apiResult)

                        txtTime?.setText("Last Updated - "+jsonObject.getString("time_last_update_utc"))

                        conversionRate = jsonObject.getJSONObject("conversion_rates").getString(convertedToCurrency)
                                .toFloat()

                        Log.d("Main", "$conversionRate")
                        Log.d("MainResult", apiResult)

                        withContext(Dispatchers.Main) {
                            val text =
                                ((et_firstConversion.text.toString()
                                    .toFloat()) * conversionRate).toString()
                            et_secondConversion?.setText(text+" "+convertedToCurrency)

                        }

                    } catch (e: Exception) {
                        Log.e("Main", "$e")
                    }
                }
            }
        }
    }

    private fun spinnerSetup() {
        val spinner: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter

        }

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner2.adapter = adapter

        }

        spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                baseCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }

        })

        spinner2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }

        })
    }

    companion object {
        lateinit var editTextValidator: ValueRange

        @JvmStatic
        fun editTextValidator(s: String): Boolean {

            return true;
        }
    }

}