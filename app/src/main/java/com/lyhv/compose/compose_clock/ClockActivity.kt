package com.lyhv.compose.compose_clock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview

class ClockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DefaultPreview()
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {

    MaterialTheme {
        Stack {
            ComposeClock()

            Align(alignment = Alignment.BottomLeft) {
                Column {
                    Text(
                        modifier = LayoutPadding(Dp(16f)),
                        text = "Compose Clock",
                        style = TextStyle(Color.White)
                    )
                    Text(
                        modifier = LayoutPadding(Dp(16f)),
                        text = "github.com/adibfara/ComposeClock",
                        style = TextStyle(Color.White, TextUnit.Companion.Sp(12f))
                    )

                }
            }
        }
    }
}
