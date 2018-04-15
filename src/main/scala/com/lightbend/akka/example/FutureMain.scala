package com.lightbend.akka.example

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Random

object FutureMain extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  val sw = new StopWatch

  def grimlockJob: Future[String] = Future {
    val thread = Thread.currentThread.getName
    println(s"[BotMeta $thread] Bot meta on ${sw.compare}")
    Thread.sleep(500)
    println(s"[BotMeta $thread] Bot meta Done on ${sw.compare}")
    "bot"
  }

  def benderJob(bot: String): Future[String] = Future {
    val thread = Thread.currentThread.getName
    println(s"[Bender $thread] Intent on ${sw.compare}")
    Thread.sleep(1500)
    println(s"[Bender $thread] Intent done on ${sw.compare}")
    "intent"
  }

  def resolveSkill(intent: String, candidate: Int): Future[String] = Future {
      val thread = Thread.currentThread.getName
      println(s"[JetStorm $thread] action$candidate start on ${sw.compare}")
      val ranValue = Random.nextDouble() * 1000
      Thread.sleep(500)
      val s = s"[JetStorm $thread] action$candidate resolve $ranValue on ${sw.compare}"
      println(s)
      s
    }



  val result = for {
    meta <- grimlockJob
    intent <- benderJob(meta)
    action1 <- resolveSkill(intent, 1)
    action2 <- resolveSkill(intent, 2)

  } yield {
    (meta, intent, action1, action2)
  }


  Await.result(result, 15000 millis)

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



