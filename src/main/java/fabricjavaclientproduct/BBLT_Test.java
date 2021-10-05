package fabricjavaclientproduct;

/*Minimize z = 41x1 + 35x2 +96x3

2x1 + 3x2 + 7x3 >= 1250
1x1 + 1x2 + 0x3 >= 250
5x1 + 3x2 + 0x3 >= 900
0.6x1 + 0.25x2 + 1x3 >= 232.5
x1 >=0, x2>=0, x3 >=0*/

import java.util.ArrayList;
import java.util.List;

import ilog.concert.*;
import ilog.cplex.*;

public class BBLT_Test {

	public static void main(String[] args) {

		int numberOf_sum_of_products= 12250;
		int numberOf_right_side_of_equations = 250;//edges, equations
		int numberOf_product = 5;
		//double[] sum_of_products= { 41, 35, 9, 11, 25, 96, 71, 35, 9, 111, 25, 6 };//object function also distances between nodes product decisions variables
		double[] sum_of_products = new double[numberOf_sum_of_products];
		for (int i = 0; i < numberOf_sum_of_products; i++) {
			sum_of_products[i] = 10*i;
		}
//		System.out.print("c={");
//		for (int i = 0; i < numberOf_sum_of_products; i++) {
//			  System.out.print(c[i]);
//			  System.out.print(',');
//		}
//		System.out.println("}");
//		double[][] A = { { 1,   1,   1,   0,   0,   0,   0,   0,   0,   0,   0,   0 }, 
//						 { 0,   1,   1,   1,   1,   1,   0,   0,   0,   0,   0,   0 }, 
//						 { 0,   0,   1,   1,   1,   1,   0,   0,   0,   0,   0,   0 }, 
//						 { 0,   0,   0,   0,   0,   0,   0,   1,   1,   1,   0,   0 },
//						 { 0,   0,   0,   1,   1,   1,   0,   0,   0,   0,   0,   0 },
//						 { 1,   0,   0,   1,   0,   0,   0,   0,   0,   1,   1,   1 },
//						 { 0,   0,   0,   1,   1,   1,   0,   0,   0,   0,   0,   0 }, 
//						 { 1,   0,   1,   1,   1,   1,   0,   1,   0,   1,   0,   0 }, 
//						 { 1,   1,   1,   0,   0,   1,   0,   0,   0,   1,   0,   0 }, 
//						 { 0,   1,   0,   1,   0,   0,   0,   0,   0,   1,   0,   0 }, 
//						 { 1,   1,   0,   1,   0,   1,   0,   1,   0,   0,   1,   0 },
//						 { 1,   0,   1,   0,   1,   0,   0,   1,   0,   0,   0,   1 }, 
//						 { 1,   1,   0,   1,   0,   1,   0,   1,   0,   0,   1,   0 },
//						 { 1,   0,   1,   0,   1,   0,   0,   1,   0,   0,   0,   1 }};
		double[][][] A = new double[numberOf_right_side_of_equations][numberOf_sum_of_products][numberOf_product];
		for (int i = 0; i < numberOf_right_side_of_equations; i++) {
			for (int j = 0; j < numberOf_sum_of_products; j++) {
				for (int k = 0; k < numberOf_product; k++) {
				if((numberOf_sum_of_products/3 >= j && i <= numberOf_right_side_of_equations/3)
					||	((numberOf_sum_of_products*2/3 >= j  && numberOf_sum_of_products/3 < j && i <= numberOf_right_side_of_equations*2/3 && i > numberOf_right_side_of_equations/3 ))
					||	(numberOf_sum_of_products*2/3 < j  && i > numberOf_right_side_of_equations*2/3 ))
					A[i][j][k] = 1;
				else
					A[i][j][k] = 0;
				}
			}
		}
//		System.out.print("A={");
//		for (int i = 0; i < numberOf_right_side_of_equations; i++) {
//			System.out.print("{");
//			for (int j = 0; j < numberOf_sum_of_products; j++) {
//				for (int k = 0; k < numberOf_product; k++) {
//					System.out.print(A[i][j][k]);
//					System.out.print(", ");
//				}
//			}
//			System.out.print("}");
//			System.out.println(",");
//		}		
//		System.out.println("|");
		
//		double[] right_side_of_equations = { 50, 20, 100, 200, 100, 250, 300,
//				150, 250, 400, 50, 20};//right side of equations
		double[] right_side_of_equations =  new double[numberOf_right_side_of_equations];
		int balance = 1;
		for (int i = 0; i < numberOf_right_side_of_equations; i++) {			
			  right_side_of_equations[i] = balance*i;
			  balance++;
			  if(i%10 == 0)
				  balance = 1;//1,4,9,16,25,36,49,64,81,100,11,24
		}
//		System.out.print("right_side_of_equations={");
//		for (int i = 0; i < numberOf_right_side_of_equationsnumberOf_right_side_of_equations; i++) {
//			  System.out.print(right_side_of_equations[i]);
//			  System.out.print(',');
//		}
//		System.out.println("}");
		long starting = System.currentTimeMillis();
		solveModel(numberOf_sum_of_products, numberOf_right_side_of_equations, numberOf_product, sum_of_products, A, right_side_of_equations);
		long elapsed = System.currentTimeMillis() - starting;
		System.out.println("solveModelDuration: " + elapsed + " ms.");
	}

	public static void solveModel(int n, int m, int t, double[] c, double[][][] A, double[] b) {
		try {
			long starting = System.currentTimeMillis();
			IloCplex model = new IloCplex();

			IloNumVar[] x = new IloNumVar[n];
			for (int i = 0; i < n; i++) {
				x[i] = model.numVar(0, Double.MAX_VALUE);
			}

			IloLinearNumExpr obj = model.linearNumExpr();
			for (int i = 0; i < n; i++) {
				obj.addTerm(c[i], x[i]);
			}
			model.addMinimize(obj);
			
			List<IloRange> constraints = new ArrayList<IloRange>();

			for (int i = 0; i < m; i++) {
				IloLinearNumExpr constraint = model.linearNumExpr();
				for (int j = 0; j < n; j++) {
					for (int h = 0; h < t; h++) {
					constraint.addTerm(A[i][j][h], x[j]);
					}
				}
				constraints.add(model.addGe(constraint, b[i]));
			}

			boolean isSolved = model.solve();
			if (isSolved) {
				double objValue = model.getObjValue();
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
