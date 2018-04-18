package com.lightbend.akka.example

import java.util.UUID

import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Random, Success}

object FutureMain extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  type SkillResult = String

  val sw = new StopWatch
  log("Start")

  val getBot: Future[Option[Bot]] = Future {
    log(s"[Grimlock] Get bot meta on")
    Thread.sleep(500)
    log(s"[Grimlock] Get bot meta Done on")
    Some(Bot(uid, "aBot"))
  }

  val getIntent: Future[Option[Intent]] = Future {
    log(s"[Bender] Get intent on")
    Thread.sleep(1000)
    log(s"[Bender] Get intent done on")
    Some(Intent.getSomeIntent)
  }

  val resolveSkill: Future[Option[String]] = Future {
    log(s"[JetStorm] Do action start on")
    val ranValue = Random.nextDouble() * 1000
    Thread.sleep(1500)
    val s = s"[JetStorm] Do action done $ranValue on"
    log(s)
    Some(s)
  }

  val c = for {
    bot <- getBot
    intent <- getIntent
    skillResult <- resolveSkill
  } yield {
    Conversation(uid, bot, intent, skillResult)
  }

  log(c.toString)

  log("End")


  c.onComplete {
    case Success(Conversation(_, _, _, _)) =>
      log(c.toString)
    case Failure(e) =>
      log(e.getLocalizedMessage)
  }

  log(Await.result(c, 8000 millis).toString)


  def log(s: String): Unit = println(s"$s on ${sw.compare} (${Thread.currentThread.getName})")

  def uid: String = UUID.randomUUID().toString.split("-").head

  type Recovery[T] = PartialFunction[Throwable, T]

  def withNone[T]: Recovery[Option[T]] = { case e => None }

}

case class Bot(
  id: String,
  name: String
)

case class Intent(
  id: String,
  name: String,
  botId: String,
  response: String,
  skillUrl: String
)

object Intent {
  def getSomeIntent = {
    Intent(UUID.randomUUID().toString, "aaIntent", "botId", "response", "url")
  }
}

case class Conversation(
  id: String,
  bot: Option[Bot] = None,
  intent: Option[Intent] = None,
  skillResult: Option[String] = None
) {
  override def toString: String = {
    this match {
      case Conversation(_, None, _, _) =>
        s"No bot - $id"
      case Conversation(_, Some(_), None, _) =>
        s"Bot but no intent - $id"
      case Conversation(_, Some(_), Some(_), None) =>
        s"Bot and intent but no skill result - $id"
      case Conversation(_, Some(_), Some(_), Some(_)) =>
        s"${bot.get.name} - ${intent.get.name}"
    }
  }
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
