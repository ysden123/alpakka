/*
 * Copyright (c) 2020. StulSoft
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

/**
 * @author Yuriy Stul
 */
object PlayersEx1 extends App {
  implicit val system = ActorSystem()
  implicit val mat = Materializer.createMaterializer(system)

  implicit val session: SlickSession = SlickSession.forConfig("slick-sqlserver")

  import session.profile.api._

  case class Player(targetAccountId: Int, sourceAccountId: Int, etlVersion: String)

  val done: Future[Done] =
    Slick
      .source(sql"SELECT TOP 10 Target_Account_Id,Source_Account_Id,ETL_Version from webpals_dwh.Admin.Player_Data_Etl_Management"
        .as[Player](GetResult(r => Player(r.nextInt(), r.nextInt(), r.nextString()))))
      .runWith(Sink.foreach(p => println(p)))

  done.onComplete(_ => system.terminate())

  system.registerOnTermination(() => session.close())
}
