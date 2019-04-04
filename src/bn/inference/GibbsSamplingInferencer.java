package bn.inference;

import java.util.*;

import bn.base.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.inference.RejectionSamplingInferencer;
public class GibbsSamplingInferencer extends java.lang.Object {
	public RejectionSamplingInferencer rs;
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network,int n) {
    	List<RandomVariable> vars = network.getVariablesSortedTopologically();
    	for(RandomVariable r:e.variableSet()) {
        vars.remove(r);
    }
    	Assignment result = new Assignment();
    	for(RandomVariable r1:e.variableSet()) {
    		result.put(r1, e.get(r1));
    	}
    	for(RandomVariable r2 :vars) {
    		result.put(r2, rs.randomSample(network, result, r2));
    	}
		Distribution result2 = new bn.base.Distribution(X);
    	for(int i=0;i<n;i++) {
    		MarkovBlanketSampler sample =  mbSample(network,result);
    		Assignment a = (Assignment) sample.assignment;
    		for(RandomVariable r3:vars) {
    			//result.put(r3, result.get(a.get(r3)));
    			result2.set(a.get(r3), (result2.get(a.get(r3)) + 1));
    		}
    	}
    	result2.normalize();
    return result2;
}
    public MarkovBlanketSampler mbSample(BayesianNetwork bn, Assignment e){
    		return null;
    }
}