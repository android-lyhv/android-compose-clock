package com.lyhv.compose.compose_clock


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.util.*
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */

@Composable
fun ComposeClock() {
    Box(modifier = Modifier.fillMaxSize()) {


        val clockConfig = ClockConfig(
            Random()
        )
        ClockBackground(clockConfig)

        Box(
            Modifier
                .fillMaxSize()
                .padding(Dp(16f))
        ) {
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Background
            )
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Hour
            )
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Minute
            )

            ClockBackgroundBorder(clockConfig)
            ClockMinuteCircles(clockConfig)
            ClockSecondHand(clockConfig)
        }
    }
}

@Composable
private fun ClockBackground(clockConfig: ClockConfig) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .background(clockConfig.colorPalette.backgroundColor)
    )
}

@Composable
private fun ClockSecondHand(clockConfig: ClockConfig) {
    val interpolator = FastOutSlowInInterpolator()

    val animation = rememberInfiniteTransition()
    val second by animation.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    var startSecond by remember {
        mutableStateOf(Calendar.getInstance().get(Calendar.SECOND).toFloat())
    }
    Canvas(Modifier.fillMaxSize()) {
        val parentSize = size
        val clockRadius =
            0.9f * min((parentSize.width / 2), (parentSize.height / 2))
        val centerX = (parentSize.width / 2)
        val centerY = (parentSize.height / 2)
        val oneMinuteRadians = Math.PI / 30

        val currentSecondInMillisecond = System.currentTimeMillis() % 1000
        val progression = second//(currentSecondInMillisecond / 1000.0)
        /*if(progression == 0f){
            startSecond = (startSecond + 1) % 60
        }*/
        val interpolatedProgression = progression/*
            interpolator.getInterpolation(progression.toFloat())*/
        startSecond =
                /*Calendar.getInstance().get(Calendar.SECOND)*/
            startSecond + interpolatedProgression

        val degree = -Math.PI / 2 + (startSecond * oneMinuteRadians)
        val x = centerX + cos(degree) * clockRadius
        val y = centerY + sin(degree) * clockRadius

        val radius = 8f
        drawCircle(
            clockConfig.colorPalette.handleColor,
            radius,
            (Offset(x.toFloat(), y.toFloat())),
            style = Fill
        )
    }

}

@Composable
private fun ClockMinuteCircles(clockConfig: ClockConfig) {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val parentSize = size
        val clockRadius = 0.95f * kotlin.math.min((parentSize.width / 2), (parentSize.height / 2))
        val paint = Paint().apply {
            style = PaintingStyle.Fill
            color = clockConfig.colorPalette.handleColor
        }
        val centerX = (parentSize.width / 2)
        val centerY = (parentSize.height / 2)
        val oneMinuteRadians = Math.PI / 30
        0.rangeTo(59).forEach { minute ->
            val isHour = minute % 5 == 0
            val degree = -Math.PI / 2 + (minute * oneMinuteRadians)
            val x = centerX + cos(degree) * clockRadius
            val y = centerY + sin(degree) * clockRadius

            val radius: Float
            if (isHour) {
                paint.style = PaintingStyle.Fill
                radius = 12f
            } else {
                paint.style = PaintingStyle.Stroke
                radius = 6f
            }
            drawCircle(
                clockConfig.colorPalette.handleColor,
                radius,
                center = (Offset(x.toFloat(), y.toFloat())),
            )
        }

    }
}

@Composable
private fun ClockBackgroundBorder(clockConfig: ClockConfig) {
    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
        val parentSize = size
        val radius = kotlin.math.min((parentSize.width / 2), (parentSize.height / 2)) * 0.9f
        drawCircle(
            clockConfig.colorPalette.borderColor,
            radius,
            (Offset((parentSize.width / 2), (parentSize.height / 2))),
            style = Stroke(10.dp.toPx())
        )
    }
}

@Composable
fun ParticleHeartBeat(
    clockConfig: ClockConfig,
    type: ParticleObject.Type
) {
    val animation = rememberInfiniteTransition()
    val particles = remember { mutableStateOf(listOf<ParticleObject>()) }
    val animated by animation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = CubicBezierEasing(0.98f, 0.2f, 1.0f, 1.0f)),
            repeatMode = RepeatMode.Reverse
        )
    )

    androidx.compose.foundation.Canvas(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged {
            particles.value = 1
                .rangeTo(100)
                .map {
                    ParticleObject(type, clockConfig)
                }
        }) {
        particles.value.forEach { particle ->
            particle.animate(
                this@Canvas,
                animated,
                size
            )
            drawParticle(particle)
        }

    }
}

