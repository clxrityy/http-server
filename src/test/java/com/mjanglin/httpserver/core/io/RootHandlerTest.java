package com.mjanglin.httpserver.core.io;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RootHandlerTest {

    private RootHandler rootHandler;
    private Method checkIfEndsWithSlashMethod;
    private Method checkifProvidedRelativePathExistsInsideRootMethod;

    
    @BeforeAll
    public void beforeClass() throws RootNotFoundException, NoSuchMethodException, SecurityException {
        rootHandler = new RootHandler("root");
        Class<RootHandler> cls = RootHandler.class;
        checkIfEndsWithSlashMethod = cls.getDeclaredMethod("checkIfEndsWithSlash", String.class);

        checkIfEndsWithSlashMethod.setAccessible(true);

        checkifProvidedRelativePathExistsInsideRootMethod = cls.getDeclaredMethod("checkIfProvidedRelativePathExistsInsideRoot", String.class);
        checkifProvidedRelativePathExistsInsideRootMethod.setAccessible(true);
    }

    @Test
    void constructorGoodPath() {
        try {
            RootHandler rootHandler = new RootHandler("root");
        } catch (RootNotFoundException e) {
            fail(e);
        }
    }

    @Test
    void constructorBadPath() {
        try {
            RootHandler rootHandler = new RootHandler("/root2");
            fail();
        } catch (RootNotFoundException e) {
            // expected
        }
    }

    @Test
    void constructorGoodPath2() {
        try {
            RootHandler rootHandler = new RootHandler("root");
        } catch (RootNotFoundException e) {
            fail(e);
        }
    }

    @Test
    void constructorBadPath2() {
        try {
            RootHandler rootHandler = new RootHandler("root2");
            fail();
        } catch (RootNotFoundException e) {
            // expected
        }
    }

    @Test
    void checkIfEndsWithSlashMethodTrue() {
        try {
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(rootHandler, "/");
            assertTrue(result);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            fail(ex);
        }
    }

    @Test
    void checkIfEndsWithSlashMethodFalse() {
        try {
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(rootHandler, "index.html");
            assertFalse(result);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            fail(ex);
        }
    }

    @Test
    void checkIfEndsWithSlashMethodTrue2() {
        try {
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(rootHandler, "index.html/");
            assertTrue(result);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            fail(ex);
        }
    }

    @Test
    void checkIfEndsWithSlashMethodFalse2() {
        try {
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(rootHandler, "index.html/index.html");
            assertFalse(result);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            fail(ex);
        }
    }

    @Test
    void checkIfProvidedRelativePathExistsInsideRootMethodTrue() {
        try {
            boolean result = (Boolean) checkifProvidedRelativePathExistsInsideRootMethod.invoke(rootHandler, "index.html");
            assertTrue(result);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            fail(ex);
        }
    }

    @Test
    void checkIfProvidedRelativePathExistsInsideRootMethodFalse() {
        try {
            boolean result = (Boolean) checkifProvidedRelativePathExistsInsideRootMethod.invoke(rootHandler, "index.html/index.html");
            assertFalse(result);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            fail(ex);
        }
    }


    @Test
    void testGetFileMimeTypeText() {
        try {
            String mimeType = rootHandler.getFileMimeType("/index.html");
            assertEquals("text/html", mimeType);
        } catch (FileNotFoundException e) {
            fail(e);
        }
    }

    @Test
    void testGetFileMimeTypePng() {
        try {
            String mimeType = rootHandler.getFileMimeType("/icon.png");
            assertEquals("image/png", mimeType);
        } catch (FileNotFoundException e) {
            fail(e);
        }
    }

    @Test
    void testGetFileMimeTypeDefault() {
        try {
            String mimeType = rootHandler.getFileMimeType("/favicon.ico");
            assertEquals("application/octet-stream", mimeType);
        } catch (FileNotFoundException e) {
            fail(e);
        }
    }

    @Test
    void testGetFileByteArrayData() {
        try {
            byte[] data = rootHandler.getFileByteArrayData("/index.html");
            assertTrue(data.length > 0);
        } catch (FileNotFoundException | ReadFileException e) {
            fail(e);
        }
    }

    @Test
    void testGetFileByteArrayDataFileNotFound() {
        try {
            byte[] data = rootHandler.getFileByteArrayData("/index.html/index.html");
            assertEquals(data, null);
        } catch (FileNotFoundException e) {
            // expected
        } catch (ReadFileException e) {
            fail(e);
        }
    }

    @Test
    void testGetFileTypeCss() {
        try {
            String fileType = rootHandler.getFileMimeType("/style.css");
            assertEquals("text/css", fileType);
        } catch (FileNotFoundException e) {
            fail(e);
        }
    }
}
