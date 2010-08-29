package com.miriamlaurel.billgate.router

import ru.circumflex.core.RequestRouter

/**
 * @author Alexander Temerev
 */
class Main extends RequestRouter {
  new AuthRouter

  get("/version") = "BillGate 1α1"
}