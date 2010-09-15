package com.miriamlaurel.billgate.security.session.dao

import com.miriamlaurel.billgate.model.Client
import com.miriamlaurel.billgate.security.session.Session

trait SessionDAO {

  def createSession(client: Client) : Session;

  def invalidateSession(session: Session) : Boolean ;

  def loadClient(session : Session) : Client;

}