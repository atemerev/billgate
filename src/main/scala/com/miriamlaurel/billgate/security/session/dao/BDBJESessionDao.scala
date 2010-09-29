package com.miriamlaurel.billgate.security.session.dao

import bindings.JEBindingFactory
import com.miriamlaurel.billgate.security.session.Session
import java.io.File
import com.sleepycat.je._
import com.sleepycat.bind.tuple.TupleBinding
import com.miriamlaurel.billgate.security.session.entity.ClientAccessTimeWrapper
import java.util.{Date, UUID}
class BDBJESessionDao( envP : Environment, sessionDBP : Database, purgeThresholdSec : Int = 15 ) extends SessionDAO
        with BDBJETxnTrait with Purgeable {

  private val env : Environment = envP
  
  private val sessionDb : Database = sessionDBP

  private var purgeThresholdSeconds : Int = 15;

  override def loadClient( session : Session ) : String = {
    var client : String = null
    withTransactionDo(env) {
      txn => {
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
            client = clientWrapper getClientLogin
          case _ => client = null
        }
        cursor close
      }
    }
    client
  }

  override  def invalidateSession( session : Session ) : Boolean = {
    var removed = false
    withTransactionDo(env) {
      txn => {
        val sessionBinding : TupleBinding[Session] = JEBindingFactory getSessionBinding
        val sessionEntry = new DatabaseEntry
        sessionBinding objectToEntry (session, sessionEntry)
        removed = (sessionDb delete (txn, sessionEntry)) == OperationStatus.SUCCESS
      }
    }
    removed
  }

  override  def createSession( clientLogin : String ) : Session = {
    val session = new Session(UUID.randomUUID.toString)
    val sessionBinding = JEBindingFactory.getSessionBinding
    val clientBinding = JEBindingFactory.getClientBinding
    val sessionEntry = new DatabaseEntry
    val clientEntry = new DatabaseEntry
    sessionBinding objectToEntry (session, sessionEntry)
    clientBinding objectToEntry (new ClientAccessTimeWrapper(clientLogin, new Date), clientEntry)
    withTransactionDo(env) {
      txn => {
        val status = sessionDb putNoOverwrite (txn, sessionEntry, clientEntry)
      }
    }
    session
  }

  def purge() = {
    val purgeThreshold = purgeThresholdSec * 1000
    withTransactionDo(env) {
      txn => {
        val clientBinding = JEBindingFactory.getClientBinding
        val ccfg : CursorConfig = new CursorConfig
        ccfg setReadCommitted true
        val clientEntry = new DatabaseEntry
        val sessionEntry = new DatabaseEntry
        val cursor = sessionDb openCursor (txn, ccfg)
        val currentDateTime = System.currentTimeMillis
        while ( cursor.getNext(sessionEntry, clientEntry, LockMode.RMW ) == OperationStatus.SUCCESS) {
          val client : ClientAccessTimeWrapper = clientBinding entryToObject clientEntry
          val clientAccessTime = client.getAccessDate.getTime
          if ( currentDateTime - clientAccessTime > purgeThreshold ) {
            cursor delete
          }
        }
        cursor close
      }
    }
  }

}

object BDBJESessionDao {

  val dbName = "SESSIONDB"

  def getSessionStorage( folder : String, purgeThreshold : Int = 15 ) = {

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

    new BDBJESessionDao(env, sessionDb, purgeThreshold)
  }

}