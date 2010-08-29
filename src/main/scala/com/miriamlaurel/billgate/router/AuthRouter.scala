package com.miriamlaurel.billgate.router

import ru.circumflex.core.RequestRouter
import com.miriamlaurel.billgate.model.Client

/**
 * @author Alexander Temerev
 */
class AuthRouter extends RequestRouter {

  post("/auth/session/+") = {
    val login = param("login")
    val password = param("password")
    val client = Client.byLogin(login)
    if (client.isDefined && client.get.auth(password)) {
      // create session and return session id (set cookie)
      "OK"
    } else {
      error(404, "AUTH FAILED")
    }
  }
}