package bn.inference;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import bn.base.BooleanValue;
import bn.base.Domain;
import bn.base.StringValue;
import bn.base.BayesianNetwork.Node;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Value;

public class RejectionSamplingInferencer extends java.lang.Object {

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

	public boolean isConsistent(Assignment a, Assignment b) {
		for(RandomVariable v : b.variableSet()){
			//System.out.println(v);
			// see if all e are the same
			//			System.out.println("a"+a.get(v));
			//			System.out.println("b"+a.get(v));
			if(a.get(v).equals(b.get(v))==false){
				return false;
			}
		}
		return true;
	}
	//@Override
	public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network,int n) {
		
		Distribution result = new bn.base.Distribution(X);
		for(Value k: X.getDomain()) {
			result.set(k, 0);
		}
		for(int i=0; i<n; i++) {
			Assignment a = pSample(network);
			//System.out.println("here"+a);
			if(isConsistent(a,e)) {
				result.set(a.get(X), (result.get(a.get(X)) + 1));
			}
		}

		result.normalize();

		return result;
	}
	public double largestProb(RandomVariable r,BayesianNetwork bn,Assignment e) {
		PriorityQueue<Double> pQueue = new PriorityQueue<Double>(); 
		int dSize = r.getDomain().size();
		ArrayList<Double> prob = new ArrayList<Double>();
		Node n = bn.getNodeForVariable(r);
		for(int i=0;i<dSize;i++) {
			double p=n.cpt.get(getValue(i,r.getDomain()),e);
			pQueue.add(p);
		}
		return (1-pQueue.peek());
	}
	public Assignment pSample(BayesianNetwork network) {
		Assignment event = new bn.base.Assignment();
		for(RandomVariable r: network.getVariablesSortedTopologically()) {
			Assignment temp = event.copy();
			Value v = randomSample(network,temp,r);
			if(v!=null) {
			event.put(r, randomSample(network,temp,r));
			}
		}
		return event;
	}
	

	public Value randomSample(BayesianNetwork network,Assignment e,RandomVariable r) {
		double d = largestProb(r, network, e);
		int dSize=  r.getDomain().size();
		double counter = 0;
		Random rd = new Random();
		double random = 0 + (d - 0) * rd.nextDouble();
		//System.out.println(random);
		double p = 0;
		for(int i=0;i<dSize;i++) {
			Assignment temp = e.copy();
			bn.core.Value v = getValue(i,r.getDomain());
			bn.core.Value v2= getValue(1-i,r.getDomain());
			temp.put(r, v);
			p = network.getProbability(r, temp);
			counter+=p;
			if(random<=counter) {
				return v;
			}		

		}
		return null;
	}

//	@Override
//	public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	

}