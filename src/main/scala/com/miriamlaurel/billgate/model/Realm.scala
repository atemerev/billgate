package com.miriamlaurel.billgate.model

import ru.circumflex.orm.{Table, Record}

/**
 * @author Alexander Temerev
 */
class Realm extends Record[Realm] {
  val name = "name" VARCHAR(64)

  def this(name: String) = {
    this();
    this.name := name
  }
}

object Realm extends Table[Realm] {
  UNIQUE(this.name)
}