package models

import play.api.libs.json._

/**
 * Presentation object used for displaying data in a template.
 *
 * Note that it's a good practice to keep the presentation DTO,
 * which are used for reads, distinct from the form processing DTO,
 * which are used for writes.
 */
case class Message(id: Long, text: String)

object Message {
  implicit val messageFormat = Json.format[Message]
}
