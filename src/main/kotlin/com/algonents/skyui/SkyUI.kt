package com.algonents.skyui

import kotlinx.html.FlowContent
import kotlinx.html.HTMLTag
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