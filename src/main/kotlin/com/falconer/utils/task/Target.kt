package com.falconer.utils.task

data class TargetConfig(
    val hostname: String,
    val sslCertPath: String
) {
    override fun toString():String {
        return "TargetConfig($hostname, $sslCertPath)"
    }
}

data class Target(
    val computerName: String,
    val targetConfig: TargetConfig
) {
    override fun toString():String {
        return "Target($computerName, $targetConfig)"
    }
    companion object {
        val LOCAL_HOST = Target("localhost", TargetConfig("localhost", ""))
    }
}

