package com.algonents.skyui

import kotlinx.html.FlowContent
import kotlinx.html.HEAD
import kotlinx.html.HTMLTag
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.unsafe

class AssetRegistry {
    val cssFiles = LinkedHashSet<String>()
    val jsFiles = LinkedHashSet<String>()
}

class SkyUI(val registry: AssetRegistry? = null) {
    fun requireCss(fc: FlowContent, path: String) {
        if (registry != null) {
            registry.cssFiles.add(path)
        } else {
            (fc as HTMLTag).unsafe {
                +"""<script>if(!document.querySelector('link[href="$path"]')){var l=document.createElement('link');l.rel='stylesheet';l.href='$path';document.head.appendChild(l)}</script>"""
            }
        }
    }

    fun requireJs(fc: FlowContent, path: String) {
        if (registry != null) {
            registry.jsFiles.add(path)
        } else {
            (fc as HTMLTag).unsafe {
                +"""<script>if(!document.querySelector('script[src="$path"]')){var s=document.createElement('script');s.src='$path';document.head.appendChild(s)}</script>"""
            }
        }
    }
}

fun HEAD.skyuiHead(ui: SkyUI) {
    link(rel = "stylesheet", href = "https://unpkg.com/leaflet/dist/leaflet.css")
    script(src = "https://kit.fontawesome.com/35cad2ef35.js") {}

    link(rel = "stylesheet", href = "/static/css/skyui-base.css")

    ui.registry?.cssFiles?.forEach { cssPath ->
        link(rel = "stylesheet", href = cssPath)
    }

    script(src = "https://unpkg.com/htmx.org@1.9.10") {}
    script(src = "https://unpkg.com/inputmask@5.0.8/dist/inputmask.min.js") {}
    script(src = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js") {}

    ui.registry?.jsFiles?.forEach { jsPath ->
        script(src = jsPath) {}
    }
}