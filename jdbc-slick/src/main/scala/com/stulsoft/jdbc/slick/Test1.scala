/*
 * Copyright (c) 2020. Webpals
 */

package com.stulsoft.jdbc.slick

import akka.Done
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl._
import akka.stream.scaladsl._
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** MySQL server
 *
 * @author Yuriy Stul
 */
object Test1 extends App {
  implicit val system = ActorSystem()
  implicit val mat = Materializer.createMaterializer(system)

  implicit val session: SlickSession = SlickSession.forConfig("slick-mysql")

  import session.profile.api._

  case class T22(id: Int)

  val done: Future[Done] =
    Slick.source(sql"SELECT accountId FROM EtlJavaSolutionLog limit 10".as[T22](GetResult(r => T22(r.nextInt))))
      .runWith(Sink.foreach(t => println(t)))
  done.onComplete(_ => system.terminate())

  system.registerOnTermination(() => session.close())
}
