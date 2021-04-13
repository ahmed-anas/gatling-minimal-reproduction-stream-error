/*
 * Copyright 2011-2018 GatlingCorp (https://gatling.io)
 *
 * All rights reserved.
 */

package frontline.sample

import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class BasicSimulation extends Simulation {
  private def requestsBody: String ="""
                                      |{
                                      |    "invocationSequenceNumber": 1,
                                      |    "tenantIdentifier": ${PROVIDER.jsonStringify()},
                                      |    "subscriberIdentifier": ${MSISDN.jsonStringify()},
                                      |    "multipleUnitUsage": {
                                      |        "requestedUnit": {
                                      |            "serviceSpecificUnits": 0.1
                                      |        },
                                      |        "usedUnitContainer": {
                                      |            "serviceSpecificUnits": 0.1
                                      |        },
                                      |        "ratingGroup": "voice"
                                      |    }
                                      |}
                                      |""".stripMargin

  val accountFeeder: BatchableFeederBuilder[String]#F = csv("data/accounts.csv").circular

  val httpProtocol = http
    .enableHttp2
    .baseUrl(s"https://alb-for-gatling-supp-147400401.us-east-1.elb.amazonaws.com/nchf-convergedcharging/v3/")
    .header("Content-Type", "application/json")
    .http2PriorKnowledge(Map("alb-for-gatling-supp-147400401.us-east-1.elb.amazonaws.com" -> true))
    .shareConnections


  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .feed(accountFeeder)
    .exec(http("Init Request")
      .post("/chargingData")
      .body(StringBody(requestsBody))
      .check(status.is(201))
    )

  setUp(scn.inject(
    constantConcurrentUsers(4000) during(300.seconds)
  ).protocols(httpProtocol)).maxDuration(315.seconds)
}
