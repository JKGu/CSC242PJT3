package bn.inference;

import bn.core.Assignment;
import bn.core.RandomVariable;
import bn.core.Value;

public class MarkovBlanketSampler extends PriorSampler{
	
	        Assignment assignment;
	        double counter = 0.0;

	        MarkovBlanketSampler(Assignment a){
	            this.assignment = a;
	        }

	
	     
}