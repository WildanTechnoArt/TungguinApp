package com.hyperdev.tungguin.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.hyperdev.tungguin.fragment.TopUpHisFragment
import com.hyperdev.tungguin.fragment.TransactionHisFragment

class PagerAdapter(fm: FragmentManager, private val numberTabs: Int) : FragmentStatePagerAdapter(fm) {

    //Mengembalikan Fragment yang terkait dengan posisi tertentu
    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> TransactionHisFragment()
            1 -> TopUpHisFragment()
            else -> null
        }
    }

    //Mengembalikan jumlah tampilan yang tersedia.
    override fun getCount(): Int {
        return numberTabs
    }
}