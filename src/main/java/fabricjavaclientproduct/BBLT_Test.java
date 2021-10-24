package fabricjavaclientproduct;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ilog.concert.*;
import ilog.cplex.*;

public class BBLT_Test {

	public static void main(String[] args) {
		System.out.println("Method is runnining..");
		int numberOfPharmacy = 50;
		int numberOfWareHouse = 50;//in our case it must be same as numberOfPharmacy
		int numberOfMedicine = 25;
		
		int numberOf_sum_of_products= numberOfMedicine * numberOfPharmacy * numberOfWareHouse;
		int numberOf_right_side_of_equations = (numberOfWareHouse + numberOfPharmacy) * numberOfMedicine;
		
//		System.out.println("numberOf_sum_of_products=" + numberOf_sum_of_products);
//		System.out.println("numberOf_right_side_of_equations=" + numberOf_right_side_of_equations);
//		double[] objectFunction= { 4, 5, 6, 8, 10, 6, 4, 3, 5, 8, 9, 7, 4, 2, 4,
//								   4, 5, 6, 8, 10, 6, 4, 3, 5, 8, 9, 7, 4, 2, 4};//object function also distances between nodes product decisions variables


		double[] objectFunction = new double[numberOf_sum_of_products];
		for (int i = 0; i < (numberOfWareHouse * numberOfPharmacy); i++) {
			int randomNum = ThreadLocalRandom.current().nextInt(10, 100 + 1);
			objectFunction[i] = randomNum;
		}
		for (int i = (numberOfWareHouse * numberOfPharmacy); i < numberOf_sum_of_products; i++) {
			int randomNum = ThreadLocalRandom.current().nextInt(10, 100 + 1);
			objectFunction[i] = objectFunction[i - (numberOfWareHouse * numberOfPharmacy)];
		}
		System.out.println("objectFunction is in progress..");
//		System.out.print("c={");
//		for (int i = 0; i < numberOf_sum_of_products; i++) {
//			  System.out.print(objectFunction[i]);
//			  System.out.print(',');
//		}
//		System.out.println("}");
//		System.out.println("|");


		//                 x11,x12,x13,...
//		double[][] A = { { 1,0,0,0,0,1,0,0,0,0,1,0,0,0,0 , 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 }, 
//						 { 0,1,0,0,0,0,1,0,0,0,0,1,0,0,0 , 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 }, 
//						 { 0,0,1,0,0,0,0,1,0,0,0,0,1,0,0 , 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 }, 
//						 { 0,0,0,1,0,0,0,0,1,0,0,0,0,1,0 , 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 }, 
//						 { 0,0,0,0,1,0,0,0,0,1,0,0,0,0,1 , 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 },
//						 { 1,1,1,1,1,0,0,0,0,0,0,0,0,0,0 , 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 }, 
//						 { 0,0,0,0,0,1,1,1,1,1,0,0,0,0,0 , 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 }, 
//						 { 0,0,0,0,0,0,0,0,0,0,1,1,1,1,1 , 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 },
//						 
//						 { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 , 1,0,0,0,0,1,0,0,0,0,1,0,0,0,0 }, 
//						 { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 , 0,1,0,0,0,0,1,0,0,0,0,1,0,0,0 }, 
//						 { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 , 0,0,1,0,0,0,0,1,0,0,0,0,1,0,0 }, 
//						 { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 , 0,0,0,1,0,0,0,0,1,0,0,0,0,1,0 }, 
//						 { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 , 0,0,0,0,1,0,0,0,0,1,0,0,0,0,1 },
//						 { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 , 1,1,1,1,1,0,0,0,0,0,0,0,0,0,0 }, 
//						 { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 , 0,0,0,0,0,1,1,1,1,1,0,0,0,0,0 }, 
//						 { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 , 0,0,0,0,0,0,0,0,0,0,1,1,1,1,1 }};
		
		double[][] A = new double[numberOf_right_side_of_equations][numberOf_sum_of_products];
		for (int i = 0; i < numberOf_right_side_of_equations; i++) {			
			for (int j = 0; j < numberOf_sum_of_products; j++) {
				int relativIndisOfMedicineInColumn = j/(numberOfWareHouse * numberOfPharmacy);
				int relativIndisOfMedicineInRow = i/(numberOfWareHouse + numberOfPharmacy);
				//System.out.println("j=" + j);
				//System.out.println("(numberOfWareHouse * numberOfPharmacy)=" + (numberOfWareHouse * numberOfPharmacy));
				//System.out.println("relativIndisOfMedicineInColumn=" + relativIndisOfMedicineInColumn);
				int relativIndisOfWareHouseInMatrix = i%numberOfPharmacy;//1,2,3
				if(relativIndisOfMedicineInColumn == relativIndisOfMedicineInRow)	{	
					if(i%(numberOfWareHouse + numberOfPharmacy) < numberOfPharmacy)//1,2,3,4,5   9,10,11,12,13
					{	
						if((j+i)%numberOfPharmacy == 0 && j%(numberOfPharmacy + 1) != 0)
							A[i][j]= 1;
						else
							A[i][j]= 0;
					}
					else//6,7,8   14,15,16 
					{
						//System.out.println("relativIndisOfWareHouseInMatrix=" + relativIndisOfWareHouseInMatrix);
						if(j >= ((relativIndisOfWareHouseInMatrix) * numberOfPharmacy) && 
								j < ((relativIndisOfWareHouseInMatrix + 1) * numberOfPharmacy) && 
								j%(numberOfPharmacy + 1) != 0)				
							A[i][j]= 1;
						else
							A[i][j]= 0;
					}	
				}
				else {
					//System.out.println("relativIndisOfMedicineInColumn=" + relativIndisOfMedicineInColumn);
					//System.out.println("relativIndisOfMedicineInRow=" + relativIndisOfMedicineInRow);
					A[i][j]= 0;
				}				
			}
		}
		System.out.println("Model is in progress..");
//		System.out.println("A={");
//		for (int i = 0; i < numberOf_right_side_of_equations; i++) {
//			System.out.print("{");
//			for (int j = 0; j < numberOf_sum_of_products; j++) {
//				System.out.print(A[i][j]);
//				System.out.print(", ");
//			}
//			System.out.print("}");
//			System.out.println(",");
//		}		
//		System.out.println("|");

		
//		double[] right_side_of_equations = { 80, 270, 250, 160, 180, 500, 500, 500, 
//											 80, 270, 250, 160, 180, 500, 500, 500};//right side of equations



		double[] right_side_of_equations =  new double[numberOf_right_side_of_equations];
		int randomNumForPharmacies = 1;
		int randomNumForWareHouses = 2;
		for (int i = 0; i < numberOf_right_side_of_equations; i++) {
			
			if(i%(numberOfWareHouse + numberOfPharmacy) < numberOfPharmacy)
			{
				randomNumForPharmacies = ThreadLocalRandom.current().nextInt(10, 80 + 1);
				right_side_of_equations[i] = randomNumForPharmacies;
			}
			else
			{
				randomNumForWareHouses = ThreadLocalRandom.current().nextInt(300, 1000 + 1);
				right_side_of_equations[i] = randomNumForWareHouses;
			}
		}
		System.out.println("right_side_of_equations is in progress..");

		
//		System.out.print("right_side_of_equations={");
//		for (int i = 0; i < numberOf_right_side_of_equations; i++) {
//			  System.out.print(right_side_of_equations[i]);
//			  System.out.print(',');
//		}
//		System.out.println("}");

		
		long starting = System.currentTimeMillis();
		System.out.println("solveModel is in progress..");
		solveModel(numberOfPharmacy, numberOfWareHouse, numberOfMedicine, objectFunction, A, right_side_of_equations);
		long elapsed = System.currentTimeMillis() - starting;
		System.out.println("solveModelDuration: " + elapsed + " ms.");
	}

