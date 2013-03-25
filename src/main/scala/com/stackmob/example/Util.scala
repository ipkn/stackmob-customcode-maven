package com.stackmob.example

object Util {
	def computeLastResetTime(resetBegin:Long, resetLongerval:Long):Long = {
		val currentTimestamp: Long = System.currentTimeMillis
			currentTimestamp - (currentTimestamp - resetBegin) % resetLongerval
	}
	def getCurrentScore(client:Map[String,Any], resetTime:Long):Long = {
		if (client.get("scoredate").getValue - resetTime < 0)
			0
		else
			client.get("score").getValue
	}
}
