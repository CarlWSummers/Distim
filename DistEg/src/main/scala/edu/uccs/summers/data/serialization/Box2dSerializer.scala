package edu.uccs.summers.data.serialization

import akka.serialization.Serializer
import org.jbox2d.dynamics.Body
import org.jbox2d.serialization.pb.PbSerializer
import org.jbox2d.serialization.pb.PbDeserializer
import org.jbox2d.dynamics.World
import akka.util.ClassLoaderObjectInputStream
import akka.actor.ActorSystem
import java.io.ByteArrayInputStream
import akka.actor.ExtendedActorSystem

class Box2dSerializer(system : ExtendedActorSystem) extends Serializer {

  val serializer = new PbSerializer
  val deserializer = new PbDeserializer
  
  /**
   * Completely unique value to identify this implementation of Serializer, used to optimize network traffic
   * Values from 0 to 16 is reserved for Akka internal usage
   */
  def identifier: Int = 1939398124

  /**
   * Serializes the given object into an Array of Byte
   */
  def toBinary(o: AnyRef): Array[Byte] = {
    o match {
      case b : Body => {
        serializer.serializeBody(b).build().toByteArray()
      }
      case w : World => {
        serializer.serializeWorld(w).build().toByteArray()
      }
    }
  }

  /**
   * Returns whether this serializer needs a manifest in the fromBinary method
   */
  def includeManifest: Boolean = true

  /**
   * Produces an object from an array of bytes, with an optional type-hint;
   * the class should be loaded using ActorSystem.dynamicAccess.
   */
  def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val in = new ClassLoaderObjectInputStream(system.dynamicAccess.classLoader, new ByteArrayInputStream(bytes))
    val obj = manifest.get match {
      case q if q == classOf[World] => deserializer.deserializeWorld(in)
//      case q if q == classOf[Body] => deserializer.deserializeBody(in)
    }
    in.close()
    obj
  }
}