package trader.net.test.app.presentation


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.viewmodel.ext.android.viewModel
import trader.net.test.app.R

class MainActivity : ComponentActivity() {
    private val viewModel: QuotationsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { renderState() }
    }

    @Composable
    private fun renderState() {
        val state = viewModel.state.collectAsState()
        when {
            state.value.inProgress -> progressScreen()
            state.value.error != null -> errorScreen(state.value.error!!)
            state.value.list.isNotEmpty() -> quotationsList(state.value.list)
        }
    }

    @Composable
    private fun progressScreen() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }

    @Composable
    private fun errorScreen(error: String) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Text(text = error)
                Button(onClick = { viewModel.loadData() }) {
                    Text(text = getString(R.string.repeat_label))
                }
            }
        }
    }

    @Composable
    private fun quotationsList(list: List<QuotationViewData>) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(
                items = list,
                key = { _, item -> item.ticker }
            ) { index, item ->
                QuotationCell(data = item, index < list.lastIndex)
            }
        }
    }
}
