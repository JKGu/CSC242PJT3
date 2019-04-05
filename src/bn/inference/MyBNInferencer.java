package bn.inference;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import bn.base.Assignment;
import bn.base.BayesianNetwork;
import bn.base.Distribution;
import bn.base.Value;
import bn.parser.BIFParser;
import bn.parser.XMLBIFParser;

public class MyBNInferencer {
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		bn.core.BayesianNetwork bn = new BayesianNetwork();
		String file = args[0];
		String query = args[1];
		ArrayList<String> observedEvidence = new ArrayList<String>();
        for(int i = 2; i < args.length; i ++){
        		observedEvidence.add(args[i]);
        }

        // read in the network
        if(file.contains("xml")){

            	XMLBIFParser xmlbifParser = new XMLBIFParser();
            bn = xmlbifParser.readNetworkFromFile("bn/examples/" + file);

        }else if(file.contains("bif")){       
            	BIFParser parser = new BIFParser(new FileInputStream("bn/examples/" + file));
            bn = parser.parseNetwork();
        }
        
        Assignment event = new Assignment();
        for(int i = 0; i < observedEvidence.size() - 1; i += 2){
        	event.put(bn.getVariableByName(observedEvidence.get(i)), new Value<String>(observedEvidence.get(i + 1)));
        }

       
        System.out.println("Inference: ");
        EnumerationInferencer exact = new EnumerationInferencer();
        Distribution dist = exact.query(bn.getVariableByName(query), event ,bn);
        System.out.println(dist);

	}
}