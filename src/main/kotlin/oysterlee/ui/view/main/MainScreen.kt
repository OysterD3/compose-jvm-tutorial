package oysterlee.ui.view.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import com.arkivanov.mvikotlin.extensions.coroutines.states
import kotlinx.coroutines.ExperimentalCoroutinesApi
import oysterlee.ui.common.Loading
import oysterlee.util.formatDateTime

@ExperimentalCoroutinesApi
@Composable
internal fun MainScreen(
  store: MainStore
) {
  val state = store.states.collectAsState(initial = State()).value

  UpdaterDialog(store = store, state = state)

  Button(
    onClick = {
      store.accept(Intent.CheckUpdate)
    }
  ) {
    Text("Check updates...")
  }
}

@ExperimentalCoroutinesApi
@Composable
fun UpdaterDialog(
  store: MainStore,
  state: State,
) {
  val dialogState = rememberDialogState(
    position = WindowPosition(Alignment.Center),
    size = WindowSize(500.dp, 250.dp)
  )
  DownloadDialog(store = store, state = state)

  Dialog(
    state = dialogState,
    title = "Updater",
    onCloseRequest = {
      store.accept(Intent.SetUpdateDialogVisibility(false))
    },
    visible = state.updateDialogVisible
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column {
        state.hasUpdate.let {
          when (it) {
            is Event.Loading -> Loading("Checking version")
            is Event.Success -> {
              if (it.data && state.version.version.isNotBlank()) {
                Text("New version found.")
                Text("Version: ${state.version.version}")
                Text("Size: ${state.version.size}MB")
                Text("Released date: ${formatDateTime(state.version.publishDate)}")
                Button(
                  onClick = {
                    store.accept(Intent.Download)
                  }
                ) {
                  Text("Download Update")
                }
              } else {
                Text("You are using latest version!")
              }
            }
            is Event.Error -> println(it.error.printStackTrace())
            else -> {
            }
          }
        }
      }
    }
  }
}

@ExperimentalCoroutinesApi
@Composable
fun DownloadDialog(
  store: MainStore,
  state: State,
) {
  val dialogState = rememberDialogState(
    position = WindowPosition(Alignment.Center),
    size = WindowSize(500.dp, 100.dp)
  )

  Dialog(
    state = dialogState,
    title = "Downloading Update",
    onCloseRequest = {
      store.accept(Intent.SetDownloadDialogVisibility(false))
    },
    visible = state.downloadDialogVisible
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(verticalArrangement = Arrangement.spacedBy(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        state.downloadProgress.let {
          when (it) {
            is Event.Loading -> Loading()
            is Event.Success -> {
              LinearProgressIndicator(
                progress = it.data.percentage.toFloat() / 100f,
                modifier = Modifier.height(10.dp)
              )
              Text("${it.data.percentage}%")
            }
            is Event.Error -> println(it.error.printStackTrace())
            else -> {
            }
          }
        }
      }
    }
  }
}
