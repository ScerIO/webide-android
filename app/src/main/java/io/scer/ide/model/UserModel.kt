package io.scer.ide.model

/**
 * User role
 */
enum class Role(val role: Int) {
    /**
     * Administrator
     */
    Admin(0),
    /**
     * User
     */
    User(1);

    companion object {
        private val map = Role.values()
        fun fromInt(type: Int) = map[type]
    }
}


/**
 * User
 */
interface UserModel {
    /**
     * Entity ID
     */
    val id: String

    /**
     * Email
     */
    val email: String

    /**
     * Role
     */
    val role: Int

    /**
     * Acess key
     */
    val token: String

    /**
     * First name
     */
    val firstName: String

    /**
     * Last name
     */
    val lastName: String

    /**
     * Avatar url
     */
    val picture: String
}