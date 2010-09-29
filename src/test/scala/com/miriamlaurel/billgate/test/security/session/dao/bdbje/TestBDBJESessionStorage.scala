package com.miriamlaurel.billgate.test.security.session.dao.bdbje

import org.scalatest.FunSuite
import java.io.File
import com.sleepycat.je.{DatabaseConfig, EnvironmentConfig, Database, Environment}
import com.miriamlaurel.billgate.model.Client
import com.miriamlaurel.billgate.security.session.dao.{BDBJETxnTrait, BDBJESessionDao}

class TestBDBJESessionStorage extends FunSuite with BDBJETxnTrait {
  private def setupDatabase(): (Environment, Database) = {
    val tmpDir = new File("target/test.bdbjestorage");
    tmpDir mkdirs
    val envCfg = new EnvironmentConfig()
    envCfg setTransactional true
    envCfg setAllowCreate true
    val env = new Environment(tmpDir, envCfg)
    if ((env getDatabaseNames) contains "test")
      env truncateDatabase (null, "test", true)
    val dbCfg = new DatabaseConfig
    dbCfg setAllowCreate true
    dbCfg setTransactional true
    val testDb = env openDatabase (null, "test", dbCfg)
    (env, testDb)
  }

  test("Session management") {
    val (env, db) = setupDatabase
    val storage = new BDBJESessionDao(env, db)
    val session = storage createSession ("jdevelop")
    assert(session != null)
    val clientLogin = storage loadClient session
    assert("jdevelop" == clientLogin, "Loaded client login " + clientLogin)
    storage invalidateSession session
    val absentLogin = storage loadClient session
    assert(null == absentLogin, "Client login still exists" + absentLogin)
    closeEverything(env, db)
  }

  test("Purge validation") {
    val (env, db) = setupDatabase
    val storage = new BDBJESessionDao(env, db, 5)
    val session = storage createSession ("jdevelop")
    assert(session != null)
    val clientLogin = storage loadClient session
    assert("jdevelop" == clientLogin, "Loaded client login " + clientLogin)
    Thread.sleep(2000)
    storage.purge()
    val clientLoginAfterPurgeExists = storage loadClient session
    assert("jdevelop" == clientLoginAfterPurgeExists, "Loaded client login " + clientLoginAfterPurgeExists)
    Thread.sleep(7000)
    storage.purge()
    val clientLoginAfterPurge = storage loadClient session
    assert(clientLoginAfterPurge == null, "Client was not purged")
    closeEverything(env, db)
  }

  private def closeEverything(env: Environment, db: Database) {
    db.close()
    env.close
  }

}