class RunnerClass {
    hidden [String] $rootPath

    RunnerClass ([String] $rootPath)
    {
        $this.rootPath = $rootPath
    }

    [string] ResolveKafkaScript([String] $path) {
        $kafkaBin = Join-Path $this.rootPath 'kafka/bin/windows/'
        $resolved = Join-Path $kafkaBin $path

        return $resolved
    }

    [string] ResolveKafkaConfig([String] $path) {
        $kafkaConfig = Join-Path $this.rootPath 'kafka/config/'
        return Join-Path $kafkaConfig $path
    }

    [void] Run([String]$scriptName, [String] $configName, [Boolean]$wait) {
        $scriptPath = $this.ResolveKafkaScript($scriptName)
        $arguments = @()
        if ($configName) {
            $configPath = $this.ResolveKafkaConfig($configName)
            Start-Process -FilePath $scriptPath -ArgumentList @($configPath) -NoNewWindow -Wait:$wait
        }
        else {
            Start-Process -FilePath $scriptPath -NoNewWindow -Wait:$wait
        }
    }

    [void] StartKafka() {
        $this.Run('zookeeper-server-start.bat', 'zookeeper.properties', $false)
        Start-Sleep -Seconds 10
        $this.Run('kafka-server-start.bat', 'server.properties', $false)
        Start-Sleep -Seconds 10
    }

    [void] StopKafka() {
        $this.Run('kafka-server-stop.bat', $null, $true)
        $this.Run('zookeeper-server-stop.bat', $null, $true)
    }
}

$scriptDir = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
[RunnerClass] $runner = [RunnerClass]::new($scriptDir)
$runner.StopKafka() # Ensure cleanup from previous runs
$runner.StartKafka()
$wait = Read-Host 'Press any key to exit'
$runner.StopKafka()