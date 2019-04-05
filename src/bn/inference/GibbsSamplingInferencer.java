package bn.inference;

import java.util.*;

import bn.base.Assignment;
import bn.core.Value;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.inference.RejectionSamplingInferencer;
public class GibbsSamplingInferencer extends java.lang.Object {
    public RejectionSamplingInferencer rs= new RejectionSamplingInferencer();
    public LikelihoodWeightingInferencer ls = new LikelihoodWeightingInferencer();

    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network,int n) {
        //---------------------vars is the list of non evidence
    	List<RandomVariable> vars = network.getVariablesSortedTopologically();
    	for(RandomVariable r:e.variableSet()) {
        vars.remove(r);
        }

        //----------------------copying evidence to sample0
    	Assignment sample = e.copy();

        //-----------------------randomize non evidence - iterate through non-e list
    	for(int i=0;i<vars.size();i++) {
            int size = vars.get(i).getDomain().size(); double counter=0;
            double random=Math.random()*10;
            Value v =null;
            for(int k=0; k<size;k++){
                counter+=10/size;
                if(random<counter){
                    System.out.println("random="+random+"counter="+counter);
                    v = getValue(k,vars.get(i).getDomain());
                    break;
                }
            }
            //Value v = rs.randomSample(network, temp2, vars.get(i));
            sample.put(vars.get(i), v);
        }

        //initialize the probability vector
        Distribution probabilityVector = new bn.base.Distribution(X);
        MarkovBlanketSampler mbs =  new MarkovBlanketSampler(network);
        for(Value k: X.getDomain()) {
			probabilityVector.set(k, 0);
        }

        //start sampling
    	for(int i=0;i<n;i++) {
    		for(RandomVariable r3:vars) {
                System.out.println("before: "+sample);
                mbs.sample(r3, sample);
                System.out.println("after:"+sample); 
    			probabilityVector.set(sample.get(X),probabilityVector.get(sample.get(X))+1);
    		}
    	}
    	probabilityVector.normalize();
    return probabilityVector;
}

public Value getValue(int i,bn.core.Domain domain) {
    int counter = 0;
    for(Value v:domain) {
        if(counter==i) {
            return v;
        }
        counter++;
    }
    return null;
}
}