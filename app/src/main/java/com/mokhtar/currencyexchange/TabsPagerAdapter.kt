package com.mokhtar.currencyexchange

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ExchangeFragment()
            1 -> TransactionsFragment()
            else -> ExchangeFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}
