package bn.inference;

import java.util.List;
import java.util.Set;

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
        return new Distribution(X);
    }
    
    public Distribution enumerationASK(RandomVariable X, Assignment e, BayesianNetwork network) {
    		CPT Q = new CPT(X);
    		bn.core.Domain xDomain = X.getDomain();
    		List<RandomVariable> vars = network.getVariablesSortedTopologically();
    		for(Value s:xDomain) {
    			Q.set(s, e, enumerateAll(network,e,vars));
			//where ey is extended with Y=y
    		}
    		Distribution result = new Distribution(Q.getVariable());
    		result.normalize();
		return result; 	
    }
    
    public double enumerateAll(BayesianNetwork network, Assignment e,List<RandomVariable> vars) {
    		//List<RandomVariable> vars = network.getVariablesSortedTopologically();
    		if(vars.isEmpty()) {
    			return 1.0;
    		}
    		RandomVariable Y = vars.get(0);
    		Set<RandomVariable> parent = network.getParents(Y);
			Assignment parentA = new bn.base.Assignment();
			for(RandomVariable r:parent) {
				parentA.put(r, BooleanValue.TRUE);
			}
			vars.remove(0);
    		if(e.containsValue(Y)) {
    			return network.getProbability(Y, parentA)*enumerateAll(network,e,vars);
    		}else {
    			double result = 0;
    			bn.core.Domain yDomain = Y.getDomain();
    			for(Value y:yDomain) {
    				//where ey is extended with Y=y
    				result+=network.getProbability(Y, parentA)*enumerateAll(network,e,vars);
    			}
    			return result;
    		}
    }
    
}