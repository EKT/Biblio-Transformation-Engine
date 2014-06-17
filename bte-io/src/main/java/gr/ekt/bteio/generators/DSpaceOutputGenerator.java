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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

public class DSpaceOutputGenerator implements OutputGenerator {
    private Map<String, String> field_map_;
    private DSpaceOutputSpec spec_;
    private String output_directory_ = "./output";
    private int padding_ = 5;
    private boolean write_json_;
    private int directory_cnt_ = 0;

    private Logger logger_ = Logger.getLogger(DSpaceOutputGenerator.class);

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

        if(write_json_) {
            writeJsonToFile(ret, "./output.json");
        }

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
                String filename = file_object.getAsJsonPrimitive("name").getAsString();
                String abs_filename = path + File.separator + filename;
                PrintWriter file_writer = null;
                try {
                    file_writer = new PrintWriter(new File(abs_filename));
                } catch (FileNotFoundException e) {
                    logger_.debug("Cannot open file " + abs_filename);
                    continue;
                }
                if (filename.equals("contents")) {
                    JsonArray data = file_object.getAsJsonArray("data");
                    for (JsonElement contents : data) {
                        file_writer.println(contents.getAsJsonPrimitive().getAsString());
                    }
                }
                else if (filename.equals("collections")) {
                    JsonArray data = file_object.getAsJsonArray("data");
                    for (JsonElement collections : data) {
                        file_writer.println(collections.getAsJsonPrimitive().getAsString());
                    }
                }
                else if (filename.equals("handle")) {
                    JsonPrimitive data = file_object.getAsJsonPrimitive("data");
                    file_writer.println(data.getAsString());
                }
                else {
                    file_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    file_writer.println("<dublin_core schema='" + file_object.getAsJsonPrimitive("schema").getAsString() + "'>");
                    JsonArray data = file_object.getAsJsonArray("data");
                    for (JsonElement dc_value : data) {
                        JsonObject value_object = dc_value.getAsJsonObject().getAsJsonObject("dcvalue");
                        String line = "  <dcvalue ";
                        line += "namespace='" + value_object.getAsJsonPrimitive("namespace").getAsString() + "' ";
                        line += "element='" + value_object.getAsJsonPrimitive("element").getAsString() + "'";
                        if (value_object.has("qualifier")) {
                            line += " qualifier='" + value_object.getAsJsonPrimitive("qualifier").getAsString() + "'";
                        }
                        line += ">" + StringEscapeUtils.escapeXml(value_object.getAsJsonPrimitive("value").getAsString()) + "</dcvalue>";
                        file_writer.println(line);
                    }
                    file_writer.println("</dublin_core>");

                }
                file_writer.close();
            }
        }
    }

    private List<String> createOutput(RecordSet records, String dir_prefix, int padding) {
        ArrayList<String> ret = new ArrayList<String>();
        Map<String, List<String>> namespace_fields = new HashMap<String, List<String>>();
        for (String key : field_map_.keySet()) {
            String elems[] = key.split("\\.");
            if (elems.length == 1) {
                //We allow handle and contents entries
                if (elems[0].equals("handle") || elems[0].equals("contents") || elems[0].equals("collections")) {
                    continue;
                }
                else {
                    logger_.debug("Field \"" + key + "\" is not a valid dspace field name. Ignoring");
                    continue;
                }
            }

            if (!namespace_fields.containsKey(elems[0])) {
                namespace_fields.put(elems[0], new ArrayList<String>());
            }

            namespace_fields.get(elems[0]).add(key);
        }

        String format_string = "%0" + padding + "d";

        String parent_dir = sanitize(dir_prefix);
        for (Record rec : records) {
            String elem = "{\"dir_prefix\": \"" + parent_dir + "\", ";
            directory_cnt_++;
            String output_directory = dir_prefix + File.separator + String.format(format_string, directory_cnt_) + File.separator;
            logger_.debug("Outdir = " + output_directory);
            elem += "\"directory\": {\"path\": \"" + sanitize(output_directory) + "\", ";
            //Output the namespaces, one in each file
            elem += "\"files\":[";

            List<String> file_json = prepareFilesJSONRepresentation(namespace_fields, rec);

            if (file_json.size() > 0) {
                for(int i = 0; i < file_json.size(); i++) {
                    elem += file_json.get(i);
                    if (i < file_json.size() - 1) {
                        elem += ", ";
                    }
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

    private List<String> prepareFilesJSONRepresentation(Map<String, List<String>> namespace_fields, Record rec) {
        ArrayList<String> ret = new ArrayList<String>();

        Iterator<String> ns_it = namespace_fields.keySet().iterator();
        while (ns_it.hasNext()) {
            String json_file = "";
            String filename;
            String cns = ns_it.next();
            if (cns.equals("dc")) {
                filename = "dublin_core.xml";
            }
            else {
                filename = "metadata_" + cns + ".xml";
            }
            json_file += "{\"name\": \"" + sanitize(filename) + "\", \"schema\": \"" + sanitize(cns) + "\", \"data\":[";
            List<String> file_data = prepareFileDataJSONRepresentation(namespace_fields.get(cns), rec);
            if (file_data.size() == 0) {
                continue;
            }
            for (int i = 0; i < file_data.size(); i++) {
                json_file += file_data.get(i);
                if (i < file_data.size() - 1) {
                    json_file += ", ";
                }
            }
            json_file += "]"; //closes the data
            json_file += "}"; //closes the file

            ret.add(json_file);
        }

        //The contents file contains (optionally) a list of files
        //to be uploaded as bitstreams one in each line
        String json_file = "{\"name\": \"contents\", \"data\":[";
        if (field_map_.containsKey("contents")) {
            List<Value> contents = rec.getValues(field_map_.get("contents"));

            if (contents != null) {
                Iterator<Value> val_it = contents.iterator();
                while(val_it.hasNext()) {
                    Value val = val_it.next();
                    json_file += "\"" + sanitize(val.getAsString()) + "\"";
                    if (val_it.hasNext()) {
                        json_file += ", ";
                    }
                }
            }
        }
        json_file += "]"; //closes the contents file data section
        json_file += "}"; //closes the contents file
        ret.add(json_file);

        //The handle file contains (optionally) the handle that
        //this item should take.
        if (field_map_.containsKey("handle")) { //Do not create handle file if no data is given for handle
            json_file = "{\"name\": \"handle\", \"data\": \"";
            List<Value> handle_list = rec.getValues(field_map_.get("handle"));
            String handle = "";
            if (handle_list != null && handle_list.size() > 0) {
                Value handle_value = handle_list.get(0);
                handle = handle_value.getAsString();
            }
            json_file += sanitize(handle);
            json_file += "\"}"; //closes the handle file
            ret.add(json_file);
        }
        if (field_map_.containsKey("collections")) {
            json_file = "{\"name\": \"collections\", \"data\":[";
            List<Value> collection_list = rec.getValues(field_map_.get("collections"));
            if (collection_list != null) {
                Iterator<Value> val_it = collection_list.iterator();
                while(val_it.hasNext()) {
                    Value val = val_it.next();
                    json_file += "\"" + sanitize(val.getAsString()) + "\"";
                    if (val_it.hasNext()) {
                        json_file += ", ";
                    }
                }
            }
            json_file += "]";
            json_file += "}";
            ret.add(json_file);
        }

        return ret;
    }

    private List<String> prepareFileDataJSONRepresentation(List<String> field_list, Record rec) {
        List<String> ret = new ArrayList<String>();
        String[] titles = {"namespace", "element", "qualifier"};

        for (int i = 0; i < field_list.size(); i++) {
            String field = field_list.get(i);
            String[] field_elems = field.split("\\.");
            if (field_elems.length < 2 || field_elems.length > 3) {
                //ERROR
            }
            String rec_field = field_map_.get(field);
            if (rec_field == null) {
                logger_.debug("Field " + field + " not found in field map");
                continue;
            }
            List<Value> value_list = rec.getValues(rec_field);
            if (value_list == null || value_list.size() == 0) {
                logger_.debug("Field " + field + " has no values");
                continue;
            }

            for (int j = 0; j < value_list.size(); j++) {
                Value val = value_list.get(j);
                if (val.getAsString().equals("")) {
                    logger_.debug("Empty value, not writing");
                    continue;
                }
                String json_value = "{\"dcvalue\": {";
                for (int idx = 0; idx < field_elems.length; idx++) {
                    json_value += "\"" + titles[idx] + "\": \"" + field_elems[idx] + "\", ";
                }
                json_value += "\"value\": \"" + sanitize(val.getAsString()) +  "\"";
                json_value += "}}"; //closes the dc_value
                ret.add(json_value);
            }
        }
        return ret;
    }

    /**
     * Handle JSON special characters.
     *
     */
    private String sanitize(String inp) {
        String ret = inp;
        //Handle the backslashes
        int pfi = 0;
        int fi = ret.indexOf('\\');
        while(fi != -1) {
            // If the last character of the string is '\\', just add a
            // second '\\'
            if (fi == ret.length() - 1) {
                ret = ret + "\\";
                break;
            }
            if (ret.charAt(fi + 1) == '\\') {
                fi = ret.indexOf('\\', fi + 2);
                continue;
            }
            String prefix = ret.substring(pfi, fi);
            String suffix = ret.substring(fi + 1);
            ret = prefix + "\\\\" + suffix;
            fi = ret.indexOf('\\', prefix.length() + 2);
        }

        ret = ret.replaceAll("\\n", "\\\\n");
        ret = ret.replaceAll("\\f", "\\\\f");
        ret = ret.replaceAll("\\r", "\\\\r");
        ret = ret.replaceAll("\\t", "\\\\t");
        ret = ret.replaceAll("\\\"", "\\\\\"");

        return ret;
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

    private void writeJsonToFile(List<String> json, String filename) {
        //Write the generated json to a file for debugging purposes.
        try {
            PrintWriter pw = new PrintWriter(new File(filename));
            pw.println("[");
            System.out.println("size = " + json.size());
            for (int i = 0; i < json.size(); i++) {
                pw.print(json.get(i));
                if (i < json.size() - 1) {
                    pw.println(",");
                }
            }
            pw.println("]");
            pw.close();
        } catch(FileNotFoundException e) {
            //nothing to see here
        }
    }

    /**
     * @return the output_directory_
     */
    public String getOutputDirectory() {
        return output_directory_;
    }

    /**
     * @param output_directory_ the output_directory_ to set
     */
    public void setOutputDirectory(String output_directory_) {
        this.output_directory_ = output_directory_;
    }

    /**
     * @return the debug_
     */
    public boolean getWriteJSON() {
        return write_json_;
    }

    /**
     * @param debug_ the debug_ to set
     */
    public void setWriteJSON(boolean write_json_) {
        this.write_json_ = write_json_;
    }

    /**
     * @return the directory_cnt_
     */
    public int getDirectoryCounter() {
        return directory_cnt_;
    }

    /**
     * @param directory_cnt_ the directory_cnt_ to set
     */
    public void setDirectoryCounter(int directory_cnt_) {
        this.directory_cnt_ = directory_cnt_;
    }
}
