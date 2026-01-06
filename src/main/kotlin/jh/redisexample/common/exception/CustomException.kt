package jh.redisexample.common.exception

class CustomException : RuntimeException {
    private val `interface`: Interface

    constructor(i: Interface) : super(i.getMessage()) {
        this.`interface` = i
    }

    constructor(i: Interface, message: String?) : super(i.getMessage() + message) {
        this.`interface` = i
    }
}
