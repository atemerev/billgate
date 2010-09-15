package com.miriamlaurel.billgate.security.session.dao

import bindings.JEBindingFactory
import com.miriamlaurel.billgate.model.Client
import com.miriamlaurel.billgate.security.session.Session
import java.io.File
import com.sleepycat.je._
import com.sleepycat.bind.tuple.{TupleOutput, TupleInput, TupleBinding}
import com.miriamlaurel.billgate.security.session.entity.ClientAccessTimeWrapper
import java.util.UUID

class BDBJESessionDao( envP : Environment, sessionDBP : Database ) extends SessionDAO with BDBJETxnTrait {

  private val env : Environment = envP
  
  private val sessionDb : Database = sessionDBP

  override def loadClient( session : Session ) : Client = {
    var client : Client = null
    withTransactionDo(env) {
      _ => def worker( txn : Transaction ) = {
        val ccfg = new CursorConfig
        ccfg setReadCommitted true
        val cursor = sessionDb openCursor (txn, ccfg)
        val sessionEntry = new DatabaseEntry
        val sessionBinding = JEBindingFactory.getSessionBinding
        sessionBinding objectToEntry (session, sessionEntry)
        val clientEntry = new DatabaseEntry
        val clientBinding = JEBindingFactory.getClientBinding
        cursor getSearchKey ( sessionEntry, clientEntry, LockMode.RMW ) match {
          case OperationStatus.SUCCESS =>
            val clientWrapper : ClientAccessTimeWrapper = clientBinding entryToObject clientEntry
            client = clientWrapper getClient
          case _ => client = null
        }
      }
    }
    client
  }

  override  def invalidateSession( session : Session ) : Boolean = {
    //TODO remove from database
    var removed = false
    withTransactionDo(env) {
      _ => def worker( txn : Transaction ) = {
        val sessionBinding : TupleBinding[Session] = JEBindingFactory getSessionBinding
        val sessionEntry = new DatabaseEntry
        sessionBinding objectToEntry (session, sessionEntry)
        removed = (sessionDb delete (txn, sessionEntry)) == OperationStatus.SUCCESS
      }
    }
    removed
  }

  override  def createSession( client : Client ) : Session = {
    new Session(UUID.randomUUID.toString)
  }

}

object BDBJESessionDao {

  val dbName = "SESSIONDB"

  def getSessionStorage( folder : String ) = {

    val envCfg : EnvironmentConfig = new EnvironmentConfig
    envCfg setAllowCreate true
    envCfg setCachePercent 20
    envCfg setReadOnly false
    envCfg setDurability Durability.COMMIT_WRITE_NO_SYNC

    val rootDir = new File(folder)
    val env = new Environment(rootDir,envCfg)

    val sessionDbCfg = new DatabaseConfig()
    sessionDbCfg setAllowCreate true
    sessionDbCfg setTransactional true
    val sessionDb = env openDatabase (null, dbName, sessionDbCfg)

    new BDBJESessionDao(env, sessionDb)
  }

}