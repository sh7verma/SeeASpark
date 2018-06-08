package models

/**
 * Created by applify on 6/7/2018.
 */
abstract class BaseModel {
    var error: Error? = null

    inner class Error {
        var code: String? = null
        var message: String? = null
    }

}