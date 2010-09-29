package com.miriamlaurel.billgate.security.session.dao

trait Purgeable {

  /**
   * Used to purge objects from internal storage
   */
  def purge();

}