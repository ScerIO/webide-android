package io.sker.ide.util

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class JSONConfig {

    companion object {

        fun makeJSONObjectWrapper(file: File): JSONConfig.JSONObjectWrapper {
            if (!file.exists())
                file.createNewFile()
            return when {
                (file.length().toInt() != 0) -> JSONConfig.JSONObjectWrapper(file, file.readText())
                else -> JSONConfig.JSONObjectWrapper(file)
            }
        }

        fun makeJSONArrayWrapper(file: File): JSONConfig.JSONArrayWrapper {
            if (!file.exists())
                file.createNewFile()
            return when {
                (file.length().toInt() != 0) -> JSONConfig.JSONArrayWrapper(file, file.readText())
                else -> JSONConfig.JSONArrayWrapper(file)
            }
        }

    }

    interface JSONWrapper {
        fun commit()
    }

    class JSONObjectWrapper : JSONObject, JSONConfig.JSONWrapper {

        private var config: File

        constructor(config: File): super() {
            this.config = config
        }

        constructor(config: File, content: String): super(content) {
            this.config = config
        }

        override fun put(name: String?, value: Any?): JSONConfig.JSONObjectWrapper {
            super.put(name, value)
            return this
        }

        override fun put(name: String?, value: Boolean): JSONConfig.JSONObjectWrapper {
            super.put(name, value)
            return this
        }

        override fun put(name: String?, value: Double): JSONConfig.JSONObjectWrapper {
            super.put(name, value)
            return this
        }

        override fun put(name: String?, value: Int): JSONConfig.JSONObjectWrapper {
            super.put(name, value)
            return this
        }

        override fun put(name: String?, value: Long): JSONConfig.JSONObjectWrapper {
            super.put(name, value)
            return this
        }

        override fun putOpt(name: String?, value: Any?): JSONConfig.JSONObjectWrapper {
            super.putOpt(name, value)
            return this
        }

        override fun commit () {
            if (!config.exists())
                config.createNewFile()
            config.writeText("")
            config.writeText(this.toString())
        }

    }

    class JSONArrayWrapper : JSONArray, JSONConfig.JSONWrapper {

        private var config: File

        constructor(config: File): super() {
            this.config = config
        }

        constructor(config: File, content: String): super(content) {
            this.config = config
        }

        override fun put(value: Any?): JSONConfig.JSONArrayWrapper {
            super.put(value)
            return this
        }

        override fun put(index: Int, value: Any?): JSONConfig.JSONArrayWrapper {
            super.put(index, value)
            return this
        }

        override fun put(value: Boolean): JSONConfig.JSONArrayWrapper {
            super.put(value)
            return this
        }

        override fun put(index: Int, value: Boolean): JSONConfig.JSONArrayWrapper {
            super.put(index, value)
            return this
        }

        override fun put(value: Double): JSONConfig.JSONArrayWrapper {
            super.put(value)
            return this
        }

        override fun put(index: Int, value: Double): JSONConfig.JSONArrayWrapper {
            super.put(index, value)
            return this
        }

        override fun put(value: Int): JSONConfig.JSONArrayWrapper {
            super.put( value)
            return this
        }

        override fun put(index: Int, value: Int): JSONConfig.JSONArrayWrapper {
            super.put(index, value)
            return this
        }

        override fun put(value: Long): JSONConfig.JSONArrayWrapper {
            super.put(value)
            return this
        }

        override fun put(index: Int, value: Long): JSONConfig.JSONArrayWrapper {
            super.put(index, value)
            return this
        }

        fun indexOf (string: String) = (0..this.length()).firstOrNull { this.get(it) == string } ?: -1

        override fun remove(index: Int): JSONConfig.JSONArrayWrapper {
            val output = JSONConfig.JSONArrayWrapper(this.config)
            val length = this.length()
            if (length > 1)
                (0 until length)
                        .filter { it != index }
                        .forEach { output.put(this.get(it)) }
            return output
        }

        override fun commit () {
            if (!config.exists())
                config.createNewFile()
            config.writeText("")
            config.writeText(this.toString())
        }

    }

}