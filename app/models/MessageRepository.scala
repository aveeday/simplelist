package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for message.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class MessageRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of people
   */
  private class MessageTable(tag: Tag) extends Table[Message](tag, "message") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The text column */
    def text = column[String]("text")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Message object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Message case classes
     * apply and unapply methods.
     */
    def * = (id, text) <> ((Message.apply _).tupled, Message.unapply)
  }

  /**
   * The starting point for all queries on the message table.
   */
  private val message = TableQuery[MessageTable]

  /**
   * Create a message with the given text.
   *
   * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
   * id for that message.
   */
  def create(text: String): Future[Message] = db.run {
    // We create a projection of just the name column, since we're not inserting a value for the id column
    (message.map(p => (p.text))
      // Now define it to return the id, because we want to know what id was generated for the message
      returning message.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((text, id) => Message(id, text))
    // And finally, insert the person into the database
    ) += (text)
  }

  /**
   * List all the people in the database.
   */
  def list(): Future[Seq[Message]] = db.run {
    message.result
  }
}
