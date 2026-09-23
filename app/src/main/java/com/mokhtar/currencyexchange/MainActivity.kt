package com.mokhtar.currencyexchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.mokhtar.currencyexchange.api.ExchangeService
import com.mokhtar.currencyexchange.api.model.ExchangeRates
import com.mokhtar.currencyexchange.api.model.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var buyUsdTextView: TextView? = null
    private var sellUsdTextView: TextView? = null
    private var fab: FloatingActionButton? = null
    private var transactionDialog: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        buyUsdTextView = findViewById(R.id.txtBuyUsdRate)
        sellUsdTextView = findViewById(R.id.txtSellUsdRate)
        fab = findViewById(R.id.fab)

        fab?.setOnClickListener {
            showDialog()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchRates()
    }

    private fun fetchRates() {
        ExchangeService.exchangeApi().getExchangeRates().enqueue(object : Callback<ExchangeRates> {
            override fun onResponse(
                call: Call<ExchangeRates>,
                response: Response<ExchangeRates>
            ) {
                val responseBody: ExchangeRates? = response.body()
                buyUsdTextView?.text = responseBody?.usdToLbp?.toString()
                sellUsdTextView?.text = responseBody?.lbpToUsd?.toString()
            }

            override fun onFailure(call: Call<ExchangeRates>, t: Throwable) {
                return
            }
        })
    }

    private fun showDialog() {
        transactionDialog = LayoutInflater.from(this)
            .inflate(R.layout.dialog_transaction, null, false)

        MaterialAlertDialogBuilder(this)
            .setView(transactionDialog)
            .setTitle("Add Transaction")
            .setMessage("Enter transaction details")
            .setPositiveButton("Add") { dialog, _ ->
                val usdAmount = transactionDialog
                    ?.findViewById<TextInputLayout>(R.id.txtInptUsdAmount)
                    ?.editText?.text.toString().toFloat()
                val lbpAmount = transactionDialog
                    ?.findViewById<TextInputLayout>(R.id.txtInptLbpAmount)
                    ?.editText?.text.toString().toFloat()
                val selectedTransactionType = transactionDialog
                    ?.findViewById<RadioGroup>(R.id.rdGrpTransactionType)
                    ?.checkedRadioButtonId

                val transaction = Transaction()
                transaction.usdAmount = usdAmount
                transaction.lbpAmount = lbpAmount
                transaction.usdToLbp = selectedTransactionType == R.id.rdBtnSellUsd
                addTransaction(transaction)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addTransaction(transaction: Transaction) {
        ExchangeService.exchangeApi().addTransaction(transaction).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Snackbar.make(fab as View, "Transaction added!", Snackbar.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Snackbar.make(fab as View, "Could not add transaction.", Snackbar.LENGTH_LONG).show()
            }
        })
    }
}
