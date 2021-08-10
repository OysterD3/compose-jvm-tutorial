package oysterlee.ui.view.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.kodein.di.compose.LocalDI
import org.kodein.di.instance
import oysterlee.data.repo.VersionRepo
import oysterlee.ui.navigation.Component

class MainScreenComponent(
  private val componentContext: ComponentContext,
) : Component, ComponentContext by componentContext {

  @ExperimentalCoroutinesApi
  @Composable
  override fun render() {
    val versionRepo: VersionRepo by LocalDI.current.instance()
    val store = remember {
      MainStoreFactory(
        storeFactory = DefaultStoreFactory,
        versionRepo = versionRepo
      ).create()
    }

    DisposableEffect(store) {
      onDispose {
        store.dispose()
      }
    }

    MainScreen(store)
  }

}
