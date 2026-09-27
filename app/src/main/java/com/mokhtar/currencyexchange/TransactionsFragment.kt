package com.mokhtar.currencyexchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mokhtar.currencyexchange.api.Authentication
import com.mokhtar.currencyexchange.api.ExchangeService
import com.mokhtar.currencyexchange.api.model.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionsFragment : Fragment() {
    private var listview: ListView? = null
    private var transactions: ArrayList<Transaction>? = ArrayList()
    private var adapter: TransactionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchTransactions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)
        listview = view.findViewById(R.id.listview)
        adapter = TransactionAdapter(layoutInflater, transactions!!)
        listview?.adapter = adapter

        return view
    }

    private fun fetchTransactions() {
        if (Authentication.getToken() != null) {
            ExchangeService.exchangeApi()
                .getTransactions("Bearer ${Authentication.getToken()}")
                .enqueue(object : Callback<List<Transaction>> {
                    override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                        return
                    }

                    override fun onResponse(
                        call: Call<List<Transaction>>,
                        response: Response<List<Transaction>>
                    ) {
                        transactions?.addAll(response.body()!!)
                        adapter?.notifyDataSetChanged()
                    }
                })
        }
    }

    class TransactionAdapter(
        private val inflater: LayoutInflater,
        private val dataSource: List<Transaction>
    ) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = inflater.inflate(R.layout.item_transaction, parent, false)
            val transaction = dataSource[position]

            view.findViewById<TextView>(R.id.txtTransactionType).text =
                if (transaction.usdToLbp == true) "Sell USD" else "Buy USD"
            view.findViewById<TextView>(R.id.txtTransactionAmounts).text =
                "USD: ${transaction.usdAmount}    LBP: ${transaction.lbpAmount}"
            view.findViewById<TextView>(R.id.txtTransactionDate).text =
                "Date: ${transaction.addedDate}"

            return view
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return dataSource[position].id?.toLong() ?: 0
        }

        override fun getCount(): Int {
            return dataSource.size
        }
    }
}
