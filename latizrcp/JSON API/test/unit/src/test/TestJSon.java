package test;

import com.AandR.library.io.XmlFile;
import com.AandR.library.utility.CloneWorker;
import com.AandR.library.utility.FastByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.Date;

import org.jdom.Document;
import org.json.JSONObject;
import org.json.XML;


public class TestJSon {

    public static void main(String[] args) throws Exception{

        Employee p = new Employee(11,"Biju",new Salary(100));
        
        JSONObject o = new JSONObject();
        o.append("name", p.name)
                .append("age", p.age)
                .append("salary", new JSONObject().append("basicPay", p.sal.basicPay));
      
        System.out.println("---------------------JSON OBJECT------------------");
        System.out.println(o.toString(4));

        String xml = XML.toString(o);
        System.out.println("------------------XML-------------------------");
        System.out.println(xml);

        JSONObject obj2 = XML.toJSONObject(xml);
        System.out.println("----------------------JSON OBJ2-----------------");
        System.out.println(obj2);

        Employee p2 = new Employee(obj2.getInt("age"), obj2.getString("name"), new Salary(obj2.getJSONObject("salary").getInt("basicPay")));
        System.out.println("p2 = "+p2);
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <root>").append(xml).append("</root>");
        FastByteArrayOutputStream fbos = CloneWorker.serializeObject(sb.toString());
        Document doc = XmlFile.readDocument(fbos.getInputStream());
        System.out.println("----------------------ELEMENT-----------------");
        System.out.println(doc.getRootElement());
    }
}
/*

{
"LMap": {
 "SAL-1": {"basicPay": 3011},
 "SAL-2": {"basicPay": 4012}
},
"age": 11,
"intge": 77,
"l": [
 {"basicPay": 301},
 {"basicPay": 401}
],
"name": "www",
"sal": {"basicPay": 100},
"salArray": [
 {"basicPay": 30},
 {"basicPay": 40}
],
"status": false
}

*/