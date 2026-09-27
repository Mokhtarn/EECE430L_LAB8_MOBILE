package com.mokhtar.currencyexchange

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputLayout
import com.mokhtar.currencyexchange.api.Authentication
import com.mokhtar.currencyexchange.api.ExchangeService
import com.mokhtar.currencyexchange.api.model.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var fab: FloatingActionButton? = null
    private var transactionDialog: View? = null
    private var menu: Menu? = null
    private var tabLayout: TabLayout? = null
    private var tabsViewPager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Authentication.initialize(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        tabLayout = findViewById(R.id.tabLayout)
        tabsViewPager = findViewById(R.id.tabsViewPager)
        tabLayout?.tabMode = TabLayout.MODE_FIXED
        tabLayout?.isInlineLabel = true
        tabsViewPager?.isUserInputEnabled = true
        val adapter = TabsPagerAdapter(supportFragmentManager, lifecycle)
        tabsViewPager?.adapter = adapter

        TabLayoutMediator(tabLayout!!, tabsViewPager!!) { tab, position ->
            when (position) {
                0 -> tab.text = "Exchange"
                1 -> tab.text = "Transactions"
            }
        }.attach()

        fab = findViewById(R.id.fab)

        fab?.setOnClickListener {
            showDialog()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        setMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        setMenu()
        return true
    }

    private fun setMenu() {
        menu?.clear()
        menuInflater.inflate(
            if (Authentication.getToken() == null) {
                R.menu.menu_logged_out
            } else {
                R.menu.menu_logged_in
            },
            menu
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login -> {
                startActivity(Intent(this, LoginActivity::class.java))
                true
            }

            R.id.register -> {
                startActivity(Intent(this, RegistrationActivity::class.java))
                true
            }

            R.id.logout -> {
                Authentication.clearToken()
                setMenu()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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
