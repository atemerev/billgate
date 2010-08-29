package com.miriamlaurel.billgate.model

import ru.circumflex.orm.{Table, Record}
import com.miriamlaurel.billgate.security.digest._

/**
 * @author Alexander Temerev
 */
class Client extends Record[Client] {
  val login = "login" VARCHAR(127)
  val password = "password" VARCHAR(255)
  val name = "name".VARCHAR(255).NULLABLE
  val realm = "realm_id" REFERENCES(Realm)
  val roles = inverse(Role.user)

  def auth(password: String): Boolean = hashMatch(password, this.password.get.get)
}

object Client extends Table[Client] {
  UNIQUE(this.login)

  def byLogin(l: String): Option[Client] = criteria.add(this.login LIKE l).unique  
}
