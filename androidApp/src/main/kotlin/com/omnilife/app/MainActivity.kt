package com.omnilife.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

/**
 * Application entry point. Bootstrap scope only (Engineering Plan, EPIC-00):
 * no navigation, no screens, no business logic — see README-BUILD.md.
 * Real composition of feature modules starts in the first development sprint.
 */
public class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface {
                Text("OmniLife — bootstrap")
            }
        }
    }
}
