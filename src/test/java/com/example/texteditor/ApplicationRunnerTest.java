package com.example.texteditor;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationRunnerTest {

    @Test
    void mainMethodIsStaticVoidNoArgs() throws Exception {
        Method method = ApplicationRunner.class.getDeclaredMethod("main");

        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(void.class, method.getReturnType());
        assertEquals(0, method.getParameterCount());
    }
}
