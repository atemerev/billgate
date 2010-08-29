package com.miriamlaurel.billgate.security

import util.Random
import java.security.MessageDigest

/**
 * @author Alexander Temerev
 */
package object digest {

  def hashMatch(password: String, digest: String): Boolean = {
    val salt = digest.split(':')(0)
    hash(salt, password) == digest
  }

  def hash(password: String):String = hash(salt(), password)

  def hash(salt: String, password: String): String = salt + ":" + sha256(salt + password)

  def sha256(s: String) = hex(MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8")))

  def hex(bytes: Array[Byte]): String =
    bytes.map{ b => format("%02X", java.lang.Byte.valueOf(b)) }.mkString.toLowerCase

  def salt(): String = "" + randomChar('a', 'z') + randomChar('a', 'z')

  private def randomChar(from: Char, to: Char) = {
    val random = new Random
    (random.nextInt(to.toInt - from.toInt) + from.toInt).toChar
  }
}