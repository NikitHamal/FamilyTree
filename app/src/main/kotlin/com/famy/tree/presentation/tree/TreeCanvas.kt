package com.famy.tree.presentation.tree

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.Gender
import com.famy.tree.data.model.RelationshipType
import com.famy.tree.presentation.theme.FamyMaternal
import com.famy.tree.presentation.theme.FamyPaternal
import kotlin.math.max

@Composable
fun FamilyTreeCanvas(
    state: FamilyState,
    mode: TreeLayoutMode,
    modifier: Modifier = Modifier,
    onMemberClick: (String) -> Unit
) {
    val layout = remember(state.members, state.relationships, state.tree.rootMemberId, mode) {
        FamilyTreeLayoutEngine.compute(state, mode)
    }
    var scale by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }

    val cardColor = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outlineVariant
    val lineColor = MaterialTheme.colorScheme.outline
    val spouseColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
    val rootColor = MaterialTheme.colorScheme.tertiary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceSoft = MaterialTheme.colorScheme.onSurfaceVariant
    val defaultAccent = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(scale, pan, layout) {
                    detectTapGestures { tap ->
                        val world = Offset(
                            x = (tap.x - size.width / 2f - pan.x) / scale,
                            y = (tap.y - size.height / 3f - pan.y) / scale
                        )
                        layout.nodes.lastOrNull { it.contains(world) }?.let { onMemberClick(it.member.id) }
                    }
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, panChange, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.35f, 3.5f)
                        pan += panChange
                    }
                }
        ) {
            val origin = Offset(size.width / 2f + pan.x, size.height / 3f + pan.y)
            val viewport = Rect(
                left = (-origin.x) / scale - 260f,
                top = (-origin.y) / scale - 260f,
                right = (size.width - origin.x) / scale + 260f,
                bottom = (size.height - origin.y) / scale + 260f
            )

            withTransform({ translate(origin.x, origin.y); scale(scale, scale) }) {
                drawWorldGrid(viewport = viewport, strokeScale = scale, color = cardBorder.copy(alpha = 0.24f))

                layout.edges.forEach { edge ->
                    if (viewport.hasPoint(edge.start) || viewport.hasPoint(edge.end)) {
                        when (edge.type) {
                            RelationshipType.PARENT_CHILD -> {
                                val midY = (edge.start.y + edge.end.y) / 2f
                                val path = Path().apply {
                                    moveTo(edge.start.x, edge.start.y)
                                    lineTo(edge.start.x, midY)
                                    lineTo(edge.end.x, midY)
                                    lineTo(edge.end.x, edge.end.y)
                                }
                                drawPath(
                                    path = path,
                                    color = lineColor.copy(alpha = 0.72f),
                                    style = Stroke(width = 2.2f / max(scale, 0.7f), cap = StrokeCap.Round)
                                )
                            }
                            RelationshipType.SPOUSE -> {
                                drawLine(
                                    color = spouseColor,
                                    start = edge.start,
                                    end = edge.end,
                                    strokeWidth = 3f / max(scale, 0.7f),
                                    cap = StrokeCap.Round
                                )
                            }
                            RelationshipType.SIBLING -> {
                                drawLine(
                                    color = rootColor.copy(alpha = 0.55f),
                                    start = edge.start,
                                    end = edge.end,
                                    strokeWidth = 2f / max(scale, 0.7f),
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                layout.nodes.forEach { node ->
                    val left = node.center.x - node.width / 2f
                    val top = node.center.y - node.height / 2f
                    val bounds = Rect(left, top, left + node.width, top + node.height)
                    if (!bounds.intersects(viewport)) return@forEach

                    val accent = when (node.member.gender) {
                        Gender.MALE -> FamyPaternal
                        Gender.FEMALE -> FamyMaternal
                        else -> defaultAccent
                    }
                    val isRoot = node.member.id == (state.tree.rootMemberId ?: layout.nodes.firstOrNull()?.member?.id)

                    drawRoundRect(
                        color = cardColor.copy(alpha = 0.34f),
                        topLeft = Offset(left + 4f, top + 4f),
                        size = Size(node.width, node.height),
                        cornerRadius = CornerRadius(28f, 28f)
                    )
                    drawRoundRect(
                        color = cardColor,
                        topLeft = Offset(left, top),
                        size = Size(node.width, node.height),
                        cornerRadius = CornerRadius(28f, 28f)
                    )
                    drawRoundRect(
                        color = if (isRoot) rootColor.copy(alpha = 0.75f) else cardBorder,
                        topLeft = Offset(left, top),
                        size = Size(node.width, node.height),
                        cornerRadius = CornerRadius(28f, 28f),
                        style = Stroke(width = if (isRoot) 2.4f / max(scale, 0.7f) else 1.2f / max(scale, 0.7f))
                    )
                    drawRoundRect(
                        color = accent.copy(alpha = 0.92f),
                        topLeft = Offset(left, top),
                        size = Size(8f, node.height),
                        cornerRadius = CornerRadius(28f, 28f)
                    )

                    val avatarCenter = Offset(left + 30f, top + node.height / 2f)
                    drawCircle(color = accent.copy(alpha = 0.16f), radius = 20f, center = avatarCenter)
                    drawCircle(color = accent.copy(alpha = 0.32f), radius = 20f, center = avatarCenter, style = Stroke(width = 1.2f))

                    drawIntoCanvas { canvas ->
                        val nativeCanvas = canvas.nativeCanvas
                        val initialsPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            color = accent.toArgb()
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 13.sp.toPx()
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
                        }
                        nativeCanvas.drawText(node.member.initials, avatarCenter.x, avatarCenter.y + 4f, initialsPaint)

                        val namePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            color = onSurface.toArgb()
                            textSize = 15.sp.toPx()
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
                        }
                        val metaPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            color = onSurfaceSoft.toArgb()
                            textSize = 12.sp.toPx()
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.NORMAL)
                        }
                        val badgePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            color = if (isRoot) rootColor.copy(alpha = 0.95f).toArgb() else accent.copy(alpha = 0.85f).toArgb()
                            textSize = 10.sp.toPx()
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
                        }

                        nativeCanvas.drawText(node.member.displayName.take(22), left + 58f, top + 34f, namePaint)
                        val meta = listOf(
                            node.member.birthDate.takeIf { it.isNotBlank() },
                            node.member.birthPlace.takeIf { it.isNotBlank() }?.take(12)
                        ).filterNotNull().joinToString(" • ")
                        nativeCanvas.drawText(meta.ifBlank { if (node.member.isLiving) "Living profile" else "Archived profile" }.take(28), left + 58f, top + 56f, metaPaint)
                        nativeCanvas.drawText(if (isRoot) "FOCUS" else "GEN ${node.generation}", left + 58f, top + 72f, badgePaint)
                    }
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWorldGrid(viewport: Rect, strokeScale: Float, color: androidx.compose.ui.graphics.Color) {
    val spacing = 48f
    val startX = (viewport.left / spacing).toInt() * spacing
    val endX = (viewport.right / spacing).toInt() * spacing
    val startY = (viewport.top / spacing).toInt() * spacing
    val endY = (viewport.bottom / spacing).toInt() * spacing
    var x = startX
    while (x <= endX) {
        var y = startY
        while (y <= endY) {
            drawCircle(color = color, radius = 1.6f / max(strokeScale, 0.7f), center = Offset(x, y))
            y += spacing
        }
        x += spacing
    }
}


private fun Rect.hasPoint(point: Offset): Boolean = point.x >= left && point.x <= right && point.y >= top && point.y <= bottom

private fun Rect.intersects(other: Rect): Boolean = left < other.right && right > other.left && top < other.bottom && bottom > other.top
