package models


import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import java.util
import util.{Date, Calendar}
import java.math.BigDecimal
import scala.language.postfixOps
import com.github.nscala_time.time.Imports._
import org.joda.time._


case class User(id: Pk[Long] = NotAssigned, email: String, name: String, password: String, isTransporter: Boolean, isShipper: Boolean, description: String, address: String)


case class Shipment(id: Pk[Long] = NotAssigned, reference: String, fromAddress: String, toAddress: String, volume: Double, weight: Double, pieces: Integer,
  booked: Boolean, expirationDate: Date, pickupTime: Option[Date], ownerId: Long, transporterId: Option[Long])



object User {


  val simple = {
    get[Pk[Long]]("user.id") ~
      get[String]("user.email") ~
      get[String]("user.name") ~
      get[String]("user.password") ~
      get[Boolean]("user.is_transporter") ~
      get[Boolean]("user.is_shipper") ~
      get[String]("user.description") ~
      get[String]("user.address")  map {
      case id~email~name~password~isTransporter~isShipper~description~address => User(id, email, name, password, isTransporter, isShipper, description, address)
    }
  }
  // -- Queries

  def findById(id: Long): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where id = {id}").on('id -> id).as(User.simple.singleOpt)
    }
  }

  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on('email -> email).as(User.simple.singleOpt)
    }
  }

  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user order by name").as(User.simple *)
    }
  }

  def insert(user: User) = {
    DB.withConnection { implicit connection =>
      SQL("""
        insert into user(email, name, password, is_transporter, is_shipper, description, address) values
        ({email}, {name}, {password}, {isTransporter}, {isShipper}, {description}, {address});
        """).on(
        'email -> user.email, 'name -> user.name, 'password -> user.password, 'isTransporter -> user.isTransporter, 'isShipper -> user.isShipper,
        'description -> user.description, 'address -> user.address
      ).executeUpdate()
    }
  }

}


object Shipment {

   // Mysql specific
   implicit def bigDecimalToDouble: Column[Double] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case d: BigDecimal => Right(d.doubleValue)
      case d: Double => Right(d)
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to Double for column " + qualified))
    }
  }

  implicit val doubleTimeToStatement = new ToStatement[Double] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: Double): Unit = {
      s.setDouble(index, aValue)
    }
  }

  val simple = {
    get[Pk[Long]]("shipment.id") ~
      get[String]("shipment.reference") ~
      get[String]("shipment.from_address") ~
      get[String]("shipment.to_address") ~
      get[Double]("shipment.volume") ~
      get[Double]("shipment.weight") ~
      get[Int]("shipment.pieces") ~
      get[Boolean]("shipment.booked") ~
      get[Date]("shipment.expiration_date") ~
      get[Option[Date]]("shipment.pickup_time") ~
      get[Long]("shipment.owner_id") ~
      get[Option[Long]]("shipment.transporter_id") map {
      case id~reference~fromAddress~toAddress~volume~weight~pieces~booked~expirationDate~pickupTime~ownerId~transporterId => Shipment(id, reference, fromAddress, toAddress, volume, weight, pieces, booked, expirationDate, pickupTime, ownerId, transporterId)
    }
  }

 def findByOwner(owner: Long): Seq[Shipment] = {
    DB.withConnection { implicit connection => {
      SQL(
        """
          select * from shipment
          where shipment.owner_id = {owner}
          order by expiration_date
        """
      ).on(
        'employee -> owner
      ).as(Shipment.simple *)
    }
    }
  }

  def findByOwnerEmail(email: String): Seq[Shipment] = {
    DB.withConnection { implicit connection => {
      SQL(
        """
          select * from user u, shipment s
          where s.owner_id = u.id and u.email = {email}
          order by s.expiration_date
        """
      ).on(
        'email -> email
      ).as(Shipment.simple *)
    }
    }
  }


  def insert(shipment: Shipment) = {
    DB.withConnection { implicit connection =>
      SQL("""
        insert into shipment(reference, from_address, to_address, volume, weight, pieces, booked, expiration_date, picked_time, owner_id, transporter_id) values
        ({id}, {reference}, {fromAddress}, {toAddress}, {volume}, {weight}, {pieces}, {booked}, {expirationDate}, {pickupTime}, {ownerId}, {transporterId});
        """).on(
        'reference -> shipment.reference, 'fromAddress -> shipment.fromAddress, 'toAddres -> shipment.toAddress, 'volume -> shipment.volume, 'weight -> shipment.weight,
        'booked -> shipment.booked, 'expirationDate -> shipment.expirationDate, 'pickupTime -> shipment.pickupTime,  'ownerId -> shipment.ownerId,  'transporterId -> shipment.transporterId
      ).executeUpdate()
    }
  }

}
