package com.filemark.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.text.StrTokenizer;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

public class Csv2json {
	private final StrTokenizer token = StrTokenizer.getCSVInstance();
    private Map<String, String> subjects;

    public void setOwnerOrg(String org) {
    }

	public Map<String,String> getSubjects() {
        return this.subjects;
    }
	public void setSubjects(Map<String, String> map) {
        this.subjects = map;

    }
	public String[] parseLine(String line) {
        token.reset(line);
        final String[] row = new String[token.size()];
        for (int i = 0; i < row.length; i++) {
            row[i] = (String) token.next();
        }
		return row;
	}

	public List<String[]> readAll(final BufferedReader reader) throws IOException {
		final List<String[]> list = new ArrayList<String[]>(20);
		String line = reader.readLine();
        token.reset(line);
        int size = token.size();
        String[] row = new String[size];
        for (int i = 0; i < row.length; i++) {
           	row[i] = (String) token.next();
        }
        list.add(row);
        String csvLine = "";
		while ((line = reader.readLine()) != null) {
            csvLine +=line; 
            token.reset(csvLine);
            if(token.size() >= size) {
                row = new String[token.size()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = (String) token.next();
                }

	            list.add(row);
				csvLine = "";
        	}

		}
		return list;
	}

	public List<String[]> readAll(final InputStream in) throws IOException {
		return readAll(new BufferedReader (new InputStreamReader(in,"UTF-8")));
	}

	public Map<String,String> readMap(final InputStream in) throws IOException {
        Map<String,String> subjects = new HashMap<String,String>();
        List<String[]> lines = readAll(in);
        if (lines.size() <= 1) {
			return subjects;
		}
        int m = lines.size();
        lines.get(0);
        for (int j = 1; j < m; j++) {
            final String[] line = lines.get(j);
            //for (int i = 0; i < line.length; i++) {
             subjects.put(line[0], line[1]);
            //} 
        }
		return subjects;
	}

	public void transform(InputStream in , OutputStream out,String lang) throws IOException {
		toJson(readAll(in), out, lang);
		
	}


    public void write(JsonGenerator jg,Map map, String name, String lang) {
        String key = name+"_"+lang;
        String value = (String)map.get(key);
        if("description".equals(name)) value = value.replaceAll("_x000D_","\n");
		jg.write(name, value);
    }

	public void toJson(List<String[]> lines, OutputStream out,String lang) {
		if (lines.size() <= 1) {
			return;
		}
		final String[] header = lines.get(0);

		final Map<String, Object> properties = new HashMap<String, Object>();
		final JsonGeneratorFactory jgf = Json.createGeneratorFactory(properties);
		final JsonGenerator jg = jgf.createGenerator(out);

		try {
			jg.writeStartObject();

			jg.writeStartArray("headers");
			for (String string : header) {
				jg.write(string);
			}
			jg.writeEnd();

			jg.write("size", ""+lines.size());

			jg.writeStartArray("data");
			for (int j = 1; j < lines.size(); j++) {
				final String[] line = lines.get(j);
				jg.writeStartObject();
				for (int i = 0; i < line.length; i++) {
                    jg.write(header[i], line[i]);
				}

				jg.writeEnd();

			}
			jg.writeEnd();

			jg.writeEnd();
		} finally {
			jg.close();
		}
	}

	public String toSubjects (String codes) {
        String subjectStr = "";
        String lines[] = codes.split("\\,");
		subjectStr = (String)subjects.get(lines[0]);
        for (int j = 1; j < lines.length; j++) {
            subjectStr +=" | " +(String)subjects.get(lines[j]);
        }
        return subjectStr;
    }



}
