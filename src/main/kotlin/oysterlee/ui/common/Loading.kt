package oysterlee.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

/**
 * To show a rotating icon at the center and blinking text at the bottom of the screen
 */
@Composable
fun Loading(message: String = "") {
  var currentRotation by remember { mutableStateOf(0f) }
  val rotation = remember { Animatable(currentRotation) }

  LaunchedEffect(Unit) {
    rotation.animateTo(
      targetValue = currentRotation - 360f,
      animationSpec = infiniteRepeatable(
        animation = tween(800, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
      )
    ) {
      currentRotation = value
    }
  }

  Box(
    modifier = Modifier.fillMaxSize().zIndex(999f).background(color = Color.Transparent)
  ) {
    Column(
      modifier = Modifier.align(Alignment.Center),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      LoadingIcon(rotation.value)
      LoadingText(message = message)
    }
  }
}

@Composable
fun LoadingIcon(rotation: Float) {
  Image(
    modifier = Modifier
      .rotate(rotation)
      .size(50.dp),
    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
    painter = painterResource("drawables/loading.png"),
    contentDescription = ""
  )
}

@Composable
fun LoadingText(
  message: String,
  modifier: Modifier = Modifier
) {
  var enabled by remember { mutableStateOf(true) }

  val alpha = if (enabled) {
    1f
  } else {
    0.2f
  }

  val animatedAlpha by animateFloatAsState(
    targetValue = alpha,
    animationSpec = tween(200),
    finishedListener = {
      enabled = !enabled
    }
  )

  Text(
    text = message,
    modifier = modifier.alpha(animatedAlpha),
    style = TextStyle(
      fontSize = 20.sp,
    ),
    textAlign = TextAlign.Center
  )

  LaunchedEffect(Unit) {
    enabled = !enabled
  }
}
