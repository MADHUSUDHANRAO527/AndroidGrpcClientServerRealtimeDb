package myproto

interface TriggerResponse {
    fun success(response: String)
    fun error()
}