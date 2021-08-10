package oysterlee.ui.view

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.crossfade
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.rx.observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.compose.withDI
import oysterlee.data.repo.VersionRepo
import oysterlee.ui.navigation.NavHostComponent
import oysterlee.ui.navigation.Router
import oysterlee.ui.view.splash.SplashScreenComponent
import oysterlee.util.getAppIcon
import wetix.ui.view.RootComponent

/**
 * The activity who will be hosting all screens in this app
 */

@ExperimentalDecomposeApi
@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
fun MainActivity() {
  val lifecycle = LifecycleRegistry()
  val router = NavHostComponent(DefaultComponentContext(lifecycle))

  // create global store (singleton)
  val globalStore = GlobalStoreFactory(
    storeFactory = DefaultStoreFactory,
  ).create()

  application {

    val state = rememberWindowState(
      size = WindowSize(1366.dp, 768.dp),
      position = WindowPosition(Alignment.Center)
    )
    Window(
      title = "Compose Desktop Tutorial",
      onCloseRequest = ::exitApplication,
      state = state,
      icon = getAppIcon(),
    ) {
      MenuBar {
        Menu(text = "File") {
          Item(
            text = "About",
            onClick = {
            },
            shortcut = KeyShortcut(Key.I)
          )
          Item(
            text = "Exit",
            onClick = {
              exitApplication()
            },
            shortcut = KeyShortcut(Key.W)
          )
        }
        Menu(
          text = "Window",
        ) {
          Item(
            text = "Minimize",
            onClick = {
              state.isMinimized = true
            },
            shortcut = KeyShortcut(Key.M)
          )
          Item(
            text = "Maximize",
            onClick = {
              state.isMinimized = true
            },
          )
        }
        Menu(
          text = "Help",
        ) {
          Item(
            text = "Contact Us",
            onClick = {
            },
          )
        }
      }

      MaterialTheme {

        // define every dependency injection
        val di = DI {
          bindProvider<Router> { router }
          bindProvider { globalStore }
          bindSingleton { VersionRepo() }
        }

        // inject everything into RootComponent
        withDI(di) {
          RootUI(router)
        }

        // clean up everything to prevent memory leak
        DisposableEffect(di) {
          val labelObserver = globalStore.labels(
            observer {
              when (it) {
                is Label.RedirectTo -> {
                  router.replace(it.route)
                }
              }
            }
          )
          onDispose {
            globalStore.dispose()
            labelObserver.dispose()
          }
        }
      }
    }
  }
}

@ExperimentalDecomposeApi
@Composable
fun RootUI(component: NavHostComponent) {
  Children(
    routerState = component.state,
    animation = crossfade(),
  ) {
    when (val child = it.instance) {
      is SplashScreenComponent -> child.render()
      else -> {
        RootComponent(child)
      }
    }
  }
}
