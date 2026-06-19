package com.mantapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mantapp.app.ui.navigation.MantappNavGraph
import com.mantapp.app.ui.theme.MantappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MantappTheme {
                MantappApp()
            }
        }
    }
}

@Composable
fun MantappApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        MantappNavGraph()
    }
}

@Preview(showBackground = true)
@Composable
private fun MantappAppPreview() {
    MantappTheme {
        MantappApp()
    }
}
