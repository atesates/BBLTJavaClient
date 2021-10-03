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

public class BBLT_Test8 {

	public static void main(String[] args) {

		int n = 196000;
		int m = 420;//edges, equations
		//double[] c = { 41, 35, 9, 11, 25, 96, 71, 35, 9, 111, 25, 6 };//object function also distances between nodes product decisions variables
		double[] c = new double[n];
		for (int i = 0; i < n; i++) {
			  c[i] = 10*i;
		}
//		System.out.print("c={");
//		for (int i = 0; i < n; i++) {
//			  System.out.print(c[i]);
//			  System.out.print(',');
//		}
//		System.out.println("}");
		//double[][] A = { { 1,   1,   1,   0,   0,   0,   0,   0,   0,   0,   0,   0 }, 
		//				 { 0,   1,   1,   1,   1,   1,   0,   0,   0,   0,   0,   0 }, 
		//				 { 0,   0,   1,   1,   1,   1,   0,   0,   0,   0,   0,   0 }, 
		//				 { 0,   0,   0,   0,   0,   0,   0,   1,   1,   1,   0,   0 },
		//				 { 1,   1,   0,   1,   1,   1,   0,   0,   0,   0,   0,   0 },
		//				 { 1,   0,   0,   1,   0,   0,   0,   0,   0,   1,   1,   1 },
		//				 { 0,   0,   0,   1,   1,   1,   0,   0,   0,   0,   0,   0 }, 
		//				 { 1,   0,   1,   1,   1,   1,   0,   1,   0,   1,   0,   0 }, 
		//				 { 1,   1,   1,   0,   0,   1,   0,   0,   0,   1,   0,   0 }, 
		//				 { 0,   1,   0,   1,   0,   0,   0,   0,   0,   1,   0,   0 }, 
		//				 { 1,   1,   0,   1,   0,   1,   0,   1,   0,   0,   1,   0 },
		//				 { 1,   0,   1,   0,   1,   0,   0,   1,   0,   0,   0,   1 }};
		double[][] A = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if(i > j && i < 2*j)
					A[i][j]= 1;
				else
					A[i][j]= 0;
			}
		}
//		System.out.print("A={");
//		for (int i = 0; i < m; i++) {
//			System.out.print("{");
//			for (int j = 0; j < n; j++) {
//				System.out.print(A[i][j]);
//				System.out.print(", ");
//			}
//			System.out.print("}");
//			System.out.println(",");
//		}		
//		System.out.println("|");
		
		//double[] b = { 50, 20, 100, 200, 100, 250, 300,
		//		150, 250, 400, 50, 20};//right side of equations
		double[] b =  new double[m];
		for (int i = 0; i < m; i++) {
			  b[i] = 10*i;
		}
//		System.out.print("b={");
//		for (int i = 0; i < m; i++) {
//			  System.out.print(b[i]);
//			  System.out.print(',');
//		}
//		System.out.println("}");
		
		solveModel(n, m, c, A, b);
	}

	public static void solveModel(int n, int m, double[] c, double[][] A, double[] b) {
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
					constraint.addTerm(A[i][j], x[j]);
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
