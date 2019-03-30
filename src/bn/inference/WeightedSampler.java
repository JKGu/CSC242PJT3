package bn.inference;

import bn.core.Assignment;

public class WeightedSampler extends PriorSampler{
	
	        Assignment assignment;
	        double weight = 0.0;

	        WeightedSampler(Assignment a, double weight){
	            this.assignment = a;
	            this.weight = weight;
	        }
	     
}
