package io.scer.ide.project.models

import io.scer.ide.util.JSONConfig
import java.io.File

class Project (configFilePath: String) {

    val configFile: File = File(configFilePath)

    val config: JSONConfig.JSONObjectWrapper? get() = if (configFile.canRead()) JSONConfig.makeJSONObjectWrapper(configFile) else null

}