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
package gr.ekt.bteio.loaders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import gr.ekt.bte.core.DataLoader;
import gr.ekt.bte.core.Record;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.exceptions.MalformedSourceException;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestLoaders {
    @Test
    public void testCSVLoader() {
        Map<Integer, String> fields = new HashMap<Integer, String>();
        fields.put(0, "title");
        fields.put(1, "subject");
        fields.put(2, "language");
        fields.put(3, "author");
        fields.put(4, "date");
        fields.put(5, "id");
        fields.put(6, "note");
        fields.put(7, "type");

        try {
            DataLoader dl = new CSVDataLoader("src/test/resources/test_data.csv", fields);
            RecordSet recs = dl.getRecords();
            assertEquals(2, recs.size());
            Record rec = recs.getRecords().get(0);
            for (String fl : fields.values()) {
                assert(rec.hasField(fl));
            }
        } catch(MalformedSourceException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBibTeXLoader() {
        Map<String, String> fields = new HashMap<String, String>();

        fields.put("abstract", "abstract");
        fields.put("acknowledgement", "acknowledgement");
        fields.put("address", "address");
        fields.put("adviser", "adviser");
        fields.put("affiliation", "affiliation");
        fields.put("alttitle", "alttitle");
        fields.put("annote", "annote");
        fields.put("author", "author");
        fields.put("bibdate", "bibdate");
        fields.put("bibsource", "bibsource");
        fields.put("classcodes", "classcodes");
        fields.put("classification", "classification");
        fields.put("CODEN", "CODEN");
        fields.put("confdate", "confdate");
        fields.put("conflocation", "conflocation");
        fields.put("conftitle", "conftitle");
        fields.put("corpsource", "corpsource");
        fields.put("day", "day");
        fields.put("dimensions", "dimensions");
        fields.put("fjournal", "fjournal");
        fields.put("howpublished", "howpublished");
        fields.put("ISBN", "ISBN");
        fields.put("ISBN-13", "ISBN-13");
        fields.put("ISSN", "ISSN");
        fields.put("ISSN-L", "ISSN-L");
        fields.put("journal", "journal");
        fields.put("keywords", "keywords");
        fields.put("LCCN", "LCCN");
        fields.put("month", "month");
        fields.put("note", "note");
        fields.put("number", "number");
        fields.put("pages", "pages");
        fields.put("paperback", "paperback");
        fields.put("price", "price");
        fields.put("pubcountry", "pubcountry");
        fields.put("publisher", "publisher");
        fields.put("school", "school");
        fields.put("thesaurus", "thesaurus");
        fields.put("title", "title");
        fields.put("treatment", "treatment");
        fields.put("type", "type");
        fields.put("URL", "URL");
        fields.put("volume", "volume");
        fields.put("year", "year");

        try {
            BibTeXDataLoader ld = new BibTeXDataLoader("src/test/resources/java.bib", fields);
            RecordSet recs = ld.getRecords();
            assertEquals(100, recs.size());
        } catch (MalformedSourceException e) {
            fail(e.getMessage());
        }
    }
}
