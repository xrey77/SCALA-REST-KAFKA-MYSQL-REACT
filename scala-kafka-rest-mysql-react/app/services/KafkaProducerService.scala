// app/services/KafkaProducerService.scala
package services

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties
import scala.concurrent.{Future, Promise}

class KafkaProducerService {

  private val props = new Properties()
  // Replace with your actual Confluent Broker / Cloud endpoints
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") 
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  
  props.put(ProducerConfig.ACKS_CONFIG, "all") // Options: "0", "1", or "all"
  props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true") 
  props.put(ProducerConfig.RETRIES_CONFIG, "3")
  props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "60000") 

  // Confluent Cloud Security Configuration (Uncomment if using Confluent Cloud)
  /*
  props.put("security.protocol", "SASL_SSL")
  props.put("sasl.mechanism", "PLAIN")
  props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username='<API_KEY>' password='<API_SECRET>';")
  */

  private val producer = new KafkaProducer[String, String](props)
  private val centralTopic = "central-topic"

  def sendToCentralTopic(key: String, value: String): Future[RecordMetadata] = {
    val promise = Promise[RecordMetadata]()
    val record = new ProducerRecord[String, String](centralTopic, key, value)

    // Send asynchronously and map the callback to a Scala Future
    producer.send(record, new org.apache.kafka.clients.producer.Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        if (exception != null) promise.failure(exception)
        else promise.success(metadata)
      }
    })

    promise.future
  }

  def close(): Unit = producer.close() // Invoke during server shutdown
}
