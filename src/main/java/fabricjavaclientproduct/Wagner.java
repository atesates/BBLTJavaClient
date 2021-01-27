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

public class Wagner {

	public static void main(String[] args) {

		int n = 3;
		int m = 4;
		double[] c = { 41, 35, 96 };
		
		double[][] A = { { 2,   3,    7 }, 
						 { 1,   1,    0 }, 
						 { 5,   3,    0 }, 
						 { 0.6, 0.25, 1 } };
		
		double[] b = { 1250, 250, 900, 232.5 };

		solveModel(n, m, c, A, b);
	}

	public static void solveModel(int n, int m, double[] c, double[][] A, double[] b) {
		try {
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

		} catch (IloException ex) {
			ex.printStackTrace();
		}
	}
}