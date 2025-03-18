package com.example.chat_it.util

class SDKUninitializedException : IllegalStateException {

    constructor() : super("Please initialize the ChatIt SDK before using it")

    constructor(errorMessage: String) : super(errorMessage)

}

