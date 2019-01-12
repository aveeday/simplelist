package controllers

import models.Message
import play.api.data._
import javax.inject._
import play.api._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(repo: MessageRepository,
                                cc: MessagesControllerComponents
                              )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  import MessageForm._

  // The URL to the widget.  You can call this directly from the template, but it
  // can be more convenient to leave the template completely stateless i.e. all
  // of the "WidgetController" references are inside the .scala file.
  private val postUrl = routes.HomeController.createMessage()

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.index(form, postUrl))
  }

  def list() = Action.async { implicit request: Request[AnyContent] =>
    // Ok(views.html.list(repo.list()))
    repo.list().map { message =>
      Ok(views.html.list(message))
    }
  }

  /**
   * The add message action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on MessageRepository.
   */
  def createMessage = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    form.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm, postUrl)))
      },
      // There were no errors in the from, so create the person.
      message => {
        repo.create(message.text).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.HomeController.list).flashing("success" -> "message.created")
        }
      }
    )
  }

}
