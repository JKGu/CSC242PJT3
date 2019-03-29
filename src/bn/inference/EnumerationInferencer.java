package bn.inference;

import java.util.List;
import java.util.Set;

import bn.base.BayesianNetwork.Node;
import bn.base.BooleanValue;
import bn.base.CPT;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.base.Distribution;
import bn.base.Domain;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Value;

public class EnumerationInferencer extends java.lang.Object implements Inferencer {

    @Override
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
		Distribution Q = new bn.base.Distribution(X);
		bn.core.Domain xDomain = X.getDomain();
		List<RandomVariable> vars = network.getVariablesSortedTopologically();
		for(Value xi:xDomain) {
			Assignment cpy=e.copy();
			cpy.put(X, xi);
			double r=enumerateAll(network,cpy,vars);
			//System.out.println(r);
			Q.set(xi, r);
		} 
		Q.normalize();
	return Q; 	
    }

    public double enumerateAll(BayesianNetwork network, Assignment e,List<RandomVariable> vars) {
    		//List<RandomVariable> vars = network.getVariablesSortedTopologically();
    		if(vars.isEmpty()) {
    			return 1.0;
    		}
			RandomVariable Y = vars.get(0);
			/*
    		Set<RandomVariable> parent = network.getParents(Y);
			Assignment parentA = new bn.base.Assignment();
			for(RandomVariable r:parent) {
				parentA.put(r, BooleanValue.TRUE);
			}
			vars.remove(0);
			*/
    		if(e.containsKey(Y)) {
				Assignment cpy=e.copy();
				return network.getProbability(Y, cpy)*enumerateAll(network, cpy, vars.subList(1, vars.size()));
    		}else {
    			double result = 0;
    			bn.core.Domain yDomain = Y.getDomain();
    			for(Value y:yDomain) {
					Assignment cpy=e.copy();
					cpy.put(Y,y);

//					for(RandomVariable r:parent) {
//						temp.put(Y, e.get(r));
//					}
					result += network.getProbability(Y, cpy)*enumerateAll(network, cpy, vars.subList(1, vars.size()));
    			
    			}
    			return result;
    		}
    }
   
    
}