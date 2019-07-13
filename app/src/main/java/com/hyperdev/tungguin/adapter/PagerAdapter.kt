package com.hyperdev.tungguin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hyperdev.tungguin.fragment.TopUpHisFragment
import com.hyperdev.tungguin.fragment.TransactionHisFragment

class PagerAdapter(fm: FragmentManager, behavior: Int, private val numberTabs: Int) :
    FragmentPagerAdapter(fm, behavior) {

    //Mengembalikan Fragment yang terkait dengan posisi tertentu
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> TransactionHisFragment()
            1 -> TopUpHisFragment()
            else -> null
        }!!
    }

    //Mengembalikan jumlah tampilan yang tersedia.
    override fun getCount(): Int {
        return numberTabs
    }
}