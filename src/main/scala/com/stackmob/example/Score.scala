package com.stackmob.example

import com.stackmob.sdkapi.{SMCondition, SMEquals, SMString, SMInt}
import com.stackmob.core.MethodVerb
import com.stackmob.core.customcode.CustomCodeMethod
import com.stackmob.sdkapi.SDKServiceProvider
import com.stackmob.core.rest.{ResponseToProcess, ProcessedAPIRequest}
import java.util.{List => JList}
import java.net.HttpURLConnection
import scala.collection.JavaConverters._
import util.parsing.json.JSON

class ScoreUpdate extends CustomCodeMethod {

	override def getMethodName: String = "score_update"

		override def getParams: JList[String] = List[String]("client_id", "game_id", "score", "password").asJava

		def internalError = 
				new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, Map().asJava)

		def process(request: ProcessedAPIRequest, serviceProvider: SDKServiceProvider, clientId:String, gameId:String, score:Int, password:String):ResponseToProcess = {
			if (request.getVerb != MethodVerb.PUT)
				return internalError
			val ds = serviceProvider.getDataService

			val gameQuery = List[SMCondition](new SMEquals("game_id", new SMString(gameId))).asJava
			val gameResults = ds.readObjects("game", gameQuery)
			if (gameResults.size == 0)
				return internalError
			val gameResult = gameResults.get(0).getValue.asScala

			val resetTime = Util.computeLastResetTime(gameResult("reset_begin").asInstanceOf[SMInt].getValue,gameResult("reset_interval").asInstanceOf[SMInt].getValue)

			val clientQuery = List[SMCondition](new SMEquals("client_id", new SMString(clientId))).asJava
			val clientResults = ds.readObjects("client", clientQuery)
			if (clientResults.size == 0)
				return internalError
			val clientResult = clientResults.get(0).getValue.asScala
			if (clientResult.getOrElse("password", new SMString("")).getValue != password)
				return internalError

			val currentScore = Util.getCurrentScore(client, resetTime)
			if (currentScore < score)
			{
				

				new ResponseToProcess(HttpURLConnection.HTTP_OK, Map("client_id"->clientId, "game_id"->gameId, "score"->score).asJava)
			}
			else
				new ResponseToProcess(HttpURLConnection.HTTP_OK, Map("client_id"->clientId, "game_id"->gameId, "score"->currentScore).asJava)
		}

		override def execute(request: ProcessedAPIRequest, serviceProvider: SDKServiceProvider): ResponseToProcess = {
			if (request.getBody.isEmpty)
				return internalError
			JSON.parseFull(request.getBody).map(json => {
				json match {
					case m : Map[String, Any] =>
						process(request, serviceProvider, m("client_id").asInstanceOf[String],m("game_id").asInstanceOf[String], m("score").asInstanceOf[Int], m.getOrElse("password","").asInstanceOf[String])
					case _ =>
						internalError
				}
			}) getOrElse 
				internalError
		}

}
