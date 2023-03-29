package mishin.trader.net.test.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mishin.trader.net.test.data.network.entity.QuotationRemoteData
import mishin.trader.net.test.domain.QuotationsUseCase

data class QuotationsViewState(
    val array: List<QuotationRemoteData> = listOf(),
    val inProgress: Boolean = false,
    val error: String? = null
)

class QuotationsViewModel(private val quotationsUseCase: QuotationsUseCase) : ViewModel() {
    private val _state = MutableStateFlow(QuotationsViewState())
    val state: StateFlow<QuotationsViewState> get() = _state

    init {
        viewModelScope.launch {
            kotlin.runCatching {
                quotationsUseCase.getTickers()
            }
                .onFailure { t ->
                    _state.update {
                        it.copy(error = t.message)
                    }
                }
                .onSuccess { result ->
                    _state.update {
                        it.copy(error = result.toString())
                    }
                }
        }
    }

    override fun onCleared() {

    }

    inline fun <reified T> List<Any>.find(): T? = find { it is T } as T
}