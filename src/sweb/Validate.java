package sweb;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.shacl.vocabulary.SH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Validate {
    private static void validateGraph(String filename) throws IOException {
        Model model = ModelFactory.createDefaultModel() ;
        model.read("triples.ttl") ;

        Model shape = ModelFactory.createDefaultModel();
        shape.read(filename);

        File file = new File("report"+filename);
        FileOutputStream stream = new FileOutputStream(file);

        Resource resource = ValidationUtil.validateModel(model, shape, true);

        boolean isValid = resource.getProperty(SH.conforms).getBoolean();
        System.out.println("isValid?" + isValid);

        RDFDataMgr.write(stream, resource.getModel(), RDFFormat.TTL);
    }

    public static void main(String[] args) {
        try {
            for (int i=1; i<=2; i++)
                validateGraph("graphs/"+i+".ttl");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
