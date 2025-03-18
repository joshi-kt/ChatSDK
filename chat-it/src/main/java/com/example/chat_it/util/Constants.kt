package com.example.chat_it.util

import android.Manifest

const val DEFAULT_PROMPT = "Keep your answers crisp and to the point, don't elaborate it a lot until you are asked to elaborate it."
const val DEFAULT_REPLY_DELAY = 2000L
const val DEFAULT_IMAGE = -1
const val DEFAULT_BOT_NAME = "Omniscient Bot"
const val BASE_URL = "https://generativelanguage.googleapis.com"
const val UPLOAD = "/upload"
const val API_VERSION = "/v1beta"
const val GEMINI_MODEL = "/models/gemini-1.5-flash"
const val MAX_FILE_SIZE = 15000000

const val SEND_MESSAGE_ENDPOINT = ":generateContent"
const val CREATE_FILE_ENDPOINT = "/files"

const val API_KEY_PARAM = "key"
const val ROLE = "role"
const val USER = "user"
const val AI_MODEL = "model"
const val TEXT = "text"
const val PARTS = "parts"
const val CONTENTS = "contents"
const val FILE_DATA = "file_data"
const val SYSTEM_INSTRUCTION = "system_instruction"
const val DEFAULT_AUDIO_MESSAGE_OUTPUT_INSTRUCTION = "Whatever you understand from this audio describe it in detail"

const val SEND_TEXT_MESSAGE_TASK = "SEND_MESSAGE_TASK"
const val CREATE_AUDIO_MESSAGE_TASK = "SEND_MESSAGE_TASK"
const val SEND_AUDIO_MESSAGE_TASK = "SEND_MESSAGE_TASK"
const val CLOSE_CHAT_IT_TASK = "CLOSE_CHATIT_TASK"