package com.miriamlaurel.billgate.security.session.entity

import com.miriamlaurel.billgate.model.Client
import java.util.Date

class ClientAccessTimeWrapper(clientParam : Client, accessDateParam : Date) {

  private val client = clientParam

  private val accessDate = accessDateParam

  def getClient = client 

  def getAccessDate = accessDate

}