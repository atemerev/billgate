package com.miriamlaurel.billgate.test.security.session.dao.bdbje

import org.scalatest.FunSuite
import java.io.File
import com.sleepycat.je.{DatabaseConfig, EnvironmentConfig, Database, Environment}
import com.miriamlaurel.billgate.security.session.dao.BDBJESessionDao
import com.miriamlaurel.billgate.model.Client

class TestBDBJESessionStorage extends FunSuite {

  private def setupDatabase() : (Environment,Database) = {
    val tmpDir = new File("target/test.bdbjestorage");
    tmpDir mkdirs
    val envCfg = new EnvironmentConfig()
    envCfg setTransactional true
    envCfg setAllowCreate true
    val env = new Environment(tmpDir, envCfg)
    env truncateDatabase (null,"test",false)
    val dbCfg = new DatabaseConfig
    dbCfg setAllowCreate true
    dbCfg setTransactional true
    val testDb = env openDatabase (null, "test", dbCfg)
    ( env, testDb )
  }

  test("Session management") {
    val (env, db) = setupDatabase
    val storage = new BDBJESessionDao(env,db)
    val session = storage createSession("jdevelop")
    assert(session != null)
    val clientLogin = storage loadClient session
    assert( "jdevelop" == clientLogin, "Loaded client login " + clientLogin )
    storage invalidateSession session
    val absentLogin = storage loadClient session
    assert( null == absentLogin, "Client login still exists" + absentLogin )
    println("Completed without errors")
  }

}
