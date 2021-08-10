package oysterlee.ui.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.RouterState
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.pop
import com.arkivanov.decompose.push
import com.arkivanov.decompose.replaceCurrent
import com.arkivanov.decompose.router
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import oysterlee.ui.view.main.MainScreenComponent
import oysterlee.ui.view.splash.SplashScreenComponent

/**
 * Available screensSelectApp
 */

sealed class Config : Parcelable {
  object Splash : Config()
  object Main : Config()
}

interface Router {
  fun push(config: Config)
  fun replace(config: Config)
  fun pop()
  val state: Value<RouterState<*, Component>>
}

/**
 * All navigation decisions are made from here
 */
class NavHostComponent(
  private val componentContext: ComponentContext,
) : Component, Router, ComponentContext by componentContext {

  /**
   * Router configuration
   */
  private val router = router<Config, Component>(
    initialConfiguration = Config.Splash,
    handleBackButton = true,
    childFactory = ::createScreenComponent
  )

  override val state: Value<RouterState<*, Component>> = router.state

  /**
   * When a new navigation request made, the screen will be created by this method.
   */
  private fun createScreenComponent(
    config: Config,
    componentContext: ComponentContext,
  ): Component {
    return when (config) {
      is Config.Splash -> SplashScreenComponent(componentContext)
      is Config.Main -> MainScreenComponent(componentContext)
    }
  }

  override fun push(config: Config) = router.push(config)
  override fun replace(config: Config) = router.replaceCurrent(config)
  override fun pop() = router.pop()

  @Composable
  override fun render() {
    Children(
      routerState = router.state
    ) { child ->
      child.instance.render()
    }
  }
}
