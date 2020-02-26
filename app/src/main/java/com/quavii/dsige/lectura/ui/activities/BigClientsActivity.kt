package com.quavii.dsige.lectura.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.adapters.TabLayoutAdapter
import kotlinx.android.synthetic.main.activity_big_clients.*

class BigClientsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_clients)

        val b = intent.extras
        if (b != null){
            bindUI(b.getInt("clienteId"))
        }
    }

    private fun bindUI(id : Int) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Grandes Clientes"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab4))

        val tabLayoutAdapter = TabLayoutAdapter.TabLayoutClient(supportFragmentManager, tabLayout.tabCount,id)
        viewPager.adapter = tabLayoutAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                viewPager.currentItem = position
                Util.hideKeyboard(this@BigClientsActivity)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}