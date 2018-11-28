function Start-Kafka() {
    Start-Process -FilePath ./kafka/bin/windows/zookeeper-server-start.bat -ArgumentList @('./kafka/config/zookeeper.properties')
    Start-Sleep -Seconds 5
    Start-Process -FilePath ./kafka/bin/windows/kafka-server-start.bat -ArgumentList @('./kafka/config/server.properties')
}

function Stop-Kafka() {
    ./kafka/bin/windows/kafka-server-stop.bat
    Start-Sleep -Seconds 5
    ./kafka/bin/windows/zookeeper-server-stop.bat
}

# Ensure cleanup from previous runs
Stop-Kafka
Start-Kafka
$wait = Read-Host 'Press any key to exit'
Stop-Kafka