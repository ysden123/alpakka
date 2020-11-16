/*
 * Copyright (c) 2020. StulSoft
 */

package com.stulsoft.jdbc.slick

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.Sink
import com.typesafe.scalalogging.StrictLogging
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

/** MySQL server
 *
 * @author Yuriy Stul
 */
object CountEx2 extends App with StrictLogging {
  implicit val system = ActorSystem()
  implicit val mat = Materializer.createMaterializer(system)

  implicit val session: SlickSession = SlickSession.forConfig("slick-mysql")

  import session.profile.api._

  case class TheCount(count: Long)

  system.registerOnTermination(() => session.close())

  logger.info("Started")
  val count = Slick.source(sql"SELECT count(*) FROM EtlJavaSolutionLog".as[TheCount](GetResult(r => TheCount(r.nextLong()))))
    .runWith(Sink.head)
  count.onComplete(r => {
    r match {
      case Success(result) => logger.info("{}", result)
      case _ =>
    }
    system.terminate()
  })
}
