package oysterlee.ui.view.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.rx.observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import org.kodein.di.compose.LocalDI
import org.kodein.di.instance
import oysterlee.ui.navigation.Component
import oysterlee.ui.navigation.Config
import oysterlee.ui.navigation.Router
import oysterlee.ui.view.GlobalStore
import oysterlee.ui.view.Label

class SplashScreenComponent(
  private val componentContext: ComponentContext,
) : Component, ComponentContext by componentContext {

  @Composable
  override fun render() {
    val router: Router by LocalDI.current.instance()
    val globalStore: GlobalStore by LocalDI.current.instance()

    DisposableEffect(globalStore) {
      val labelObserver = globalStore.labels(
        observer {
          when (it) {
            is Label.RedirectTo -> router.replace(it.route)
          }
        }
      )

      // clean up upon destroy to prevent memory leak
      onDispose {
        labelObserver.dispose()
      }
    }

    SplashScreen()

    LaunchedEffect(Dispatchers.Main) {
      delay(1000)
      router.push(Config.Main)
    }
  }
}
