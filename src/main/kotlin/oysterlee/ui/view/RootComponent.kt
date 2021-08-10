package wetix.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import oysterlee.ui.navigation.Component

@Composable
fun RootComponent(
  child: Component,
) {

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    child.render()
  }
}
