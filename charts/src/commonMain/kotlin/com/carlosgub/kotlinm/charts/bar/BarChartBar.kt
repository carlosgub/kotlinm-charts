package com.carlosgub.kotlinm.charts.bar

data class BarChartBar(
    val width: ClosedFloatingPointRange<Float>,
    val height: ClosedFloatingPointRange<Float>,
    val data: BarChartEntry
)
