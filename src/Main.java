import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Terminal terminal = new Terminal();

        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.print(terminal.curr_directory + "> ");
            String command = scanner.nextLine();
            command = command.replace("\\", "/");
            terminal.chooseCommandAction(command);
        }
    }
}
