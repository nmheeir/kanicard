package com.nmheir.kanicard.core.presentation.utils

import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor

private typealias Attribute = String
private typealias Value = String

inline fun <reified T : Any> extractMembers(instance: T): List<Pair<Attribute, Value>> {
    val members = mutableListOf<Pair<Attribute, Value>>()

    // Get the primary constructor of the data class
    val constructor = T::class.primaryConstructor

    // Get the parameter names in the constructor declaration order
    val parameterNames = constructor?.parameters?.map { it.name } ?: emptyList()

    // Get all properties of the class
    val properties = T::class.members.filterIsInstance<KProperty1<T, *>>()

    // Filter and sort properties based on constructor parameter order
    val sortedProperties = properties.filter { it.name in parameterNames }
        .sortedBy { parameterNames.indexOf(it.name) }

    sortedProperties.forEach { member ->
        val name = member.name
        val value = member.get(instance)?.toString() ?: "---"
        members.add(name to value)
    }

    return members
}