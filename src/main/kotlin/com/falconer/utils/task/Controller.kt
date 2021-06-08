package com.falconer.utils.task

class Controller {
    val runners = mutableListOf<Runner>()
    val targets = mutableListOf<Target>()

    fun addRunner(runner: Runner) {
        runners.add(runner)
    }

    fun addTarget(target: Target) {
        for (t in targets) {
            if (t.targetConfig.equals(target.targetConfig)) {
                println("the requested new target already exists with config: ${target.targetConfig}")
            }
        }
        targets.add(target)
    }

    fun updateRunner(originalRunner: Runner?, updatedRunner: Runner) {
        // FIXME: have this update the UI as well as use persistent storage
        originalRunner?.let {
            // update the existing
            removeRunner(it)
            addRunner(updatedRunner)
        } ?: run{
            // creating a new Runner
            addRunner(updatedRunner)
        }
    }

    fun removeRunner(runner: Runner) {
        runners.remove(runner)
    }

    fun removeTarget(target: Target) {
        targets.remove(target)
    }
}