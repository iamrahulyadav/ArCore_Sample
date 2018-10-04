package com.angelbroking.financialplanning.otm

import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView

class OtmActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otm)

        initializeResources()

    }

    private fun initializeResources() {
        setSupportActionBar(toolbar)
        (toolbar.findViewById(R.id.txt_common_toolbar) as TextView).text = "OTM"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener({ v -> onBackPressed() })

        addOtmFragment()

    }

    private fun addOtmFragment(){
        val intent = intent
        if (intent != null && intent.hasExtra("mandateId")) {
            var mandateId: String = intent.extras.getString("mandateId")
            if (!TextUtils.isEmpty(mandateId)) {
                supportFragmentManager.beginTransaction().replace(R.id.framelayout_main, OtmFragment.newInstance(mandateId), "OtmFragment").commit()
            }

        }
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
            return
        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }
}
