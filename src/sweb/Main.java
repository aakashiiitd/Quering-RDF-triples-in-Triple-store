package sweb;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

import java.io.*;

public class Main {
    public static String readFile(String filename) throws IOException {
        File file = new File(filename);

        BufferedReader br = new BufferedReader(new FileReader(file));

        StringBuilder query = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            query.append(line);

        return query.toString();
    }

    public static void writeFile(String data, String filename) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(data);
        writer.close();
    }

    private static ResultSet getResultSet(String filename) throws IOException {
        String queryString = readFile(filename);

        Model model = ModelFactory.createDefaultModel() ;
        model.read("triples.ttl") ;

        File file = new File("output"+filename);
        FileOutputStream stream = new FileOutputStream(file);

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            results = ResultSetFactory.copyResults(results);
            ResultSetFormatter.out(stream, results, query) ;
            return results;
        }
    }

    private static boolean getResultSetFromAsk(String filename) throws IOException {
        String queryString = readFile(filename);

        Model model = ModelFactory.createDefaultModel() ;
        model.read("triples.ttl") ;

        File file = new File("output"+filename);
        FileOutputStream stream = new FileOutputStream(file);

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            boolean results = qexec.execAsk();
            if (results)
                Main.writeFile("True", "output"+filename);
            else
                Main.writeFile("False", "output"+filename);
            return results;
        }
    }

    public static void runQuery(String filename) throws IOException {
        if (filename.equals("queries/6.txt") || filename.equals("queries/7.txt") || filename.equals("queries/10.txt")) {
            boolean result = getResultSetFromAsk(filename);
        }
        else {
            ResultSet resultSet = getResultSet(filename);
        }
    }

    public static void main(String[] args) {
        try {
            for (int i = 1; i <= 10; i++)
                runQuery("queries/"+i+".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
