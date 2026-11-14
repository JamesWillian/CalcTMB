package com.jammes.calctmb.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jammes.calctmb.R
import com.jammes.calctmb.ui.theme.AppTheme
import com.jammes.calctmb.ui.theme.BrandSurface
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

/**
 * Convite para avaliar o app, logo depois de um cálculo salvo. O topo usa a superfície de
 * marca, com a logo sob um holofote e brilhos âmbar, para o convite parecer parte da
 * conquista e não uma cobrança.
 *
 * O texto convida sem perguntar nada: a Google Play pede que o app não faça perguntas antes
 * do cartão de avaliação, nem sobre a opinião ("Está gostando?") nem sobre a nota.
 */
@Composable
fun ReviewInviteDialog(
    onRate: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // O cartão entra crescendo de leve, em vez de só aparecer.
        val appearance = remember { MutableTransitionState(false).apply { targetState = true } }
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visibleState = appearance,
                enter = fadeIn(tween(durationMillis = 180)) + scaleIn(
                    initialScale = 0.88f,
                    animationSpec = spring(
                        dampingRatio = 0.7f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            ) {
                ReviewInviteCard(
                    onRate = onRate,
                    onDismiss = onDismiss,
                    // Celular deitado: pouca altura, então o topo encolhe e os botões se juntam.
                    compact = maxHeight < COMPACT_WINDOW_HEIGHT,
                    scrollable = constraints.hasBoundedHeight,
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 16.dp)
                        .widthIn(max = 400.dp)
                )
            }
        }
    }
}

@Composable
private fun ReviewInviteCard(
    onRate: () -> Unit,
    onDismiss: () -> Unit,
    compact: Boolean,
    scrollable: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = if (scrollable) Modifier.verticalScroll(scrollState) else Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ReviewHero(height = if (compact) COMPACT_HERO_HEIGHT else HERO_HEIGHT)
            // Metade do selo fica sobre o topo, costurando as duas partes do cartão.
            SavedBadge(modifier = Modifier.overlapPreviousByHalf())
            Column(
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 14.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.convite_avaliacao_titulo),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.convite_avaliacao_mensagem,
                        stringResource(R.string.app_name)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                if (compact) {
                    // Deitado, os botões vão lado a lado para o "Agora não" não ficar escondido.
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NotNowButton(onClick = onDismiss, modifier = Modifier.weight(1f))
                        RateButton(onClick = onRate, modifier = Modifier.weight(1f))
                    }
                } else {
                    Spacer(Modifier.height(24.dp))
                    RateButton(onClick = onRate, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(4.dp))
                    NotNowButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun RateButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 52.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = stringResource(R.string.avaliar_app),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun NotNowButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = stringResource(R.string.agora_nao),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * O palco da logo: navy da marca, um holofote que respira devagar e brilhos que piscam
 * fora de compasso. Tudo é decoração, então fica fora do TalkBack.
 */
@Composable
private fun ReviewHero(height: Dp, modifier: Modifier = Modifier) {
    // Tudo o que é desenhado acompanha a altura, para a versão compacta manter o desenho.
    val scale = height / HERO_HEIGHT

    val twinkle = rememberInfiniteTransition(label = "twinkle")
    val time by twinkle.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(TWINKLE_PERIOD_MS, easing = LinearEasing)),
        label = "time"
    )

    // A logo entra com um pulinho, e a estrela chega logo depois.
    val logoScale = remember { Animatable(0.4f) }
    val badgeScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    LaunchedEffect(Unit) {
        delay(BADGE_DELAY_MS)
        badgeScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(HeroShape)
            .background(Brush.verticalGradient(listOf(BrandSurface.NavyElevated, BrandSurface.Navy)))
            .drawWithCache {
                val sparklePath = Path()
                onDrawBehind {
                    // O estado é lido só aqui: a animação redesenha, sem recompor nada.
                    drawGlow(time, scale)
                    drawSky(sparklePath, time, scale)
                }
            }
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo_tmb),
            contentDescription = null,
            modifier = Modifier
                .size(LOGO_SIZE * scale)
                .graphicsLayer {
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                }
        )
        StarBadge(
            modifier = Modifier
                .offset(x = BADGE_OFFSET * scale, y = BADGE_OFFSET * scale)
                .size(BADGE_SIZE * scale)
                .graphicsLayer {
                    scaleX = badgeScale.value
                    scaleY = badgeScale.value
                }
        )
    }
}

