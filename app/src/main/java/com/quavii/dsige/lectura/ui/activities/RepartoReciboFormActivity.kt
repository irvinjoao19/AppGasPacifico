package com.quavii.dsige.lectura.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.adapters.TabLayoutAdapter
import com.quavii.dsige.lectura.ui.services.AlertRepartoSleepService
import kotlinx.android.synthetic.main.activity_reparto_recibo_form.*

class RepartoReciboFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reparto_recibo_form)
        val b = intent.extras
        if (b != null) {
            bindUI(b.getInt("repartoId"), b.getString("recibo")!!, b.getInt("operarioId"), b.getString("cliente")!!, b.getInt("validation"))
            stopService(Intent(this, AlertRepartoSleepService::class.java))
        } else {
            bindUI(123, "123", 1, "irvin", 1)
        }
    }

    private fun bindUI(repartoId: Int, recibo: String, operarioId: Int, cliente: String, validation: Int) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "CheckList"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1))
        if (validation == 2) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab3))
        }

        val tabLayoutAdapter = TabLayoutAdapter.TabLayoutRecibo(supportFragmentManager, tabLayout.tabCount, repartoId, recibo, operarioId, cliente, validation)
        viewPager.adapter = tabLayoutAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                viewPager.currentItem = position
                Util.hideKeyboard(this@RepartoReciboFormActivity)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}