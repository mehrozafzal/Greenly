package greenely.greenely.utils

class GreenelySingleton {

    private var isInvitedUser: Boolean = false

    companion object {
        var instance: GreenelySingleton? = null
            get() {
                if (instance == null)
                    instance = GreenelySingleton()
                return instance
            }

    }

     fun setIsInvitedUser(boolean: Boolean) {
        isInvitedUser = boolean
    }


    fun getIsInvitedUser(): Boolean {
        return isInvitedUser
    }

}