/** A estrela no canto da logo; o contorno navy a destaca do anel colorido. */
@Composable
private fun StarBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(BrandSurface.Navy, CircleShape)
            .padding(3.dp)
            .background(BrandSurface.Accent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = BrandSurface.Navy,
            modifier = Modifier.fillMaxSize(0.62f)
        )
    }
}

/** Lembra o que acabou de acontecer: o convite vem por causa de um cálculo salvo. */
@Composable
private fun SavedBadge(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            // Um aro da cor do cartão separa o selo do topo navy.
            .background(colors.surfaceContainerHigh, CircleShape)
            .padding(3.dp)
            .background(colors.secondaryContainer, CircleShape)
            .padding(start = 10.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = colors.onSecondaryContainer,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = stringResource(R.string.convite_avaliacao_selo),
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.25.sp),
            color = colors.onSecondaryContainer
        )
    }
}

/**
 * Sobe o elemento metade da própria altura e ocupa só a outra metade no layout, para ele
 * ficar centrado na borda de baixo do que vem antes, seja qual for o tamanho da fonte.
 */
private fun Modifier.overlapPreviousByHalf(): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val half = placeable.height / 2
    layout(placeable.width, placeable.height - half) {
        placeable.place(0, -half)
    }
}

/**
 * Um holofote claro atrás da logo, que cresce e encolhe devagar. É claro e não âmbar: âmbar
 * translúcido sobre o navy vira marrom, e o âmbar fica para os brilhos, a estrela e o botão.
 */
