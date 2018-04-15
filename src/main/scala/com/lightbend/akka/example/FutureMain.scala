package com.lightbend.akka.example

import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object FutureMain extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  val sw = new StopWatch

  val grimlockJob = Future {
    println(s"[BotMeta] Bot meta on ${sw.compare}")
    Thread.sleep(500)
    println(s"[BotMeta] Bot meta Done on ${sw.compare}")
    "bot"
  }

  val benderJob = Future {
    println(s"[Bender] Intent on ${sw.compare}")
    Thread.sleep(1500)
    println(s"[Bender] Intent done on ${sw.compare}")
    "intent"
  }

  val jetstormJob = Future {
    println(s"[JetStorm] Action on ${sw.compare}")
    Thread.sleep(2000)
    println(s"[JetStorm] Action done on ${sw.compare}")
    "action"
  }

  val result = for {
    meta: String <- grimlockJob
    intent <- benderJob
    action <- jetstormJob

  } yield {
    println(meta)
    println(intent)
    println(action)
  }

  Await.result(result, 5000 millis)
}


class StopWatch {

  private val initTime = System.currentTimeMillis()
  private var checkTime: Long = 0

  def compare: Long = System.currentTimeMillis() - initTime

  def elapse = {
    if (checkTime != 0) {
      checkTime = System.currentTimeMillis() - checkTime
      checkTime
    } else {
      checkTime = System.currentTimeMillis() - initTime
      checkTime
    }
  }
}
