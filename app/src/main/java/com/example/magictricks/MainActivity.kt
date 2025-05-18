package upworksolutions.themagictricks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MagicTricksHomeScreen()
                }
            }
        }
    }
}

@Composable
fun MagicTricksHomeScreen() {
    var currentTrick by remember { mutableStateOf(0) }
    val tricks = listOf(
        "Card Trick",
        "Coin Vanish",
        "Rope Magic",
        "Mentalism"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Magic Tricks",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = tricks[currentTrick],
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    currentTrick = (currentTrick - 1).coerceAtLeast(0)
                }
            ) {
                Text("Previous")
            }
            
            Button(
                onClick = {
                    currentTrick = (currentTrick + 1).coerceAtMost(tricks.size - 1)
                }
            ) {
                Text("Next")
            }
        }
    }
} 