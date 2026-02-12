package com.algonents.skyui.controls.layout

import kotlinx.html.FlowContent
import kotlinx.html.id
import kotlinx.html.main

fun FlowContent.main(mainContent: FlowContent.() -> Unit){
    main("main") {
        id = "main-panel"
        mainContent()
    }
}