package oysterlee.ui.view

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import oysterlee.data.model.DownloadProgress
import oysterlee.data.model.Version
import oysterlee.data.repo.VersionRepo
import oysterlee.ui.navigation.Config
import oysterlee.ui.value.R

sealed class Intent {
}

data class State(
  val data: Any = ""
)

sealed class Label {
  data class RedirectTo(val route: Config) : Label()
}

interface GlobalStore : Store<Intent, State, Label>

internal class GlobalStoreFactory(
  private val storeFactory: StoreFactory,
) {
  private sealed class Result {
    class Value(val state: State) : Result()
  }

  fun create(): GlobalStore =
    object :
      GlobalStore,
      Store<Intent, State, Label> by storeFactory.create(
        name = "GlobalStore",
        initialState = State(),
        executorFactory = ::ExecutorImpl,
        reducer = ReducerImpl,
      ) {
    }

  private inner class ExecutorImpl : SuspendExecutor<Intent, Any, State, Result, Label>(Dispatchers.IO) {}

  private object ReducerImpl : Reducer<State, Result> {
    override fun State.reduce(result: Result): State =
      when (result) {
        is Result.Value -> result.state.copy()
      }
  }
}
