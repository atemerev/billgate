package com.miriamlaurel.billgate.test

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import com.miriamlaurel.billgate.security.digest._

/**
 * @author Alexander Temerev
 */
class DigestSuite extends FunSuite with ShouldMatchers {

  test("hash matching") {
    val password = "trustno1"
    val digest: String = hash(password)
    hashMatch(password, digest) should equal (true)
  }

  test("hex function") {
    hex("".getBytes("UTF-8")) should equal("")
    hex("Hello, world!".getBytes("UTF-8")) should equal ("48656c6c6f2c20776f726c6421")
  }

  test("salt function") {
    val s: String = salt()
    s.length should equal (2)
    s.toCharArray.forall(_.isLetterOrDigit) should equal (true)
  }

  test("sha256 digest") {
    sha256("Hello, world!") should equal ("315f5bdb76d078c43b8ac0064e4a0164612b1fce77c869345bfc94c75894edd3")
  }

  test("salted fries") {
    val s1: String = salt()
    val s2: String = otherSalt(s1)
    Array(s1, s2).map(hash(_)).forall(s => s.contains(':') && s.length == 67) should equal (true)
  }

  private def otherSalt(oldSalt: String): String = {
    val s = salt();
    if (s == oldSalt) otherSalt(oldSalt) else s
  }
}