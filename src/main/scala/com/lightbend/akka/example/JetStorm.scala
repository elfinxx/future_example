package com.lightbend.akka.example

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object JetStorm {

  def resolveSkill(stopWatch: StopWatch, intent: String)(implicit ec: ExecutionContext): Future[String] = {

    Future {

      val ranValue = Random.nextDouble() * 1000
      Thread.sleep(500)
      val s = s"resolve $ranValue on ${stopWatch.compare}"
      s
    }
  }
}
