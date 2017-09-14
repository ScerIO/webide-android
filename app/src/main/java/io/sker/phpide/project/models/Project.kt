package io.sker.phpide.project.models

import io.sker.phpide.util.JSONConfig
import java.io.File

class Project (configFilePath: String) {

    val configFile: File = File(configFilePath)

    val config: JSONConfig.JSONObjectWrapper? get() = if (configFile.canRead()) JSONConfig.makeJSONObjectWrapper(configFile) else null

}