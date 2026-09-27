package com.mokhtar.currencyexchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.mokhtar.currencyexchange.api.ExchangeService
import com.mokhtar.currencyexchange.api.model.ExchangeRates
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExchangeFragment : Fragment() {
    private var buyUsdTextView: TextView? = null
    private var sellUsdTextView: TextView? = null
    private var amountInput: TextInputLayout? = null
    private var directionRadioGroup: RadioGroup? = null
    private var calculateButton: Button? = null
    private var resultTextView: TextView? = null
    private var usdToLbpRate: Float? = null
    private var lbpToUsdRate: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchRates()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_exchange, container, false)

        buyUsdTextView = view.findViewById(R.id.txtBuyUsdRate)
        sellUsdTextView = view.findViewById(R.id.txtSellUsdRate)
        amountInput = view.findViewById(R.id.txtInptCalculateAmount)
        directionRadioGroup = view.findViewById(R.id.rdGrpCalculationType)
        calculateButton = view.findViewById(R.id.btnCalculate)
        resultTextView = view.findViewById(R.id.txtCalculationResult)

        calculateButton?.setOnClickListener {
            calculateExchange()
        }

        return view
    }

    private fun calculateExchange() {
        val amount = amountInput?.editText?.text.toString().toFloatOrNull()
        if (amount == null) {
            resultTextView?.text = "Enter an amount"
            return
        }

        if (directionRadioGroup?.checkedRadioButtonId == R.id.rdBtnUsdToLbp) {
            val rate = usdToLbpRate
            resultTextView?.text = if (rate != null) {
                "${amount * rate} LBP"
            } else {
                "Exchange rates are not available"
            }
        } else {
            val rate = lbpToUsdRate
            resultTextView?.text = if (rate != null) {
                "${amount / rate} USD"
            } else {
                "Exchange rates are not available"
            }
        }
    }

    private fun fetchRates() {
        ExchangeService.exchangeApi().getExchangeRates().enqueue(object : Callback<ExchangeRates> {
            override fun onResponse(
                call: Call<ExchangeRates>,
                response: Response<ExchangeRates>
            ) {
                val responseBody: ExchangeRates? = response.body()
                usdToLbpRate = responseBody?.usdToLbp
                lbpToUsdRate = responseBody?.lbpToUsd
                buyUsdTextView?.text = responseBody?.usdToLbp?.toString()
                sellUsdTextView?.text = responseBody?.lbpToUsd?.toString()
            }

            override fun onFailure(call: Call<ExchangeRates>, t: Throwable) {
                return
            }
        })
    }
}
