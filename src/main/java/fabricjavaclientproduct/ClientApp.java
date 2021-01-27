package fabricjavaclientproduct;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class ClientApp {
	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	private static double[] convertToDoubleArray(String s) {
		// 4,5,6
		String[] ls;
		ls = s.split(",");
		double[] d = new double[ls.length];
		for (int i = 0; i < ls.length; i++) {
			System.out.println(ls[i]);
			d[i] = Double.parseDouble(ls[i]);
		}
		return d;
	}

	private static double[][] convertToDouble2DArray(String s) {
		// {{4, 2, 2, 4}, {3, 4, 5, 6}, {6, 7, 8, 9}, {3, 2, 1, 4}}
		s = s.replace("{", "");// replacing all [ to ""
		s = s.substring(0, s.length() - 2);// ignoring last two ]]
		String s1[] = s.split("},");// separating all by "],"

		String my_matrics[][] = new String[s1.length][s1.length];// declaring two dimensional matrix for input
		double[][] A = new double[s1.length][s1.length];
		for (int i = 0; i < s1.length; i++) {
			s1[i] = s1[i].trim();// ignoring all extra space if the string s1[i] has
			String single_int[] = s1[i].split(", ");// separating integers by ", "

			for (int j = 0; j < single_int.length; j++) {
				my_matrics[i][j] = single_int[j];// adding single values
				A[i][j] = Double.parseDouble(my_matrics[i][j]);
			}
		}

		return A;
	}

	public static void main(String[] args) throws Exception {
		double[][] B = convertToDouble2DArray("{{4, 2, 2, 4}, {3, 4, 5, 6}, {6, 7, 8, 9}, {3, 2, 1, 4}}");
		System.out.println(B);
		double[] X = convertToDoubleArray("2,3,4");
		System.out.println(X);
		IloCplex model = new IloCplex();

		IloNumVar[] x = new IloNumVar[2];
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "fabric-samples", "test-network", "organizations",
				"peerOrganizations", "org1.example.com", "connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);

		// create a gateway connection
		try (Gateway gateway = builder.connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("samplechannel");
			Contract contract = network.getContract("ProductTransfer");

			byte[] result;

			contract.submitTransaction("addNewProduct", "Pharmacy2_Product4_03.04.2020", "Product4_03.04.2020",
					"Product", "Pharmacy1", "100", "12", "04.04.2030", "01.02.2020", "on sale", "03.04.2020",
					"Pharmacy2", "");

			result = contract.evaluateTransaction("queryProductById", "Pharmacy2_Product4_03.04.2020");
			System.out.println(new String(result));

			contract.submitTransaction("changeProductOwnership", "Pharmacy2_Product4_03.04.2020", "Pharmacy3");

			result = contract.evaluateTransaction("queryProductById", "Pharmacy2_Product4_03.04.2020");
			System.out.println(new String(result));

			System.out.println("worked");

			System.out
					.println(new String(contract.submitTransaction("deleteProduct", "Pharmacy2_Product4_03.04.2020")));

			System.out.println("deleting worked");

			result = contract.submitTransaction("purchaseSomeProduct", "Pharmacy1_AUGBID_01.01.2021", "Pharmacy3", "7");
			System.out.println(new String(result));

			result = contract.evaluateTransaction("queryProductById", "Pharmacy1_AUGBID_01.01.2021");
			System.out.println(new String(result));

			System.out.println("purchase worked");

		}
	}
}
