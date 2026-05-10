package custom_rmi;

public class StaticStubGeneratorCli {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage:");
            System.out.println("  java custom_rmi.StaticStubGeneratorCli <fully-qualified-interface-name>");
            System.out.println();
            System.out.println("Example:");
            System.out.println("  java custom_rmi.StaticStubGeneratorCli banking_custom_rmi.BankService");
            return;
        }

        String interfaceName = args[0];

        StaticStubGenerator.generateStub(interfaceName);
    }
}