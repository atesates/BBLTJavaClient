package fabricjavaclientproduct;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

public class ClientApp {
	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	public static void main(String[] args) throws Exception {
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

			contract.submitTransaction("addNewProduct", "4", "Product4", "10", "Pharmacy2", "120", "04.04.2020",
					"01.02.2050", "on sale", "Pharmacy2", "03.04.2020");

			result = contract.evaluateTransaction("queryProductById", "4");
			System.out.println(new String(result));

			contract.submitTransaction("changeProductOwnership", "4", "Pharmacy3");

			result = contract.evaluateTransaction("queryProductById", "4");
			System.out.println(new String(result));

			System.out.println("worked");

			System.out.println(new String(contract.submitTransaction("deleteProduct", "4")));

			System.out.println("deleting worked");

			result = contract.evaluateTransaction("purchaseSomeProduct", "3", "Pharmacy3", "7");
			System.out.println(new String(result));
			result = contract.evaluateTransaction("queryProductById", "3");
			System.out.println(new String(result));

			System.out.println("purchase worked");

		}
	}
}
