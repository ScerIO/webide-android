package io.scer.ide.model

/**
 * News model
 */
interface NewsModel {
    /**
     * Image remote url
     */
    val image: String
    /**
     * News title
     */
    val title: String
    /**
     * News descriptions
     */
    val description: String
    /**
     * News content
     */
    val content: String
    /**
     * Url
     */
    val link: String
}