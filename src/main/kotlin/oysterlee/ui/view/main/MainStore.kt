package oysterlee.ui.view.main

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import oysterlee.data.model.DownloadProgress
import oysterlee.data.model.Version
import oysterlee.data.repo.VersionRepo
import oysterlee.ui.navigation.Config
import oysterlee.ui.value.R

sealed class Event<T> {
  class None<T>() : Event<T>()
  class Loading<T>() : Event<T>()
  class Success<T>(val data: T) : Event<T>()
  class Error<T>(val error: Throwable) : Event<T>()
}

sealed class Intent {
  data class SetUpdateDialogVisibility(val visible: Boolean) : Intent()
  data class SetDownloadDialogVisibility(val visible: Boolean) : Intent()

  object CheckUpdate : Intent()
  object Download : Intent()
}

data class State(
  val version: Version = Version(),
  val hasUpdate: Event<Boolean> = Event.None(),
  val updateDialogVisible: Boolean = false,
  val downloadDialogVisible: Boolean = false,
  val downloadProgress: Event<DownloadProgress> = Event.None(),
)

interface MainStore : Store<Intent, State, Nothing>

internal class MainStoreFactory(
  private val storeFactory: StoreFactory,
  private val versionRepo: VersionRepo,
) {
  private sealed class Result {
    class Value(val state: State) : Result()
  }

  fun create(): MainStore =
    object :
      MainStore,
      Store<Intent, State, Nothing> by storeFactory.create(
        name = "MainStore",
        initialState = State(),
        executorFactory = ::ExecutorImpl,
        reducer = ReducerImpl,
      ) {
    }

  private inner class ExecutorImpl : SuspendExecutor<Intent, Nothing, State, Result, Nothing>(Dispatchers.IO) {

    @ExperimentalCoroutinesApi
    override suspend fun executeIntent(intent: Intent, getState: () -> State) =
      when (intent) {
        is Intent.SetUpdateDialogVisibility -> dispatch(
          Result.Value(
            getState().copy(
              updateDialogVisible = intent.visible
            )
          )
        )
        is Intent.SetDownloadDialogVisibility -> dispatch(
          Result.Value(
            getState().copy(
              downloadDialogVisible = intent.visible
            )
          )
        )
        is Intent.CheckUpdate -> {
          dispatch(Result.Value(getState().copy(updateDialogVisible = true)))
          _checkUpdate(getState)
        }
        is Intent.Download -> {
          val state = getState()
          if (state.hasUpdate is Event.Success && state.hasUpdate.data) {
            dispatch(Result.Value(getState().copy(downloadDialogVisible = true)))
            _downloadUpdate(getState)
          } else {
            dispatch(Result.Value(getState()))
          }
        }
      }

    private suspend fun _checkUpdate(getState: () -> State) {
      dispatch(Result.Value(getState().copy(version = Version(), hasUpdate = Event.Loading())))
      versionRepo.checkForUpdate().catch {
        dispatch(Result.Value(getState().copy(version = Version(), hasUpdate = Event.Error(it))))
      }.collect {
        if (it.version.isNotBlank() && it.version != R.VERSION) {
          dispatch(
            Result.Value(
              getState().copy(
                version = it,
                hasUpdate = Event.Success(true)
              )
            )
          )
        } else {
          dispatch(
            Result.Value(
              getState().copy(
                version = Version(version = R.VERSION),
                hasUpdate = Event.Success(false)
              )
            )
          )
        }
      }
    }

    @ExperimentalCoroutinesApi
    private suspend fun _downloadUpdate(getState: () -> State) {
      dispatch(Result.Value(getState().copy(downloadProgress = Event.Loading())))
      versionRepo.downloadUpdate(getState().version.downloadUrl).catch {
        dispatch(Result.Value(getState().copy(downloadProgress = Event.Error(it))))
      }.collect {
        dispatch(
          Result.Value(
            getState().copy(
              downloadProgress = Event.Success(it)
            )
          )
        )
      }
    }
  }

  private object ReducerImpl : Reducer<State, Result> {
    override fun State.reduce(result: Result): State =
      when (result) {
        is Result.Value -> result.state.copy()
      }
  }
}
