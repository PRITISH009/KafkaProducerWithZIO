Step 1 - Initialize Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

Step 2 - Initialize Kafka Server
bin/kafka-server-start.sh config/server.properties

Step 3 - Create Topic
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 5 --topic testing-topic-1

Step 4 - Initialize Consumer
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic testing-topic

Step 5 - Initialize Producer (Not Required, you can check if consumer is consuming using Producer)
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic testing-topic

Step 6 - Run Application with Sample Params -

brokerList=localhost:9092
topic=test-topic
message=Hello
key=1

You can Uncomment Code in Producer Settings and pass those as env variables/program args as well.