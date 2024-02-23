import java.util.Objects;
import java.util.Arrays;

import static java.util.Arrays.copyOfRange;

public class Parser {
    String commandName;
    String[] args;

    public Parser() {
        commandName = "";
        args = new String[10];
    }
    public boolean parse(String input) {
        String[] words = input.split(" ");
        if (words.length > 0) {
        commandName = words[0];
        if (words.length > 1) {
            args = Arrays.copyOfRange(words, 1, words.length);
        }
        return true;
        }
        else {
            return false;
        }
    }

    public String getCommandName(){
        return commandName;
    }
    public String[] getArgs(){
        return args;
    }
}
