package com.algonents.skyui.controls

import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.i

fun FlowContent.iconButton(
    label: String,
    iconClass: String,
    extraClasses: Set<String> = emptySet(),
){
    button {
        classes = setOf("small-button") + extraClasses

        i(classes = iconClass) {}
        +" $label"
    }

}