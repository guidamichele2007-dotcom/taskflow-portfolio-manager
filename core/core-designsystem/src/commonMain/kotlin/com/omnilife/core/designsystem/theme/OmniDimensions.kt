package com.omnilife.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.omnilife.core.designtokens.OmniBorder
import com.omnilife.core.designtokens.OmniShape
import com.omnilife.core.designtokens.OmniSpacing
import com.omnilife.core.designtokens.OmniTouchTarget

/** [OmniSpacing] converted to [Dp] (Design System Bible §01 §1). */
@Immutable
public data class OmniSpacingDp(
    public val spazio05: Dp,
    public val spazio1: Dp,
    public val spazio2: Dp,
    public val spazio3: Dp,
    public val spazio4: Dp,
    public val spazio6: Dp,
    public val spazio8: Dp,
    public val touchTargetMinimo: Dp,
)

internal fun buildOmniSpacingDp(): OmniSpacingDp =
    OmniSpacingDp(
        spazio05 = OmniSpacing.SPAZIO_05.dp,
        spazio1 = OmniSpacing.SPAZIO_1.dp,
        spazio2 = OmniSpacing.SPAZIO_2.dp,
        spazio3 = OmniSpacing.SPAZIO_3.dp,
        spazio4 = OmniSpacing.SPAZIO_4.dp,
        spazio6 = OmniSpacing.SPAZIO_6.dp,
        spazio8 = OmniSpacing.SPAZIO_8.dp,
        touchTargetMinimo = OmniTouchTarget.MINIMO_DP.dp,
    )

/** [OmniShape] radii as [RoundedCornerShape]s, plus the fully-round "pieno" shape (Design System Bible §01 §5). */
@Immutable
public data class OmniShapes(
    public val piccolo: RoundedCornerShape,
    public val medio: RoundedCornerShape,
    public val grande: RoundedCornerShape,
    public val pieno: RoundedCornerShape,
)

internal fun buildOmniShapes(): OmniShapes =
    OmniShapes(
        piccolo = RoundedCornerShape(OmniShape.RAGGIO_PICCOLO_DP.dp),
        medio = RoundedCornerShape(OmniShape.RAGGIO_MEDIO_DP.dp),
        grande = RoundedCornerShape(OmniShape.RAGGIO_GRANDE_DP.dp),
        pieno = RoundedCornerShape(percent = 50),
    )

/** [OmniBorder] thicknesses as [Dp]. */
@Immutable
public data class OmniBorderDp(
    public val spessoreDefault: Dp,
    public val spessoreFocus: Dp,
)

internal fun buildOmniBorderDp(): OmniBorderDp =
    OmniBorderDp(
        spessoreDefault = OmniBorder.SPESSORE_DEFAULT_DP.dp,
        spessoreFocus = OmniBorder.SPESSORE_FOCUS_DP.dp,
    )
