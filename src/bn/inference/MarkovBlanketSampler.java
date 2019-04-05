package bn.inference;

import java.util.Random;
import java.util.Set;

import bn.base.Assignment;
import bn.core.BayesianNetwork;
import bn.base.Value;
import bn.base.BayesianNetwork.Node;
import bn.core.RandomVariable;
public class MarkovBlanketSampler extends java.lang.Object {
    protected BayesianNetwork network;
    protected java.util.Random random;

    public MarkovBlanketSampler(BayesianNetwork network) {
        this.network=network;
        this.random=new Random();
    }
    public MarkovBlanketSampler(BayesianNetwork network, long seed){
        this.network=network;
        this.random=new Random(seed);
    }

    protected void sample(RandomVariable Xi, Assignment xvec){



        double random = 0 + (1 - 0) * this.random.nextDouble();
        double counter = 0;

        //parents(Xi)
        Set<RandomVariable> parentSet = network.getParents(Xi);
        Assignment parentAssignment = new bn.base.Assignment();
        for( RandomVariable x: parentSet){
            parentAssignment.put(x, xvec.get(x));
        }

        //Children(Xi) (Z)
        Set<RandomVariable> childrenSet = network.getChildren(Xi);
        Assignment childrenAssignment = new bn.base.Assignment();
        for( RandomVariable x: childrenSet){
            childrenAssignment.put(x, xvec.get(x));
        }
        
        //for each value in Xi Domain
        for( bn.core.Value v : Xi.getDomain()) {

            //P(Xv|parents(Xi))
            Assignment assignmentCopy=xvec.copy();
            assignmentCopy.put(Xi, v);
            parentAssignment.put(Xi, v);

            double result=network.getProbability(Xi, parentAssignment); 

            //for each z as Xi's children: P(z|parents(z))
            for(RandomVariable z: childrenSet){

                Set<RandomVariable> cpSet = network.getParents(z); 
                Assignment cpAssignment = new bn.base.Assignment();//children's parents
                for( RandomVariable p1: cpSet){
                    cpAssignment.put(p1, xvec.get(p1));
                }
                cpAssignment.put(z, v);

                // Node n = network.getNodeForVariable(x);
                // bn.core.Value v2 = xvec.get(x);
                double pTmp = network.getProbability(z, cpAssignment);
                result*=pTmp;
                    
             //result = P(xi|mb(xi))
                System.out.println("{DEBUG 71} Xi "+Xi+" counter"+counter+" result"+result+"  random=="+random);
             counter+=result;
             if(random<=counter){
                 System.out.println("[DEBUG 72] v="+v+" Xi"+xvec.get(Xi));
                 System.out.println("TESTETS----"+xvec);
                xvec.put(Xi, v);
                System.out.println("TESTETS2----"+xvec);
                return;
             }
            }

        }

    
    }

    public bn.core.Value getValue(int i,bn.core.Domain domain) {
		int counter = 0;
		for(bn.core.Value v : domain) {
			if(counter==i) {
				return v;
			}
			counter++;
		}
		return null;
	}

}