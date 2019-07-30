package com.hyperdev.tungguin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hyperdev.tungguin.fragment.TopUpHisFragment
import com.hyperdev.tungguin.fragment.TransactionHisFragment

class PagerAdapter(fm: FragmentManager, behavior: Int, private val numberTabs: Int) :
    FragmentStatePagerAdapter(fm, behavior) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> return TopUpHisFragment()
            1 -> return TransactionHisFragment()
            else -> null
        }!!
    }

    //Mengembalikan jumlah tampilan yang tersedia.
    override fun getCount(): Int {
        return numberTabs
    }
}