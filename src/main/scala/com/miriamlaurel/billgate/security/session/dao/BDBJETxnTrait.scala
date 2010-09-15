package com.miriamlaurel.billgate.security.session.dao

import com.sleepycat.je.{Transaction, Environment}

/**
 * Created by IntelliJ IDEA.
 * User: bofh
 * Date: Sep 12, 2010
 * Time: 9:42:09 PM
 * To change this template use File | Settings | File Templates.
 */

trait BDBJETxnTrait {

  def withTransactionDo(env : Environment)(action : Transaction => Unit) = {
    var txn : Transaction = null;
    var status = false
    try {
      txn = env beginTransaction (null, null)
      action(txn)
      status = true
    } finally {
      if ( txn != null ) {
        if ( status )
          txn commit
        else
          txn abort
      }
    }
  }

}