package com.android.test.scirocco2.service.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.android.test.scirocco2.datamodel.TestClass;

public class ReportService {
    public void excute(List<TestClass> testClassList, String resourcePath, String outputPath) {
        try {
            Properties properties = new Properties();
            properties.setProperty("file.resource.loader.path", resourcePath);
            Velocity.init(properties);
            VelocityContext context;
            for (TestClass testClass : testClassList) {
                context = new VelocityContext();
                context.put("testClass", testClass);  
                StringWriter sw = new StringWriter(); 
                Template template = Velocity.getTemplate("template.vm", "utf-8");  
                template.merge(context, sw);
                write(sw.toString(), outputPath + System.getProperty("file.separator") + testClass.getClassName() + ".html");  
                sw.flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void write(String targetStr, String path) {
        try{
            File file = new File(path);
            FileWriter filewriter = new FileWriter(file);
            filewriter.write(targetStr);
            filewriter.close();
        }catch(IOException e){
            // file write err
            e.printStackTrace();
        }
    }
}