	public static void solveModel(int numberOfPharmacy, int numberOfWareHouse, int numberOfMedicine, 
			double[] objectFunction, double[][] variables, double[] right_side_of_equations) {
		try {
			long starting = System.currentTimeMillis();
			IloCplex model = new IloCplex();
			
			int n = numberOfPharmacy * numberOfWareHouse * numberOfMedicine;
			int m = (numberOfWareHouse + numberOfPharmacy) * numberOfMedicine;
			
			IloNumVar[] x = new IloNumVar[n];
			for (int i = 0; i < n; i++) {
				x[i] = model.numVar(0, Double.MAX_VALUE);
			}

			IloLinearNumExpr obj = model.linearNumExpr();
			for (int i = 0; i < n; i++) {
				obj.addTerm(objectFunction[i], x[i]);
			}
			model.addMinimize(obj);
			
			List<IloRange> constraints = new ArrayList<IloRange>();

			for (int i = 0; i < m; i++) {
				IloLinearNumExpr constraint = model.linearNumExpr();
				for (int j = 0; j < n; j++) {
					constraint.addTerm(variables[i][j], x[j]);
				}
				if(i%(numberOfWareHouse + numberOfPharmacy) < numberOfPharmacy)//1,2,3,4,5   9,10,11,12,13
					constraints.add(model.addGe(constraint, right_side_of_equations[i]));	
				else//6,7,8   14,15,16 
					constraints.add(model.addLe(constraint, right_side_of_equations[i]));
			}
			System.out.println("constraint is added..");
//			for (int i = 0; i < m; i++) {
//				IloLinearNumExpr constraint = model.linearNumExpr();
//				for (int j = 0; j < n; j++) {
//					if(i == j) {
//						constraint.addTerm(variables[i][j], x[j]);
//					}					
//				}		
//				constraints.add(model.addEq(constraint, 0));
//			}
			
			boolean isSolved = model.solve();
			if (isSolved) {
				double objValue = model.getObjValue();
					System.out.print("Z = ");
				for (int k = 0; k < n; k++) {
					System.out.print( model.getValue(x[k]) + ", ");
				}
				System.out.println(", ");
				System.out.println("onb_val = " + objValue);
				for (int k = 0; k < n; k++) {
					System.out.println("x[" + (k + 1) + "] = " + model.getValue(x[k]));
					System.out.println("Reduce cost " + (k + 1) + " = " + model.getReducedCost(x[k]));
				}

	
			
				for (int i = 0; i < m; i++) {

					double slack = model.getSlack(constraints.get(1));

					double dual = model.getDual(constraints.get(i));
					if (slack == 0) {
						System.out.println("Constraint " + (i + 1) + " is binding.");
					} else {
						System.out.println("Constraint " + (i + 1) + " is non-binding.");
					}

					System.out.println("Shadow price " + (i + 1) + " = " + dual);
				}
							
				
//				for(int f = 0; f < n; f++)
//				{			
//					System.out.print(model.getValue(x[f]) + ",    " );
//					if((f + 1)%numberOfPharmacy == 0)
//						System.out.println(";");
//				}	
				
				System.out.println("m= " + m);
				System.out.println("n= " + n);
				
			} else {
				System.out.println("Model is not solved");
			}
			long elapsed = System.currentTimeMillis() - starting;
			System.out.println("duration: " + elapsed + " ms.");
		} catch (IloException ex) {
			ex.printStackTrace();
		}
	}
}
