/*
 * Copyright (c) 2020. StulSoft
 */

package com.stulsoft.jdbc.slick

import akka.Done
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl._
import akka.stream.scaladsl._
import slick.jdbc.{GetResult, GetTupleResult, PositionedResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * @author Yuriy Stul
 */
object PlayersEx2 extends App {
  implicit val system = ActorSystem()
  implicit val mat = Materializer.createMaterializer(system)

  implicit val session: SlickSession = SlickSession.forConfig("slick-sqlserver")

  import session.profile.api._

  case class Player(targetAccountId: Int, sourceAccountId: Int, etlVersion: String)

  val sql = sql"SELECT TOP 10 Target_Account_Id,Source_Account_Id,ETL_Version from webpals_dwh.Admin.Player_Data_Etl_Management"

  def transform(result:PositionedResult): Player ={
    Player(result.nextInt(),result.nextInt(),result.nextString())
  }

  val done: Future[Done] =
    Slick
      .source(sql.as[Player](GetResult(transform)))
      .filter(p => 33406 != p.sourceAccountId)
      .runWith(Sink.foreach(p => println(p)))

  done.onComplete(_ => system.terminate())

  system.registerOnTermination(() => session.close())
}
