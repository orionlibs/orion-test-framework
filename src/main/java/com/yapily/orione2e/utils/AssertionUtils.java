package com.yapily.orione2e.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssertionUtils
{
    public static void assertPublicInstanceFieldValuesContainExactly(Object obj, Collection<?> expectedValues)
    {
        assertPublicInstanceFieldValuesContainExactly(obj, expectedValues.toArray());
    }


    public static void assertPublicInstanceFieldValuesContainExactly(Object objectToCheck, Object... expectedValues)
    {
        if(objectToCheck == null)
        {
            throw new IllegalArgumentException("objectToCheck must not be null");
        }
        List<Object> actualValues = Arrays.stream(objectToCheck.getClass().getDeclaredFields())
                        .filter(f -> Modifier.isPublic(f.getModifiers()))
                        .filter(f -> !Modifier.isStatic(f.getModifiers()))
                        .map(f -> {
                            try
                            {
                                // public fields: no need for setAccessible(true), but keep try/catch for safety
                                return f.get(objectToCheck);
                            }
                            catch(IllegalAccessException e)
                            {
                                throw new RuntimeException("Unable to read field " + f.getName(), e);
                            }
                        })
                        .collect(Collectors.toList());
        // Asserts multiset equality (order independent). Works with null values too.
        assertThat(actualValues).containsExactlyInAnyOrder(expectedValues);
    }


    public static void assertPublicInstanceFieldsMatchByName(Object obj, Map<String, ?> expectedByName)
    {
        if(obj == null)
        {
            throw new IllegalArgumentException("obj must not be null");
        }
        if(expectedByName == null)
        {
            throw new IllegalArgumentException("expectedByName must not be null");
        }
        Field[] declared = obj.getClass().getDeclaredFields();
        // collect only public, non-static declared fields
        Map<String, Field> publicFields = Arrays.stream(declared)
                        .filter(f -> Modifier.isPublic(f.getModifiers()))
                        .filter(f -> !Modifier.isStatic(f.getModifiers()))
                        .collect(Collectors.toMap(Field::getName, f -> f));
        assertThat(publicFields.keySet()).as("public instance field names").containsExactlyInAnyOrderElementsOf(expectedByName.keySet());
        for(Map.Entry<String, ?> e : expectedByName.entrySet())
        {
            String name = e.getKey();
            Object expected = e.getValue();
            Field f = publicFields.get(name);
            try
            {
                Object actual = f.get(obj);
                assertThat(actual).as(() -> "field '" + name + "' value").isEqualTo(expected);
            }
            catch(IllegalAccessException ex)
            {
                throw new RuntimeException("Unable to read field " + name, ex);
            }
        }
    }


    public static void assertMapValuesContainExactly(Map<String, String> map, String... expectedValues)
    {
        if(map == null)
        {
            throw new IllegalArgumentException("map must not be null");
        }
        assertThat(map.values())
                        .as("map values")
                        .containsExactlyInAnyOrder(expectedValues);
    }


    public static void assertMapValuesContainExactly(Map<String, String> map, Collection<String> expectedValues)
    {
        assertMapValuesContainExactly(map, expectedValues.toArray(new String[0]));
    }
}
