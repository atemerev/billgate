package com.miriamlaurel.billgate.security.session.dao.bindings

object JEBindingFactory {

  def getSessionBinding = new JESessionBinding

  def getClientBinding = new JEClientBinding
  
}