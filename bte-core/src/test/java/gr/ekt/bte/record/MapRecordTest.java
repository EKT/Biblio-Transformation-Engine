/**
 * Copyright (c) 2007-2013, National Documentation Centre (EKT, www.ekt.gr)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *     Neither the name of the National Documentation Centre nor the
 *     names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior written
 *     permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gr.ekt.bte.record;

import static org.junit.Assert.*;

import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.core.Value;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MapRecordTest {
    private MapRecord rec;
    private ArrayList<Value> val;

    @Before
    public void setUp() {
        rec = new MapRecord();
        val = new ArrayList<Value>();

        val.add(new StringValue("foo"));
        val.add(new StringValue("bar"));
    }

    @Test
    public void testGetValues() {
        assertNull(rec.getValues("test"));
        rec.addField("test", val);

        List<Value> nvals = rec.getValues("test");
        assertEquals(val.size(), nvals.size());

        for (int i = 0; i < nvals.size(); i++) {
            assertEquals(val.get(i), nvals.get(i));
        }
    }

    @Test
    public void testAddField() {
        assertTrue(rec.addField("test", val));
        assertFalse(rec.addField("test", val));
    }

    @Test
    public void testAddValue() {
        rec.addField("test", val);
        Value new_val = new StringValue("baz");
        assertTrue(rec.addValue("test", new_val));
        assertTrue(rec.getValues("test").contains(new_val));
        assertFalse(rec.addValue("test", new StringValue("baz")));
        assertFalse(rec.addValue("test", new_val));
    }

    @Test
    public void testRemoveField() {
        assertFalse(rec.removeField("test"));
        rec.addField("test", val);
        assertTrue(rec.removeField("test"));
        assertFalse(rec.removeField("test"));
    }

    @Test
    public void testRemoveValue() {
        assertFalse(rec.removeValue("test", new StringValue("foo")));

        rec.addField("test", val);

        assertTrue(rec.getValues("test").contains(val.get(0)));
        assertTrue(rec.removeValue("test", val.get(0)));
        assertFalse(rec.getValues("test").contains(new StringValue("foo")));
    }

    @Test
    public void testUpdateField() {
        List<Value> val = new ArrayList<Value>();
        val.add(new StringValue("bar"));
        val.add(new StringValue("baz"));
        val.add(new StringValue("moo"));
        assertFalse(rec.updateField("test", val));

        rec.addField("test", val);

        assertTrue(rec.updateField("test", val));

        List<Value> val2 = rec.getValues("test");
        assertEquals(val2.size(), val.size());
        for (int i = 0; i < val2.size(); i++) {
            assertEquals(val2.get(i), val.get(i));
        }
    }

    @Test
    public void testUpdateValue() {
        Value value = new StringValue("baz");
        assertFalse(rec.updateValue("test", val.get(0), value));

        rec.addField("test", val);

        assertTrue(rec.updateValue("test", val.get(0), value));
        assertEquals(value, rec.getValues("test").get(0));
    }
}
