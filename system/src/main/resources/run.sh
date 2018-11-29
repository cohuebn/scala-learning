echo "Starting Kafka"
"${BASH_SOURCE%/*}/kafka/bin/zookeeper-server-start.sh" "${BASH_SOURCE%/*}/kafka/config/zookeeper.properties" &
sleep 5
"${BASH_SOURCE%/*}/kafka/bin/kafka-server-start.sh" "${BASH_SOURCE%/*}/kafka/config/server.properties" &
sleep 25

read -p "Press any key to end shutdown Kafka"
"${BASH_SOURCE%/*}/kafka/bin/kafka-server-stop.sh"
sleep 5
"${BASH_SOURCE%/*}/kafka/bin/zookeeper-server-stop.sh"