package io.scer.ide.model

/**
 * Project model
 */
interface ProjectModel {
    /**
     * Project title
     */
    val title: String
    /**
     * Project description
     */
    val description: String
    /**
     * Path to project config file
     */
    val configPath: String
}