public class Main {

    public static void main(String[] args) {
        String input, output;
        // apenas para testes
        if (args.length == 0) {
            input = "teste.l";
            output = "saidaTeste.out";
        } else {
            input = args[0];
            output = args[1];
        }

        Parser parser = null;
        try {
            parser = new Parser(input, output);
            parser.S();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
