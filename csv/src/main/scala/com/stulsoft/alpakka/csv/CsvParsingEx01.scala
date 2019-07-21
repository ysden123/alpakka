/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.alpakka.csv

import java.util.concurrent.CountDownLatch

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.csv.scaladsl.CsvParsing
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor
import scala.util.Success

/** CSV file parsing with Alpakka CSV parser
  *
  * @author Yuriy Stul
  */
object CsvParsingEx01 extends App with LazyLogging {
  implicit val system: ActorSystem = ActorSystem.create("ScalaSourceRange")
  implicit val materializer: ActorMaterializer = ActorMaterializer.create(system)
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  parse1()
  parse2()
  parse3()

  system.terminate()

  def parse1(): Unit = {
    logger.info("==>parse1")

    val countDownLatch = new CountDownLatch(1)

    val result = Source.single(ByteString("one,two,three\n"))
      .via(CsvParsing.lineScanner())
      .map(_.map(_.utf8String))
      .runForeach(list => logger.info("{}", list))

    result.onComplete(_ => countDownLatch.countDown())

    countDownLatch.await()

    logger.info("<==parse1")
  }

  def parse2(): Unit = {
    logger.info("==>parse2")

    val countDownLatch = new CountDownLatch(1)

    val result = Source.single(ByteString(
      """one,two,three
        |one 22,two 22,three 22""".stripMargin))
      .via(CsvParsing.lineScanner())
      .map(_.map(_.utf8String))
      .runForeach(list => logger.info("{}", list))

    result.onComplete(_ => countDownLatch.countDown())

    countDownLatch.await()

    logger.info("<==parse2")
  }

  def parse3(): Unit = {
    logger.info("==>parse3")

    val countDownLatch = new CountDownLatch(1)

    val result = Source.single(ByteString(
      """one,two,three
        |one 22,two 22,three 22""".stripMargin))
      .via(CsvParsing.lineScanner())
      .map(_.map(_.utf8String))
      .runWith(Sink.seq)

    result.onComplete(r => {
      logger.info("Records:")

      r match {
        case Success(records: Seq[Seq[String]]) =>
          (for (
            record <- records
          ) yield record).foreach(record => logger.info("{}", record))
        case _ =>
      }

      countDownLatch.countDown()
    })

    countDownLatch.await()

    //    logger.info("{}", result)

    logger.info("<==parse3")
  }
}
