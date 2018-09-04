package ccmviewer.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

@RestController
public class DirController {

    private static final String BASE = "/data/ng/ccm/";
    private static final String pkgName = "com.ruckuswireless.scg.protobuf.ccm";

    @RequestMapping("/")
    public String path() {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        array.add("config");
        array.add("knowledge");
        json.add("paths", array);
        json.addProperty("isFile", false);
        return json.toString();
    }

    @RequestMapping("/**")
    public String path(HttpServletRequest request) {
        String parentPath = request.getServletPath();
        File file = new File(BASE, parentPath);
        if (file.isDirectory()) {
            JsonObject json = new JsonObject();
            JsonArray array = new JsonArray();
            Arrays.stream(file.list()).forEach(array::add);
            json.add("paths", array);
            json.addProperty("isFile", true);
            return json.toString();
        } else if (StringUtils.endsWithIgnoreCase(file.getPath(), ".gpb")) {
            JsonObject json = new JsonObject();
            JsonArray array = new JsonArray();
            array.add(file.getPath());
            json.add("paths", array);
            json.addProperty("isFile", true);
            json.addProperty("content", parse(file));
            return json.toString();
        } else {
            JsonObject json = new JsonObject();
            JsonArray array = new JsonArray();
            array.add(file.getPath());
            json.add("paths", array);
            json.addProperty("isFile", true);
            json.addProperty("content", toString(file));
            return json.toString();
        }
    }

    private static String parse(File file) {
        try {
            String entityName = StringUtils.substringBefore(file.getName(),".gpb");
            String clsName = pkgName + "." + entityName + "$Ccm" + entityName;
            Method m = Class.forName(clsName).getMethod("parseFrom", byte[].class);
            String s = String.valueOf(m.invoke(null, FileUtils.readFileToByteArray(file)));
            return s;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    private String toString(File file) {
        try {
            return IOUtils.toString(new FileReader(file));
        } catch (IOException e) {
            return e.toString();
        }
    }


}
