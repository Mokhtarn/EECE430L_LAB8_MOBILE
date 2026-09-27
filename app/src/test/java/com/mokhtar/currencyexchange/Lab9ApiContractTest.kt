package com.mokhtar.currencyexchange

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mokhtar.currencyexchange.api.Exchange
import com.mokhtar.currencyexchange.api.model.Token
import com.mokhtar.currencyexchange.api.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.http.POST

class Lab9ApiContractTest {
    private val gson = Gson()

    @Test
    fun userUsesLab9JsonFieldNames() {
        val user = User().apply {
            id = 7
            username = "student"
            password = "secret"
        }

        val json = gson.fromJson(gson.toJson(user), JsonObject::class.java)

        assertEquals(7, json.get("id").getAsInt())
        assertEquals("student", json.get("user_name").getAsString())
        assertEquals("secret", json.get("password").getAsString())
        assertFalse(json.has("username"))
    }

    @Test
    fun tokenReadsLab9ResponseField() {
        val token = gson.fromJson("{\"token\":\"abc123\"}", Token::class.java)

        assertEquals("abc123", token.token)
    }

    @Test
    fun userManagementUsesLab9Endpoints() {
        val addUser = Exchange::class.java.getMethod("addUser", User::class.java)
        val authenticate = Exchange::class.java.getMethod("authenticate", User::class.java)

        assertEquals("/user", addUser.getAnnotation(POST::class.java)?.value)
        assertEquals(
            "/authentication",
            authenticate.getAnnotation(POST::class.java)?.value
        )
        assertTrue(addUser.isAnnotationPresent(POST::class.java))
        assertTrue(authenticate.isAnnotationPresent(POST::class.java))
    }
}
