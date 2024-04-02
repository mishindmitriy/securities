package trader.net.test.app.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


@Composable
fun QuotationCell(data: QuotationViewData, drawDivider: Boolean) {
    Column {
        firstRaw(data)
        secondRow(data)
        if (drawDivider) HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
    }

}

@Composable
private fun secondRow(data: QuotationViewData) {
    Row(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = data.name,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.weight(weight = 1f)
        )
        Text(
            text = data.priceChange,
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun firstRaw(data: QuotationViewData) {
    Row(
        modifier = Modifier
            .padding(
                start = 16.dp,
                top = 6.dp,
                end = 16.dp,
                bottom = 6.dp
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = data.logoUrl,
            contentDescription = data.ticker,
            modifier = Modifier
                .height(32.dp)
                .width(32.dp)
        )
        Text(
            text = data.ticker,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 6.dp)
                .weight(weight = 1f),
        )
        Text(
            text = data.percentChange,
            fontSize = 20.sp,
            color = Color(data.percentColor),
            modifier = Modifier,
        )
    }
}
