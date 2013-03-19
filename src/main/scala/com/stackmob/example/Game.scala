package com.stackmob.example

import com.stackmob.core.MethodVerb
import com.stackmob.core.customcode.CustomCodeMethod
import com.stackmob.sdkapi.SDKServiceProvider
import com.stackmob.core.rest.{ResponseToProcess, ProcessedAPIRequest}
import java.util.{List => JList}
import java.net.HttpURLConnection
import scala.collection.JavaConverters._

class CustomCodeGame extends CustomCodeMethod {

  override def getMethodName: String = "cc_game"

  override def getParams: JList[String] = List[String]("game_id", "reset_begin", "reset_interval", "password").asJava

  override def execute(request: ProcessedAPIRequest, serviceProvider: SDKServiceProvider): ResponseToProcess = {
	  val verb = request.getVerb 
	  verb match {
		  case MethodVerb.GET =>
			  new ResponseToProcess(HttpURLConnection.HTTP_OK, Map("msg" -> "Hello, world! GET").asJava)
		  case _ =>
				  new ResponseToProcess(HttpURLConnection.HTTP_OK, Map("msg" -> "Hello, world! DEFAULT").asJava)
	  }
  }

}
