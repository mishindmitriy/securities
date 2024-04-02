package trader.net.test.app.presentation


import android.os.Bundle
import android.view.LayoutInflater
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
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import trader.net.test.app.R
import trader.net.test.app.databinding.ActivityMainBinding
import trader.net.test.app.presentation.adapter.QuotationsAdapter

class MainActivity : ComponentActivity() {
    private val viewModel: QuotationsViewModel by viewModel()

    private val compose = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        if (compose) renderCompose() else renderXml()
    }

    private fun renderCompose() {
        setContent { render() }
    }

    @Composable
    private fun render() {
        val state = viewModel.state.collectAsState()
        when {
            state.value.inProgress -> progressScreen()
            state.value.error != null -> errorScreen(state.value.error!!)
            state.value.list.isNotEmpty() -> quotationsList(state.value.list)
            //todo add error state
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


    private fun renderXml() {
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.recycler.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        val quotationsAdapter = QuotationsAdapter()
        binding.recycler.adapter = quotationsAdapter
        binding.repeatButton.setOnClickListener { viewModel.loadData() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    quotationsAdapter.submitList(state.list)
                    binding.progress.isVisible = state.inProgress
                    binding.error.isVisible = state.error != null
                    binding.repeatButton.isVisible = state.error != null
                    binding.error.text = state.error
                    binding.recycler.isVisible = state.list.isNotEmpty() && state.error == null
                }
            }
        }
    }
}
