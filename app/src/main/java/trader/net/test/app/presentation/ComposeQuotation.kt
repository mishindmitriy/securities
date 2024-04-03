package trader.net.test.app.presentation

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import trader.net.test.app.R
import trader.net.test.app.domain.Quotation

@Composable
fun QuotationCell(data: QuotationViewData, drawDivider: Boolean) {
    Column {
        firstRaw(data)
        secondRow(data)
        if (drawDivider) HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
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
        val defaultColor = Color.Transparent
        val priceBackgroundColorState = remember(key1 = data.animate) { Animatable(defaultColor) }
        val priceTextColorState = remember(key1 = data.percentColor) {
            Animatable(Color(data.percentColor))
        }
        if (data.animate != Quotation.ChangeType.NONE) {
            val animateToColor = if (data.animate == Quotation.ChangeType.POSITIVE) {
                colorResource(id = R.color.green)
            } else {
                colorResource(id = R.color.red)
            }
            LaunchedEffect(Unit) {
                priceTextColorState.animateTo(
                    Color.White,
                    animationSpec = tween(PRICE_TEXT_ANIMATION_DURATION)
                )
                priceBackgroundColorState.animateTo(
                    animateToColor,
                    animationSpec = tween(PRICE_BACKGROUND_ANIMATION_DURATION)
                )
                priceBackgroundColorState.animateTo(
                    defaultColor,
                    animationSpec = tween(PRICE_BACKGROUND_ANIMATION_DURATION / 2)
                )
                priceTextColorState.animateTo(
                    Color(data.percentColor),
                    animationSpec = tween(PRICE_TEXT_ANIMATION_DURATION / 2)
                )
            }
        }
        Box(
            modifier = Modifier.background(
                priceBackgroundColorState.value,
                shape = RoundedCornerShape(8.dp)
            )
        )
        {
            Text(
                text = data.percentChange,
                fontSize = 20.sp,
                color = priceTextColorState.value,
                modifier = Modifier.padding(4.dp)
            )
        }
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
            text = data.priceWithChange,
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

private const val PRICE_BACKGROUND_ANIMATION_DURATION = 500
private const val PRICE_TEXT_ANIMATION_DURATION = 250
