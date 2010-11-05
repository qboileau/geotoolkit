/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.util.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.*;

import static org.geotoolkit.test.Commons.assertMultilinesEquals;


/**
 * Tests the {@link MonolineFormatter} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
public final class MonolineFormatterTest {
    /**
     * The formatter to be tested.
     */
    private final MonolineFormatter formatter = new MonolineFormatter(null);

    /**
     * Tests formatting of a multi-line message.
     */
    @Test
    public void testMultilines() {
        final LogRecord record = new LogRecord(Level.INFO, "First line\n  Indented line\nLast line\n");
        final String formatted = formatter.format(record);
        assertMultilinesEquals(
                "INFO        First line\n" +
                "              Indented line\n" +
                "            Last line\n", formatted);
    }

    /**
     * Tests formatting a log record which contains an exception.
     */
    @Test
    public void testException() {
        final LogRecord record = new LogRecord(Level.WARNING, "An exception occured.");
        final Exception exception = new Exception();
        exception.setStackTrace(new StackTraceElement[] {
            new StackTraceElement("org.geotoolkit.NonExistent", "foo",  "NonExistent.java", 10),
            new StackTraceElement("org.junit.WhoKnows",         "main", "WhoKnows.java",    20)
        });
        record.setThrown(exception);
        String formatted = formatter.format(record);
        assertMultilinesEquals(
                "ATTENTION   An exception occured.\n" +
                "            java.lang.Exception\n" +
                "                at org.geotoolkit.NonExistent.foo(NonExistent.java:10)\n" +
                "                at org.junit.WhoKnows.main(WhoKnows.java:20)\n", formatted);
        /*
         * Removed the message and try again.
         */
        record.setMessage(null);
        formatted = formatter.format(record);
        assertMultilinesEquals(
                "ATTENTION   java.lang.Exception\n" +
                "                at org.geotoolkit.NonExistent.foo(NonExistent.java:10)\n" +
                "                at org.junit.WhoKnows.main(WhoKnows.java:20)\n", formatted);
    }
}