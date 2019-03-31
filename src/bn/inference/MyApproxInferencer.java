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

public class MyApproxInferencer {
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		bn.core.BayesianNetwork bn = new BayesianNetwork();
		int sampleN = Integer.parseInt(args[0]);
		String file = args[1];
		String query = args[2];
		ArrayList<String> observedEvidence = new ArrayList<String>();
        for(int i = 3; i < args.length; i ++){
        		observedEvidence.add(args[i]);
        }

        if(file.contains("xml")){

            	XMLBIFParser xmlbifParser = new XMLBIFParser();
            bn = xmlbifParser.readNetworkFromFile("bn/examples/" + file);

        }else if(file.contains("bif")){       
            	BIFParser parser = new BIFParser(new FileInputStream("bn/examples/" + file));
            bn = parser.parseNetwork();
        }
        
        Assignment event = new Assignment();
        for(int i = 0; i < observedEvidence.size() - 1; i += 2){
        	event.put(bn.getVariableByName(observedEvidence.get(i)), new Value(observedEvidence.get(i + 1)));
        }

       
        System.out.println("Rejection Sampling: ");
        RejectionSamplingInferencer exact = new RejectionSamplingInferencer();
        bn.core.Distribution dist = exact.query(bn.getVariableByName(query), event ,bn,sampleN);
        System.out.println(dist);
        
        System.out.println("Likelihood Sampling: ");
        RejectionSamplingInferencer like = new RejectionSamplingInferencer();
        bn.core.Distribution dist2 = like.query(bn.getVariableByName(query), event ,bn,sampleN);
        System.out.println(dist2);

	}
}
