package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.data._
import play.api.data.Forms._

case class UserData(email: String, password: String)

object Application extends Controller with Secured {

  val userForm = Form(
    mapping(
      "email" -> text,
      "password" -> text
    )(UserData.apply)(UserData.unapply)
  )

  val createUserForm = Form(
    mapping(
      "email" -> text,
      "name" -> text,
      "password" -> text,
      "isTransporter" -> boolean,
      "isShipper" -> boolean,
      "description" -> text,
      "address" -> text
    )
        ((email, name, password, isTransporter, isShipper, description, address) => User(
          email = email,
          name = name,
          password = password,
          isTransporter = isTransporter,
          isShipper = isShipper,
          description = description,
          address = address))
        ((user: User) => Some(user.email, user.name, user.password, user.isTransporter, user.isShipper, user.description, user.address))


    )


  def login = Action { implicit request =>
    Ok(views.html.index(userForm, createUserForm))
  }

  def createAccount = Action { implicit request =>
    // Create the account
    createUserForm.bindFromRequest.fold(
      // Form has errors, redisplay it
      errors => Redirect(routes.Application.index),
      user => {
        User.insert(user)
        if (user.isShipper) {
          Redirect(routes.Application.shipperDashboard).withSession("email" -> user.email)
        } else {
          Redirect(routes.Application.transporterDashboard).withSession("email" -> user.email)
        }
      })
  }

  def index =  IsAuthenticated {  username => implicit request =>
    val user = User.findByEmail(username)
    user match {
      case Some(user) => {
          if (user.isShipper) {
            Redirect(routes.Application.shipperDashboard).withSession("email" -> user.email)
          } else {
            Redirect(routes.Application.transporterDashboard).withSession("email" -> user.email)
          }
      }
      case _ => Redirect(routes.Application.login)
    }
  }

  def transporterDashboard = IsAuthenticated {  username => implicit request =>
    val user = User.findByEmail(username)
    //val shipment = Shipment.findByOwnerEmail(username)
    Ok(views.html.transporters.dashboard(user.get))
  }

  def shipperDashboard = IsAuthenticated {  username => implicit request =>
    val user = User.findByEmail(username)
    val shipment = Shipment.findByOwnerEmail(username)
    Ok(views.html.shippers.dashboard(user.get))
  }


  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    // Check if we are a shipper
    val userData = userForm.bindFromRequest.get
    val user = User.findByEmail(userData.email)
    user match {
      case Some(user) => {
        if (user.password == userData.password) {
          if (user.isShipper) {
            Redirect(routes.Application.shipperDashboard).withSession("email" -> user.email)
          } else {
            Redirect(routes.Application.transporterDashboard).withSession("email" -> user.email)
          }
        } else {
          Redirect(routes.Application.login)
        }
      }
      case _ => Redirect(routes.Application.login)
    }

  }

 /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.index).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

}



/**
 * Provide security features
 */
trait Secured {

  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = {
    Results.Redirect(routes.Application.login).withSession("originalUrl" -> request.uri)
  }

  // --

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }


}