private fun ParticleObject.animate(
    drawScope: DrawScope,
    progress: Float,
    size: Size
) {

    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = min(centerX, centerY)
    val random = Random()
    val modifier =
        with(drawScope) { progress * animationParams.progressModifier.dp.toPx() }//* randomFloat(1f, 4f, random) }
    val xUpdate = modifier * cos(animationParams.currentAngle)
    val yUpdate = modifier * sin(animationParams.currentAngle)
    val newX = animationParams.locationX.value + xUpdate
    val newY = animationParams.locationY.value + yUpdate

    val positionInsideCircle =
        hypot(newY - centerY, newX - centerX)
    val currentPositionIsInsideCircle = positionInsideCircle < radius * type.maxLengthModifier
    val currentLengthByRadius = positionInsideCircle / (radius * type.maxLengthModifier)
    when {
        currentLengthByRadius - type.minLengthModifier <= 0f -> {
            animationParams.alpha = 0f
        }

        animationParams.alpha == 0f -> {
            animationParams.alpha = random.nextFloat()
        }

        else -> {
            val fadeOutRange = this.type.maxLengthModifier
            animationParams.alpha =
                (if (currentLengthByRadius < fadeOutRange) animationParams.alpha else ((1f - currentLengthByRadius) / (1f - fadeOutRange))).coerceIn(
                    0f,
                    1f
                )
        }
    }
    if (!currentPositionIsInsideCircle) {
        randomize(random, size)
        animationParams.alpha = 0f
    } else {
        animationParams.locationX = Dp(newX)
        animationParams.locationY = Dp(newY)
    }
}

private fun DrawScope.drawParticle(particleObject: ParticleObject) {

    val centerW = particleObject.animationParams.locationX.value
    val centerH = particleObject.animationParams.locationY.value
    drawCircle(
        SolidColor(particleObject.animationParams.currentColor),
        particleObject.animationParams.particleSize.value / 2f,
        Offset(centerW, centerH),
        style = if (particleObject.animationParams.isFilled) Fill else Stroke(1.dp.toPx()),
        alpha = particleObject.animationParams.alpha
    )
}

private fun ParticleObject.drawOnCanvas(paint: Paint, canvas: DrawScope) {
    canvas.apply {
        paint.color = animationParams.currentColor
        paint.alpha = animationParams.alpha
        val centerW = animationParams.locationX.value
        val centerH = animationParams.locationY.value
        if (animationParams.isFilled) {
            paint.style = PaintingStyle.Fill
        } else {
            paint.style = PaintingStyle.Stroke
        }
        drawCircle(
            animationParams.currentColor,
            animationParams.particleSize.value / 2f,
            Offset(centerW, centerH),
        )
    }
}

private fun ParticleObject.randomize(
    random: Random,
    pxSize: Size
) {
    val calendar = Calendar.getInstance()
    val currentMinuteCount = calendar.get(Calendar.MINUTE)
    val currentHour = (calendar.get(Calendar.HOUR_OF_DAY) % 24).toDouble() / 12.0
    val currentMinute = (currentMinuteCount).toDouble() / 60.0
    val currentMinuteRadians = (Math.PI / -2.0) + currentMinute * 2.0 * Math.PI
    val oneHourRadian = (currentMinute * 2.0 * Math.PI) / 12.0
    val currentHourRadians =
        (Math.PI / -2.0) + (currentHour) * 2.0 * Math.PI
    val currentHourMinuteRadians = (oneHourRadian * currentMinute) + currentHourRadians
    val randomAngleOffset =
        randomFloat(type.startAngleOffsetRadians, type.endAngleOffsetRadians, random)
    val randomizedAngle = when (type) {
        ParticleObject.Type.Hour -> currentHourMinuteRadians.toFloat()
        ParticleObject.Type.Minute -> currentMinuteRadians.toFloat()
        ParticleObject.Type.Background -> (currentHourMinuteRadians + randomAngleOffset).toFloat()
    }
    val centerX = (pxSize.width / 2) + randomFloat(-10f, 10f, random)
    val centerY = (pxSize.height / 2) + randomFloat(-10f, 10f, random)
    val radius = min(centerX, centerY)
    val randomLength =
        randomFloat(type.minLengthModifier * radius, this.type.maxLengthModifier * radius, random)
    val x = randomLength * cos(randomizedAngle)
    val y = randomLength * sin(randomizedAngle)
    val color = when (type) {
        ParticleObject.Type.Background -> clockConfig.colorPalette.mainColors.random()
        ParticleObject.Type.Hour -> clockConfig.colorPalette.handleColor
        ParticleObject.Type.Minute -> clockConfig.colorPalette.handleColor
    }
    animationParams = ParticleObject.AnimationParams(
        isFilled = clockConfig.random.nextFloat() < 0.7f,
        alpha = (random.nextFloat()).coerceAtLeast(0f),
        locationX = Dp(centerX + x),
        locationY = Dp(centerY + y),
        particleSize = Dp(randomFloat(type.minSize.value, type.maxSize.value, random)),
        currentAngle = randomizedAngle.toFloat(),
        progressModifier = randomFloat(1f, 8f, random),
        currentColor = color
    )
}