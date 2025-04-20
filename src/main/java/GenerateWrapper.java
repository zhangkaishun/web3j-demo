import org.web3j.codegen.SolidityFunctionWrapperGenerator;

public class GenerateWrapper {
    public static void main(String[] args) throws Exception {
        String[] arguments = {
                "--abiFile", "src/main/resources/contracts/MyToken.abi",
                "--binFile", "src/main/resources/contracts/MyToken.bin",
                "--outputDir", "src/main/java",
                "--package", "org.zks.contracts"
        };
        SolidityFunctionWrapperGenerator.main(arguments);
    }

}
