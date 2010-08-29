package com.miriamlaurel.billgate.model

import ru.circumflex.orm.{Table, Record}

/**
 * @author Alexander Temerev
 */
class Role extends Record[Role] {
  val name = "name" VARCHAR(127)
  val user = "client_id" REFERENCES(Client)
}

object Role extends Table[Role] {
  UNIQUE(this.name)
}