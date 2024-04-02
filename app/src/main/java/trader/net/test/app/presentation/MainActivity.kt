package trader.net.test.app.presentation


import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import trader.net.test.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel: QuotationsViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    private val quotationsAdapter = QuotationsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.recycler.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        binding.recycler.adapter = quotationsAdapter
        binding.repeatButton.setOnClickListener { viewModel.loadData() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    quotationsAdapter.updateFields(it.list)
                    binding.progress.isVisible = it.inProgress
                    binding.error.isVisible = it.error != null
                    binding.repeatButton.isVisible = it.error != null
                    binding.error.text = it.error
                    binding.recycler.isVisible = it.list.isNotEmpty() && it.error == null
                }
            }
        }
    }
}
