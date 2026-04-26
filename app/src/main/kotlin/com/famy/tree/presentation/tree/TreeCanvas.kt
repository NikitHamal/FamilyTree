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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
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
    val layout = remember(state.members, state.relationships, mode) { FamilyTreeLayoutEngine.compute(state, mode) }
    var scale by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outlineVariant
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val secondaryContainer = MaterialTheme.colorScheme.secondaryContainer
    val errorContainer = MaterialTheme.colorScheme.errorContainer

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(scale, pan, layout) {
                    detectTapGestures { tap ->
                        val world = Offset(
                            x = (tap.x - size.width / 2f - pan.x) / scale,
                            y = (tap.y - 96f - pan.y) / scale
                        )
                        layout.nodes.lastOrNull { it.contains(world) }?.let { onMemberClick(it.member.id) }
                    }
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, panChange, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.35f, 3.2f)
                        pan += panChange
                    }
                }
        ) {
            val origin = Offset(size.width / 2f + pan.x, 96f + pan.y)
            val viewport = Rect(
                left = (-origin.x) / scale - 240f,
                top = (-origin.y) / scale - 240f,
                right = (size.width - origin.x) / scale + 240f,
                bottom = (size.height - origin.y) / scale + 240f
            )
            withTransform({ translate(origin.x, origin.y); scale(scale, scale) }) {
                layout.edges.forEach { edge ->
                    if (viewport.contains(edge.start) || viewport.contains(edge.end)) {
                        val color = when (edge.type) {
                            RelationshipType.PARENT_CHILD -> outline
                            RelationshipType.SPOUSE -> primary.copy(alpha = 0.62f)
                            RelationshipType.SIBLING -> secondaryContainer
                        }
                        drawLine(color = color, start = edge.start, end = edge.end, strokeWidth = 3f / max(scale, 0.7f), cap = StrokeCap.Round)
                    }
                }
                layout.nodes.forEach { node ->
                    val bounds = Rect(node.center.x - node.width / 2f, node.center.y - node.height / 2f, node.center.x + node.width / 2f, node.center.y + node.height / 2f)
                    if (bounds.overlaps(viewport)) {
                        val accent = when (node.member.gender) {
                            Gender.MALE -> FamyPaternal
                            Gender.FEMALE -> FamyMaternal
                            else -> primary
                        }
                        val fill = if (node.member.isLiving) surface else errorContainer.copy(alpha = 0.22f)
                        drawRoundRect(
                            color = fill,
                            topLeft = Offset(bounds.left, bounds.top),
                            size = Size(node.width, node.height),
                            cornerRadius = CornerRadius(24f, 24f)
                        )
                        drawRoundRect(
                            color = accent.copy(alpha = 0.88f),
                            topLeft = Offset(bounds.left, bounds.top),
                            size = Size(8f, node.height),
                            cornerRadius = CornerRadius(24f, 24f)
                        )
                        drawRoundRect(
                            color = outline.copy(alpha = 0.55f),
                            topLeft = Offset(bounds.left, bounds.top),
                            size = Size(node.width, node.height),
                            cornerRadius = CornerRadius(24f, 24f),
                            style = Stroke(width = 1.2f / max(scale, 0.7f))
                        )
                        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            color = onSurface.toArgb()
                            textSize = 15.sp.toPx()
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
                        }
                        drawContext.canvas.nativeCanvas.drawText(node.member.displayName.take(22), bounds.left + 18f, node.center.y - 8f, paint)
                        val detail = listOfNotNull(node.member.birthDate.takeIf { it.isNotBlank() }, node.member.birthPlace.takeIf { it.isNotBlank() }).joinToString(" • ")
                        val small = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            color = onSurface.copy(alpha = 0.68f).toArgb()
                            textSize = 12.sp.toPx()
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.NORMAL)
                        }
                        drawContext.canvas.nativeCanvas.drawText(detail.take(28), bounds.left + 18f, node.center.y + 16f, small)
                    }
                }
            }
        }
    }
}
