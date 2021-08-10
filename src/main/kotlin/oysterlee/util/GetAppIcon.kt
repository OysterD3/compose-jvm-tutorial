package oysterlee.util

import androidx.compose.ui.graphics.asPainter
import androidx.compose.ui.graphics.painter.Painter
import javax.imageio.ImageIO

fun getAppIcon(): Painter {
  return ImageIO.read(
      Thread.currentThread().contextClassLoader
        .getResourceAsStream("drawables/launcher_icons/system.png")
    ).asPainter()
}
