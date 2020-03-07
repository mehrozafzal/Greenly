package greenely.greenely.utils

import me.jessyan.retrofiturlmanager.RetrofitUrlManager

fun isApiProduction(): Boolean {
    return RetrofitUrlManager.getInstance().globalDomain.toString().contains("api2")
}