package com.lightbend.akka.example

import java.util.UUID

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Random

object FutureMain extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  type SkillResult = String

  val sw = new StopWatch

  log("Start")
  val bot = getBot
  val intent = getIntent(bot)
  val skillResult = resolveSkill(intent.skillUrl)
  val c = Conversation(
    UUID.randomUUID().toString,
    bot = Some(bot),
    intent = Some(intent),
    skillResult = Some(skillResult)
  )
  println(c)
  log("End")


  def getBot: Bot = {
    log(s"[Grimlock] Bot meta on")
    Thread.sleep(500)
    log(s"[Grimlock] Bot meta Done on")
    Bot(UUID.randomUUID().toString, "aBot")
  }

  def getIntent(bot: Bot): Intent = {
    log(s"[Bender] Intent on")
    Thread.sleep(1000)
    log(s"[Bender] Intent done on")
    Intent.getSomeIntent
  }

  def resolveSkill(skillUrl: String): SkillResult = {
    log(s"[JetStorm] action start on")
    val ranValue = Random.nextDouble() * 1000
    Thread.sleep(1500)
    val s = s"[JetStorm] action resolve ${ranValue.day} on"
    log(s)
    s
  }

  def log(s: String): Unit = println(s"$s on ${sw.compare} (${Thread.currentThread.getName})")

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
      case _ =>
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


