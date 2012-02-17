/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.AandR.library.io.XmlFile;
import com.AandR.library.utility.CloneWorker;
import com.AandR.library.utility.FastByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;
import static org.junit.Assert.*;

/**
 *
 * @author rstjohn
 */
public class JSONTest {

    public JSONTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() {
        JSONObject o=null, o2=null;
        try {
            
            // Construct an object
            Employee p = new Employee(11, "Biju", new Salary(100));
            double[] w = new double[]{1,2};
            // Create JSON Object
            o = new JSONObject()
                    .append("name", p.name)
                    .append("name", p.name + "lkasjdf")
                    .put("age", p.age)
                    .put("salary", new JSONObject().put("basicPay", p.sal.basicPay))
                    .put("ja", new JSONArray(w));

//            System.out.println("--------------------------o-----------------------");
//            System.out.println(o);

            //Convert to JDOM Element
            Element root = o.toJDOMElement();
//            XmlFile.write(new File("C:/Users/rstjohn/Desktop/parse.xml"), (Element)root.clone());

            //Convert back to JSON
            o2 = JSONObject.fromJDOMElement(root);
//            System.out.println("--------------------------o2-----------------------");
            System.out.println(o2);
            System.out.println("ja(0)="+o2.getJSONArray("ja").getDouble(0));
            
            assertEquals("Employee Name", o2.getString("name"), o.getString("name"));
        } catch (Exception ex) {
            Logger.getLogger(JSONTest.class.getName()).log(Level.SEVERE, null, ex);
            assertFalse(true);
        }
    }
}
