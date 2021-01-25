package fabricjavaclientproduct;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

public class TestApp {
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
			System.out.println("10  step: " );
			long starting = System.currentTimeMillis();
		    Integer ContractException = 0;
		    Integer TimeoutException = 0;
		    Integer InterruptedException = 0;
		    Integer GatewayRuntimeException = 0;
		
			for (int i = 111; i < 121; i++) {
				//long start = System.currentTimeMillis();
				//System.out.println("  step: " +  i);
				Integer x = i;
				
				//contract.submitTransaction("addNewProduct", x.toString(), "Product4_03.04.2020",
						//"Product", "Pharmacy1","100", "12", "04.04.2030", "01.02.2020", "on sale", "03.04.2020", "Pharmacy2", "");
				try {
					contract.submitTransaction("purchaseSomeProduct", x.toString(), "Pharmacy3","1");
				}catch(Exception ex ){
					if(ex.getClass().toString()== "ContractException") {
						ContractException++;
					}
					else if(ex.getClass().toString()== "TimeoutException") {
						TimeoutException++;
					}
					else if(ex.getClass().toString()== "InterruptedException") {
						InterruptedException++;
					}
					else if(ex.getClass().toString()== "GatewayRuntimeException"){
						GatewayRuntimeException++;
					}
					System.out.println(new String(ex.toString()));
					
				}
				
				
				
				finally {}
				
				//contract.submitTransaction("changeProductOwnership", x.toString(), "Ates");
				//result = contract.evaluateTransaction("queryProductById", x.toString());
				//long elapsed = System.currentTimeMillis() - start;
				//System.out.println(new String(result));
				//long startChanged = System.currentTimeMillis();
				
				
				/*
				 * System.out.println(new String(result));
				 * 
				 * contract.submitTransaction("changeProductOwnership", x.toString(), "Ates");
				 * 
				 * result = contract.evaluateTransaction("queryProductById", x.toString());
				 * System.out.println(new String(result)); long elapsedChanged =
				 * System.currentTimeMillis() - startChanged;
				 */
				
				//System.out.println("  duration: " + elapsed);
				//System.out.println(" ---------------------------------------------------------------------");
			}
			long elapsed = System.currentTimeMillis() - starting;
			System.out.println("duration: " + elapsed + " ms.");
			System.out.println("ContractException: " + ContractException );
			System.out.println("TimeoutException: " + TimeoutException );
			System.out.println("InterruptedException: " + InterruptedException );
			System.out.println("GatewayRuntimeException: " + GatewayRuntimeException );
		}
		System.out.println("  *** The End ***  " );
	}
}