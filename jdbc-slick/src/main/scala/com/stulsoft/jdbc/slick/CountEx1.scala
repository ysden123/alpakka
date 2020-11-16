/*
 * Copyright (c) 2020. StulSoft
 */

package com.stulsoft.jdbc.slick

import akka.Done
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.Sink
import com.typesafe.scalalogging.StrictLogging
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** MySQL server
 *
 * @author Yuriy Stul
 */
object CountEx1 extends App with StrictLogging {
  implicit val system = ActorSystem()
  implicit val mat = Materializer.createMaterializer(system)

  implicit val session: SlickSession = SlickSession.forConfig("slick-mysql")

  import session.profile.api._

  case class TheCount(count: Long)

  logger.info("Started")
  val done: Future[Done] =
    Slick.source(sql"SELECT count(*) FROM EtlJavaSolutionLog".as[TheCount](GetResult(r => TheCount(r.nextLong()))))
      .runWith(Sink.foreach(t => logger.info("{}", t)))
  done.onComplete(_ => system.terminate())

  system.registerOnTermination(() => session.close())

}
