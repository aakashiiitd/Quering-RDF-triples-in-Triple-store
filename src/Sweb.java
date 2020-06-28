import com.github.jsonldjava.utils.Obj;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.tdb.TDBFactory;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sweb {

    public static void main(String[] args) {
        String rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        String rdfs="http://www.w3.org/2000/01/rdf-schema#";
        String schemaorg="http://schema.org/";
        String xs="http://www.w3.org/2001/XMLSchema#";
        String xsd="http://www.w3.org/2001/XMLSchema#";
        String LocalURI="http://www.iiitd.ac.in/course/sweb/";


        String csvFile = "MappingFile.csv";
        String line = "";
        ArrayList<ArrayList<String>> Map = new ArrayList<ArrayList<String>>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                String[] s = line.split(",");
                ArrayList<String> MapRow = new ArrayList<String>(Arrays.asList(s));
                Map.add(MapRow);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        csvFile = "Database.csv";
        line = "";
        ArrayList<ArrayList<String>> database = new ArrayList<ArrayList<String>>();
        int a = 100000;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                String[] s = line.split(",");
                ArrayList<String> data = new ArrayList<String>(Arrays.asList(s));
                database.add(data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*for(int i=0;i<database.size();i++){
          for(int j=0;j<database.get(i).size();j++){
              System.out.print(database.get(i).get(j)+" ");
          }
          System.out.println();
        }*/

        Dataset d = TDBFactory.createDataset("Triple Store");
        d.begin(ReadWrite.WRITE);


        Model m = d.getDefaultModel();

        for(int i=0;i<Map.size();i++){
            if(Map.get(i).get(0).equals("Class")){
                Resource subject = m.createResource(LocalURI + Map.get(i).get(1));
                Property predicate = m.createProperty( rdf+ "type");
                RDFNode object = m.createResource(rdfs+"Class");
                m.add(m.createStatement(subject, predicate, object));
                System.out.println(subject+" "+predicate+" "+object);
                for(int j=1;j<database.size();j++){
                    Resource sub = m.createResource(LocalURI + (database.get(j).get(Integer.valueOf(Map.get(i).get(2))-1)).replaceAll("[^A-Za-z0-9]+", ""));
                    Property pred = m.createProperty(rdf+ "type");
                    RDFNode obj = m.createResource(LocalURI + Map.get(i).get(1));
                    m.add(m.createStatement(sub, pred, obj));
                    System.out.println(sub+" "+pred+" "+obj);
                }
            }
            else if(Map.get(i).get(0).equals("ObjectProperty")){
                Resource subject = m.createResource(LocalURI + Map.get(i).get(1));
                Property predicate = m.createProperty( rdf+ "type");
                RDFNode object = m.createResource(rdf+"Property");
                m.add(m.createStatement(subject, predicate, object));
                System.out.println(subject+" "+predicate+" "+object);

                Resource dsubject = m.createResource(LocalURI + Map.get(i).get(1));
                Property dpredicate = m.createProperty( rdfs+ "domain");
                RDFNode dobject = m.createResource(LocalURI + Map.get(i).get(3));
                m.add(m.createStatement(dsubject, dpredicate, dobject));
                System.out.println(dsubject+" "+dpredicate+" "+dobject);

                Resource rsubject = m.createResource(LocalURI + Map.get(i).get(1));
                Property rpredicate = m.createProperty( rdfs+ "range");
                RDFNode robject = m.createResource(LocalURI + Map.get(i).get(5));
                m.add(m.createStatement(rsubject, rpredicate, robject));
                System.out.println(rsubject+" "+rpredicate+" "+robject);

                for(int j=1;j<database.size();j++){
                    Resource sub = m.createResource(LocalURI + (database.get(j).get(Integer.valueOf(Map.get(i).get(2))-1)).replaceAll("[^A-Za-z0-9]+", ""));
                    Property pred = m.createProperty(LocalURI + Map.get(i).get(1));
                    RDFNode obj = m.createResource(LocalURI + (database.get(j).get(Integer.valueOf(Map.get(i).get(4))-1)).replaceAll("[^A-Za-z0-9]+", ""));
                    m.add(m.createStatement(sub, pred, obj));
                    System.out.println(sub+" "+pred+" "+obj);
                }
            }
            else if(Map.get(i).get(0).equals("DataProperty")){
                Resource subject = m.createResource(LocalURI + Map.get(i).get(1));
                Property predicate = m.createProperty( rdf+ "type");
                RDFNode object = m.createResource(rdf+"Property");
                m.add(m.createStatement(subject, predicate, object));
                System.out.println(subject+" "+predicate+" "+object);

                Resource dsubject = m.createResource(LocalURI + Map.get(i).get(1));
                Property dpredicate = m.createProperty( rdfs+ "domain");
                RDFNode dobject = m.createResource(LocalURI + Map.get(i).get(4));
                m.add(m.createStatement(dsubject, dpredicate, dobject));
                System.out.println(dsubject+" "+dpredicate+" "+dobject);

                if(!Map.get(i).get(1).contains(":")){
                    for(int j=1;j<database.size();j++) {
                        Resource sub = m.createResource(LocalURI + (database.get(j).get(Integer.valueOf(Map.get(i).get(3))-1)).replaceAll("[^A-Za-z0-9]+", ""));
                        Property pred = m.createProperty(LocalURI + Map.get(i).get(1));
                        if (Map.get(i).get(5).equals("String")) {
                            Literal str = m.createTypedLiteral(new String(database.get(j).get(Integer.valueOf(Map.get(i).get(2))-1)));
                            //System.out.println(Integer.valueOf(Map.get(i).get(2))-1);
                            m.add(m.createStatement(sub, pred, str));
                            System.out.println(sub+" "+pred+" "+str);
                        } else if (Map.get(i).get(5).equals("Integer")) {
                            //System.out.println(Integer.valueOf(Map.get(i).get(2))-1);
                            Literal str = m.createTypedLiteral(Integer.valueOf(database.get(j).get(Integer.valueOf(Map.get(i).get(2))-1)));

                            m.add(m.createStatement(sub, pred, str));
                            System.out.println(sub+" "+pred+" "+str);
                        }
                    }
                }
                else{
                    for(int j=1;j<database.size();j++) {
                        Resource sub = m.createResource(LocalURI + database.get(j).get(Integer.valueOf(Map.get(i).get(3))-1));
                        Property pred = m.createProperty(schemaorg + "description");
                        Literal str = m.createTypedLiteral(new String(database.get(j).get(Integer.valueOf(Map.get(i).get(2))-1)));
                        m.add(m.createStatement(sub, pred, str));
                        System.out.println(sub+" "+pred+" "+str);
                    }
                }
            }
            else if(Map.get(i).get(0).equals("subClass")){
                Resource subject = m.createResource(LocalURI + Map.get(i).get(1));
                Property predicate = m.createProperty( rdf+ "type");
                RDFNode object = m.createResource(rdfs+"Class");
                m.add(m.createStatement(subject, predicate, object));
                System.out.println(subject+" "+predicate+" "+object);

                for(int j=3;j<Map.get(i).size();j++) {
                    Resource ssubject = m.createResource(LocalURI + Map.get(i).get(1));
                    Property spredicate = m.createProperty(rdfs + "subClassOf");
                    RDFNode sobject = m.createResource(LocalURI + Map.get(i).get(j));
                    m.add(m.createStatement(ssubject, spredicate, sobject));
                    System.out.println(ssubject+" "+spredicate+" "+sobject);
                }


            }
            else if(Map.get(i).get(0).equals("subProperty")){
                Resource subject = m.createResource(LocalURI + Map.get(i).get(1));
                Property predicate = m.createProperty( rdf+ "type");
                RDFNode object = m.createResource(rdfs+"Property");
                m.add(m.createStatement(subject, predicate, object));
                System.out.println(subject+" "+predicate+" "+object);

                Resource psubject = m.createResource(LocalURI + Map.get(i).get(1));
                Property ppredicate = m.createProperty( rdfs+ "subPropertyOf");
                RDFNode pobject = m.createResource(LocalURI + Map.get(i).get(2));
                m.add(m.createStatement(psubject, ppredicate, pobject));
                System.out.println(psubject+" "+ppredicate+" "+pobject);

                Resource dsubject = m.createResource(LocalURI + Map.get(i).get(1));
                Property dpredicate = m.createProperty( rdfs+ "domain");
                RDFNode dobject = m.createResource(LocalURI + Map.get(i).get(3));
                m.add(m.createStatement(dsubject, dpredicate, dobject));
                System.out.println(dsubject+" "+dpredicate+" "+dobject);

                Resource rsubject = m.createResource(LocalURI + Map.get(i).get(1));
                Property rpredicate = m.createProperty( rdfs+ "range");
                RDFNode robject = m.createResource(LocalURI + Map.get(i).get(5));
                m.add(m.createStatement(rsubject, rpredicate, robject));
                System.out.println(rsubject+" "+rpredicate+" "+robject);
            }

        }

        for(int i=1;i<database.size();i++){
            if(database.get(i).get(4).equals("A")){
                Resource subject = m.createResource(LocalURI + (database.get(i).get(2)).replaceAll("[^A-Za-z0-9]+", ""));
                Property predicate = m.createProperty( rdf+ "type");
                RDFNode object = m.createResource(LocalURI + "Agrade");
                m.add(m.createStatement(subject, predicate, object));
                System.out.println(subject+" "+predicate+" "+object);

                Resource subject2 = m.createResource(LocalURI + (database.get(i).get(2)).replaceAll("[^A-Za-z0-9]+", ""));
                Property predicate2 = m.createProperty( LocalURI+"hasGrade1");
                RDFNode object2 = m.createResource(LocalURI + "A");
                m.add(m.createStatement(subject2, predicate2, object2));
                System.out.println(subject2+" "+predicate2+" "+object2);
            }
            else if(database.get(i).get(4).equals("B")){
                Resource subject = m.createResource(LocalURI + (database.get(i).get(2)).replaceAll("[^A-Za-z0-9]+", ""));
                Property predicate = m.createProperty( rdf+ "type");
                RDFNode object = m.createResource(LocalURI + "Bgrade");
                m.add(m.createStatement(subject, predicate, object));
                System.out.println(subject+" "+predicate+" "+object);

                Resource subject2 = m.createResource(LocalURI + (database.get(i).get(2)).replaceAll("[^A-Za-z0-9]+", ""));
                Property predicate2 = m.createProperty( LocalURI+"hasGrade2");
                RDFNode object2 = m.createResource(LocalURI + "B");
                m.add(m.createStatement(subject2, predicate2, object2));
                System.out.println(subject2+" "+predicate2+" "+object2);
            }
            else if(database.get(i).get(4).equals("C")){
                Resource subject = m.createResource(LocalURI + (database.get(i).get(2)).replaceAll("[^A-Za-z0-9]+", ""));
                Property predicate = m.createProperty( rdf+ "type");
                RDFNode object = m.createResource(LocalURI + "Cgrade");
                m.add(m.createStatement(subject, predicate, object));
                System.out.println(subject+" "+predicate+" "+object);

                Resource subject2 = m.createResource(LocalURI + (database.get(i).get(2)).replaceAll("[^A-Za-z0-9]+", ""));
                Property predicate2 = m.createProperty( LocalURI+"hasGrade3");
                RDFNode object2 = m.createResource(LocalURI + "C");
                m.add(m.createStatement(subject2, predicate2, object2));
                System.out.println(subject2+" "+predicate2+" "+object2);
            }

            if(database.get(i).get(18).equals("ACTIVE")){
                Resource subject = m.createResource(LocalURI + (database.get(i).get(2)).replaceAll("[^A-Za-z0-9]+", ""));
                Property predicate = m.createProperty( rdf+ "type");
                RDFNode object = m.createResource(LocalURI + "ActiveProgram");
                m.add(m.createStatement(subject, predicate, object));
                System.out.println(subject+" "+predicate+" "+object);
            }
            else if(database.get(i).get(18).equals("INACTIVE")){
                Resource subject = m.createResource(LocalURI + (database.get(i).get(2)).replaceAll("[^A-Za-z0-9]+", ""));
                Property predicate = m.createProperty( rdf+ "type");
                RDFNode object = m.createResource(LocalURI + "InactiveProgram");
                m.add(m.createStatement(subject, predicate, object));
                System.out.println(subject+" "+predicate+" "+object);
            }
        }

        try {
            FileWriter out = new FileWriter("triples.ttl");
            RDFDataMgr.write(out, m, RDFFormat.TURTLE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try (QueryExecution qExec = QueryExecutionFactory.create(
                "SELECT (count(*) AS ?count) { ?s ?p ?o} ", d)) {
            ResultSet rs = qExec.execSelect() ;
            ResultSetFormatter.out(rs) ;
        }

        d.commit();
        d.end();

    }

}