private fun DrawScope.drawGlow(time: Float, scale: Float) {
    val breath = sin(2 * PI * time).toFloat()
    val radius = GLOW_RADIUS.toPx() * scale * (1f + 0.06f * breath)
    drawCircle(
        brush = Brush.radialGradient(
            0f to BrandSurface.OnNavy.copy(alpha = 0.18f),
            0.55f to BrandSurface.OnNavy.copy(alpha = 0.07f),
            1f to Color.Transparent,
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

/** Pontinhos fixos ao fundo e brilhos de quatro pontas que piscam, cada um no seu tempo. */
private fun DrawScope.drawSky(path: Path, time: Float, scale: Float) {
    Dots.forEach { dot ->
        drawCircle(
            color = BrandSurface.OnNavy.copy(alpha = 0.35f),
            radius = dot.radius.toPx() * scale,
            center = center + Offset(dot.x.toPx() * scale, dot.y.toPx() * scale)
        )
    }
    Sparkles.forEach { sparkle ->
        val pulse = 0.5f + 0.5f * sin(2 * PI * (time + sparkle.phase)).toFloat()
        // Piscam mais no tamanho que na transparência: âmbar apagado também amarronza.
        drawSparkle(
            path = path,
            at = center + Offset(sparkle.x.toPx() * scale, sparkle.y.toPx() * scale),
            radius = sparkle.radius.toPx() * scale * (0.55f + 0.45f * pulse),
            color = sparkle.color.copy(alpha = 0.6f + 0.4f * pulse)
        )
    }
}

/** Brilho de quatro pontas (✦): entre uma ponta e outra, a curva é puxada para o centro. */
private fun DrawScope.drawSparkle(path: Path, at: Offset, radius: Float, color: Color) {
    val pinch = radius * 0.18f
    path.reset()
    path.moveTo(at.x, at.y - radius)
    path.quadraticTo(at.x + pinch, at.y - pinch, at.x + radius, at.y)
    path.quadraticTo(at.x + pinch, at.y + pinch, at.x, at.y + radius)
    path.quadraticTo(at.x - pinch, at.y + pinch, at.x - radius, at.y)
    path.quadraticTo(at.x - pinch, at.y - pinch, at.x, at.y - radius)
    path.close()
    drawPath(path, color)
}

/** O topo termina numa curva suave em vez de uma linha reta. */
private val HeroShape = GenericShape { size, _ ->
    val curve = size.height * 0.12f
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height - curve)
    quadraticTo(size.width / 2f, size.height + curve, 0f, size.height - curve)
    close()
}

/** Posições a partir do centro do topo, na altura cheia; [phase] desencontra as piscadas. */
private class Sparkle(val x: Dp, val y: Dp, val radius: Dp, val phase: Float, val color: Color)

private class Dot(val x: Dp, val y: Dp, val radius: Dp)

private val Sparkles = listOf(
    Sparkle(x = (-108).dp, y = (-44).dp, radius = 9.dp, phase = 0f, color = BrandSurface.Accent),
    Sparkle(x = (-76).dp, y = 46.dp, radius = 6.dp, phase = 0.4f, color = BrandSurface.OnNavy),
    Sparkle(x = 100.dp, y = (-40).dp, radius = 7.dp, phase = 0.65f, color = BrandSurface.OnNavy),
    Sparkle(x = 114.dp, y = 30.dp, radius = 10.dp, phase = 0.2f, color = BrandSurface.Accent),
    Sparkle(x = 58.dp, y = (-64).dp, radius = 5.dp, phase = 0.85f, color = BrandSurface.Accent),
    Sparkle(x = (-130).dp, y = 10.dp, radius = 5.dp, phase = 0.55f, color = BrandSurface.Accent)
)

private val Dots = listOf(
    Dot(x = (-138).dp, y = (-62).dp, radius = 1.5.dp),
    Dot(x = (-40).dp, y = (-72).dp, radius = 1.dp),
    Dot(x = 34.dp, y = 66.dp, radius = 1.5.dp),
    Dot(x = 140.dp, y = (-6).dp, radius = 1.dp),
    Dot(x = (-118).dp, y = 56.dp, radius = 1.dp),
    Dot(x = 86.dp, y = (-72).dp, radius = 1.5.dp)
)

private val HERO_HEIGHT = 184.dp
private val COMPACT_HERO_HEIGHT = 96.dp
private val COMPACT_WINDOW_HEIGHT = 560.dp
private val LOGO_SIZE = 132.dp
private val GLOW_RADIUS = 84.dp
private val BADGE_SIZE = 32.dp

// O anel da logo ocupa ~59% da imagem: a estrela fica sobre a borda dele, a 45°.
private val BADGE_OFFSET = 28.dp
private const val BADGE_DELAY_MS = 280L
private const val TWINKLE_PERIOD_MS = 2_600

@Preview(name = "Claro", showBackground = true, backgroundColor = 0xFF8A8F99)
@Composable
private fun ReviewInviteCardPreview() {
    AppTheme(darkTheme = false) {
        ReviewInviteCard(
            onRate = {},
            onDismiss = {},
            compact = false,
            scrollable = false,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Preview(name = "Escuro", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ReviewInviteCardDarkPreview() {
    AppTheme(darkTheme = true) {
        ReviewInviteCard(
            onRate = {},
            onDismiss = {},
            compact = false,
            scrollable = false,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Preview(name = "Deitado", showBackground = true, backgroundColor = 0xFF8A8F99, widthDp = 460)
@Composable
private fun ReviewInviteCardCompactPreview() {
    AppTheme(darkTheme = false) {
        ReviewInviteCard(
            onRate = {},
            onDismiss = {},
            compact = true,
            scrollable = false,
            modifier = Modifier.padding(24.dp)
        )
    }
}
