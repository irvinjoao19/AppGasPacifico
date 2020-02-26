package com.quavii.dsige.lectura.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quavii.dsige.lectura.ui.fragments.FileFragment
import com.quavii.dsige.lectura.ui.fragments.FirmFragment
import com.quavii.dsige.lectura.ui.fragments.GeneralClientFragment
import com.quavii.dsige.lectura.ui.fragments.GeneralFragment

abstract class TabLayoutAdapter {
    class TabLayoutRecibo(fm: FragmentManager, private val numberOfTabs: Int, var repartoId: Int, var recibo: String, var operarioId: Int, var cliente: String, var validation: Int)
        : FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GeneralFragment.newInstance(repartoId, recibo, operarioId, cliente, validation)
                1 -> FirmFragment.newInstance(repartoId)
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }

    class TabLayoutClient(fm: FragmentManager, private val numberOfTabs: Int, val id: Int)
        : FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GeneralClientFragment.newInstance(id)
                1 -> FileFragment.newInstance(id)
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }
}