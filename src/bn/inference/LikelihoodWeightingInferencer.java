package bn.inference;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import bn.base.BayesianNetwork.Node;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Value;

public class LikelihoodWeightingInferencer extends java.lang.Object  {

	public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network,int n) {
		//result is a vector of weighted counts, initially zero
		Distribution result = new bn.base.Distribution(X);
		for(Value k: X.getDomain()) {
			result.set(k, 0);
		}
		//for j=1 to N
		for(int i=0; i<n; i++) {
			//x,w<-WeightedSample (BayesianNetwork network, Assignment e)
			WeightedSampler sample =  WeightedSample(network, e);
			Assignment a = sample.assignment;
			double weight = sample.weight ;
			//W[x]<W[X]+w where x is value of X in x
			result.set(a.get(X), (result.get(a.get(X)) + weight));
			System.out.println(result.toString());
		}
		result.normalize();
		return result;
	}

	public WeightedSampler WeightedSample (BayesianNetwork network, Assignment e) {
		double weight =1.0;
		Assignment x=new bn.base.Assignment(); 		Assignment xe=new bn.base.Assignment();
		List<RandomVariable> topoList=network.getVariablesSortedTopologically();
		for (RandomVariable r: topoList) {
			System.out.println("-------"+r);
			x.put(r, null); 
			if(e.containsKey(r)) {
				Set<RandomVariable> parentsList=network.getParents(r);//NOT SURE
				Assignment parentValues=new bn.base.Assignment();
				for(RandomVariable p:parentsList){
					parentValues.put(p, x.get(p));
				}
				parentValues.put(r, e.get(r));
				double probability = network.getProbability(r, parentValues);
				weight *= probability;
				x.put(r, e.get(r));
				xe.put(r, e.get(r));
			}else {
				System.out.println(x.toString()+"  "+xe.toString());
				x.put(r, randomSample(network,xe,r));
			}
		}
		System.out.println("DEBUG 59: "+weight+" "+x.toString());
		return new WeightedSampler(x,weight);

	}


	//below are two helper method
	public Value randomSample(BayesianNetwork network,Assignment e,RandomVariable r) {
		int dSize=  r.getDomain().size();
		double counter = 0;
		Random rd = new Random();
		double random = 0 + (1 - 0) * rd.nextDouble();
		//System.out.println(random);
		double p = 0;
		for(int i=0;i<dSize;i++) {
			Assignment temp = e.copy();
			bn.core.Value v = getValue(i,r.getDomain());
			bn.core.Value v2= getValue(1-i,r.getDomain());
			temp.put(r, v);
			p = network.getProbability(r, temp);
			System.out.println(r.toString()+"  prob"+p);
			counter+=p;
			if(random<=counter) {
				return v;
			}  

		}
		return null;
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



}
