package com.miriamlaurel.billgate.security.session.entity

import com.miriamlaurel.billgate.model.Client
import java.util.Date

class ClientAccessTimeWrapper(loginParam : String, accessDateParam : Date) {

  private val clientLogin = loginParam

  private val accessDate = accessDateParam

  def getClientLogin = clientLogin 

  def getAccessDate = accessDate

}