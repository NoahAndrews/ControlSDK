package org.btelman.control.sdk.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_demo_settings.*

class DemoSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_settings)
        settingsReturnButton?.setOnClickListener {
            Intent(this, DemoActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}
