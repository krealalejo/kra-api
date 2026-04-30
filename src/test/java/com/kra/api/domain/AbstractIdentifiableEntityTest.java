package com.kra.api.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractIdentifiableEntityTest {

    abstract Object build(String id);

    @Test
    void equals_differentId_returnsFalse() {
        assertNotEquals(build("a"), build("b"));
    }

    @Test
    void hashCode_sameId_equal() {
        assertEquals(build("x").hashCode(), build("x").hashCode());
    }

    @Test
    void equals_differentType_returnsFalse() {
        assertNotEquals("string", build("1"));
    }

    @Test
    void equals_null_returnsFalse() {
        assertNotEquals(null, build("1"));
    }

    @Test
    void equals_sameObject_returnsTrue() {
        Object entity = build("1");
        assertEquals(entity, entity);
    }
}
