package com.suadahaji.githubkmp.mobile

expect fun platformName(): String

class Greeting() {
   fun greeting(): String = "Kotlin Rocks on ${platformName()}"
}