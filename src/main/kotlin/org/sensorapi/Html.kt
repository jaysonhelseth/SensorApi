package org.sensorapi

class Html {
    companion object {
        fun getPage(path: String): String {
            return object {}.javaClass.getResource(path).readText()
        }
    }
}