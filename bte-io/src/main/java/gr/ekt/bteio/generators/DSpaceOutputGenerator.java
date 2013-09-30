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
package gr.ekt.bteio.generators;

import gr.ekt.bte.core.DataOutputSpec;
import gr.ekt.bte.core.OutputGenerator;
import gr.ekt.bte.core.Record;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.Value;
import gr.ekt.bteio.specs.DSpaceOutputSpec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class DSpaceOutputGenerator implements OutputGenerator {
    private Map<String, String> field_map_;
    private DSpaceOutputSpec spec_;
    private String output_directory_ = "./output";
    private int padding_ = 5;
    // private boolean write_output_;

    private Logger logger_ = Logger.getLogger(DSpaceOutputGenerator.class);

    // public DSpaceOutputGenerator() {
    //     field_map_ = new HashMap<String, String>();
    //     spec_ = null;
    // }

    public DSpaceOutputGenerator(Map<String, String> fmap) {
        field_map_ = fmap;
        spec_ = null;
    }

    public DSpaceOutputGenerator(Map<String, String> fmap, DSpaceOutputSpec spec) {
        field_map_ = fmap;
        spec_ = spec;
    }

    /**
     * Return the records in JSON.
     */
    @Override
    public List<String> generateOutput(RecordSet recs) {
        List<String> ret = null;
        if (spec_ != null) {
            ret = createOutput(recs, spec_.getPrefixDir(), spec_.getPadding());
        }
        else {
            ret = createOutput(recs, output_directory_, padding_);
        }

        //Write the generated json to a file for debugging purposes.
        // try {
        //     String filename;
        //     if (spec_ != null) {
        //         File dir = new File(spec_.getPrefixDir());
        //         if (!dir.exists())
        //             dir.mkdir();
        //         filename = spec_.getPrefixDir() + File.separator + "output.json";
        //     }
        //     else {
        //         File dir = new File(output_directory_);
        //         if (!dir.exists())
        //             dir.mkdir();
        //         filename = output_directory_ + File.separator + "output.json";
        //     }
        //     PrintWriter pw = new PrintWriter(new File(filename));
        //     pw.println("[");
        //     System.out.println("size = " + ret.size());
        //     for (int i = 0; i < ret.size(); i++) {
        //         pw.print(ret.get(i));
        //         if (i < ret.size() - 1) {
        //             pw.println(",");
        //         }
        //     }
        //     pw.println("]");
        //     pw.close();
        // } catch(FileNotFoundException e) {
        //     //nothing to see here
        // }

        return ret;
    }

    /**
     * Return the records in JSON.
     */
    @Override
    public List<String> generateOutput(RecordSet recs, DataOutputSpec spec) {
        spec_ = (DSpaceOutputSpec)spec;

        return generateOutput(recs);
    }

    public void writeOutput(List<String> output) {
        for (String rec : output) {
            JsonStreamParser jsp = new JsonStreamParser(rec);
            JsonObject top_level_object = jsp.next().getAsJsonObject();
            String dir_prefix  = top_level_object.get("dir_prefix").getAsString();

            File parent_dir = new File(dir_prefix);
            if (!parent_dir.exists()) {
                parent_dir.mkdir();
            }
            JsonObject dir = top_level_object.getAsJsonObject("directory");

            String path = dir.getAsJsonPrimitive("path").getAsString();

            File dir_path = new File(path);
            if (!dir_path.exists()) {
                dir_path.mkdir();
            }
            JsonArray files = dir.getAsJsonArray("files");
            for (JsonElement file : files) {
                JsonObject file_object = file.getAsJsonObject();
                String filename = path + File.separator + file_object.getAsJsonPrimitive("name").getAsString();
                try {
                    PrintWriter xml_file = new PrintWriter(new File(filename));
                    xml_file.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    xml_file.println("<dublin_core schema='" + file_object.getAsJsonPrimitive("schema").getAsString() + "'>");
                    JsonArray data = file_object.getAsJsonArray("data");
                    for (JsonElement dc_value : data) {
                        JsonObject value_object = dc_value.getAsJsonObject().getAsJsonObject("dc_value");
                        String line = "  <dc_value ";
                        line += "namespace='" + value_object.getAsJsonPrimitive("namespace").getAsString() + "' ";
                        line += "element='" + value_object.getAsJsonPrimitive("element").getAsString() + "'";
                        if (value_object.has("qualifier")) {
                            line += " qualifier='" + value_object.getAsJsonPrimitive("qualifier").getAsString() + "'";
                        }
                        line += ">" + value_object.getAsJsonPrimitive("value").getAsString() + "</dc_value>";
                        xml_file.println(line);
                    }
                    xml_file.println("</dublin_core>");
                    xml_file.close();
                } catch (FileNotFoundException e) {
                    logger_.info("Cannot open file " + filename);
                }
            }
        }
    }

    private List<String> createOutput(RecordSet records, String dir_prefix, int padding) {
        ArrayList<String> ret = new ArrayList<String>();
        Map<String, List<String>> namespace_fields = new HashMap<String, List<String>>();
        for (String key : field_map_.keySet()) {
            String elems[] = key.split("\\.");
            if (elems.length == 1) {
                //TODO Raise an exception.
            }

            if (!namespace_fields.containsKey(elems[0])) {
                namespace_fields.put(elems[0], new ArrayList<String>());
            }

            namespace_fields.get(elems[0]).add(key);
        }

        String format_string = "%0" + padding + "d";
        int cnt = 0;

        String[] titles = {"namespace", "element", "qualifier"};

        for (Record rec : records) {
            String elem = "{\"dir_prefix\": \"" + dir_prefix + "\", ";
            cnt++;
            String output_directory = dir_prefix + File.separator + String.format(format_string, cnt) + File.separator;
            logger_.debug("Outdir = " + output_directory);
            elem += "\"directory\": {\"path\": \"" + output_directory + "\", ";
            //Output the namespaces, one in each file
            Iterator<String> ns_it = namespace_fields.keySet().iterator();
            elem += "\"files\":[";
            while (ns_it.hasNext()) {
                String filename;
                String cns = ns_it.next();
                if (cns.equals("dc")) {
                    filename = "dublin_core.xml";
                }
                else {
                    filename = "metadata_" + cns + ".xml";
                }
                elem += "{\"name\": \"" + filename + "\", \"schema\": \"" + cns + "\", \"data\":[";
                Iterator<String> field_it = namespace_fields.get(cns).iterator();
                while(field_it.hasNext()) {
                    String field = field_it.next();
                    String[] field_elems = field.split("\\.");
                    if (field_elems.length < 2 || field_elems.length > 3) {
                        //ERROR
                    }
                    String rec_field = field_map_.get(field);
                    Iterator<Value> value_it = rec.getValues(rec_field).iterator();
                    while (value_it.hasNext()) {
                        elem += "{\"dc_value\": {";
                        Value val = value_it.next();
                        for (int idx = 0; idx < field_elems.length; idx++) {
                            elem += "\"" + titles[idx] + "\": \"" + field_elems[idx] + "\", ";
                        }
                        elem += "\"value\": \"" + sanitize(val.getAsString()) +  "\"";
                        elem += "}}"; //closes the dc_value
                        if (value_it.hasNext()) {
                            elem += ", ";
                        }
                    }
                    if (field_it.hasNext()) {
                        elem += ", ";
                    }
                }
                elem += "]"; //closes the data
                elem += "}"; //closes the file
                if (ns_it.hasNext()) {
                    elem += ", ";
                }
            }
            elem += "]"; //closes the "files" array
            elem += "}"; //closes the "directory" value
            elem += "}"; //closes the initial object

            //System.out.println(elem);
            ret.add(elem);
        }

        return ret;
    }

    private String sanitize(String inp) {
        return inp.replaceAll("\\\"", "\\\\\"");
    }

    /**
     * @return the spec_
     */
    public DSpaceOutputSpec getSpec() {
        return spec_;
    }

    /**
     * @param spec_ the spec_ to set
     */
    public void setSpec(DSpaceOutputSpec spec_) {
        this.spec_ = spec_;
    }

    public void setFieldMap(Map<String, String> fmap) {
        field_map_ = fmap;
    }

    public Map<String, String> getFieldMap() {
        return field_map_;
    }
}
