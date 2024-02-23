import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Arrays;
import java.util.Scanner;

public class Terminal {
    Parser parser;
    ArrayList<String> history ;
    String curr_directory;
    
    public Terminal() {
        parser = new Parser();
        curr_directory = System.getProperty("user.home");
        history = new ArrayList<>();
    }
    
    public void echo() {
        for (String text : parser.args) {
            System.out.print(text + ' ');
        }
        System.out.println();
    }
    
    public void pwd() {
        if (parser.args[0] != null) {
            System.out.println("Wrong arguments");
            return;
        }
        File currentDir = new File(curr_directory);
        System.out.println(currentDir.getAbsoluteFile());
    }
    
    public void cd() {
        if (parser.args.length > 1 && parser.args[0] != null ) {
            System.out.println("Wrong arguments");
            return;
        }
        if (Objects.equals(parser.args[0], "..")) {
            File file = new File(curr_directory);
            String file_parent = file.getParent();
            if (file_parent != null) {
                curr_directory = file_parent;
            } else {
                System.out.println("The file doesn't have a parent directory");
            }
        } else if (parser.args[0] == null) {
            curr_directory = System.getProperty("user.home");
        } else {
            File file = new File(parser.args[0]);
            if (file.isAbsolute()) {
                 file = new File(parser.args[0]);
                if (file.exists()) {
                    curr_directory = parser.args[0];
                } else {
                    System.out.println("The directory is not found");
                }
            } else {
                 file = new File(curr_directory + "\\" + parser.args[0]);
                if (file.exists()) {
                    curr_directory = curr_directory + "\\" + parser.args[0];
                } else {
                    System.out.println("The directory is not found");
                }
            }
        }
    }
    
    public void ls() {
        if (Objects.equals(parser.args[0], "-r") && parser.args.length == 1) {
            ls_reversed();
            return;
        } else if (parser.args[0] != null) {
            System.out.println("Wrong arguments");
            return;
        }
        File currentDir = new File(curr_directory);
        File[] files = currentDir.listFiles();
        if (files != null) {
            Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
            for (File file : files) {
                System.out.print(file.getName() + "  ");
            }
            System.out.println();
        } else {
            System.out.println("The current directory has no content");
        }
    }
    
    public void ls_reversed() {
        File currentDir = new File(curr_directory);
        File[] files = currentDir.listFiles();
        if (files != null) {
            Arrays.sort(files, (f1, f2) -> f2.getName().compareTo(f1.getName()));
            for (File file : files) {
                System.out.print(file.getName() + "  ");
            }
            System.out.println();
        } else {
            System.out.println("The current directory has no content");
        }
    }
    
    public void mkdir() {
        for (String dir : parser.args) {
            File file = new File(dir);
            if (file.isAbsolute()) {
                 file = new File(dir);
                boolean state = file.mkdir();
                if (state) {
                    System.out.println("Directory created successfully");
                } else {
                    System.out.println(dir + " directory couldn't be created");
                }
            } else {
                dir = curr_directory + "\\" + dir;
                 file = new File(dir);
                boolean state = file.mkdir();
                if (state) {
                    System.out.println("Directory created successfully");
                } else {
                    System.out.println(dir + " directory couldn't be created");
                }
            }
        }
    }
    
    public void rmdir() {
        if (parser.args.length != 1) {
            System.out.println("Usage: rmdir <directory_path>");
            return;
        }
        
        String directoryPath = parser.args[0];
        File dir;
        
        if (directoryPath.equals("*")) {
            // Case 1: Remove all empty directories in the current directory
            dir = new File(curr_directory);
            removeEmptyDirectories(dir);
            return;
        } else {
            dir = new File(directoryPath);
            if (dir.isAbsolute()) {
                // Full path provided
                dir = new File(directoryPath);
            } else {
                // Short path provided, combine it with the current directory
                dir = new File(curr_directory + File.separator + directoryPath);
            }
        }
        
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("The directory does not exist or is not a directory");
            return;
        }
        
        if (isEmptyDirectory(dir)) {
            if (dir.delete()) {
                System.out.println("Directory removed successfully");
            } else {
                System.out.println("An error occurred while removing the directory");
            }
        } else {
            System.out.println("The directory is not empty and cannot be removed");
        }
    }
    
    private void removeEmptyDirectories(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] subDirs = directory.listFiles();
            if (subDirs != null) {
                for (File subDir : subDirs) {
                    if (subDir.isDirectory() && isEmptyDirectory(subDir)) {
                        if (subDir.delete()) {
                            System.out.println("Empty directory removed: " + subDir.getAbsolutePath());
                        } else {
                            System.out.println("An error occurred while removing the directory: " + subDir.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }
    
    private boolean isEmptyDirectory(File directory) {
        return directory.exists() && directory.isDirectory() && directory.list().length == 0;
    }
    
    public void touch() {
        if (parser.args.length != 1) {
            System.out.println("Usage: touch <file_path>");
            return;
        }
        
        String filePath = parser.args[0];
        File file = new File(filePath);
        if (file.isAbsolute()) {
            
             file = new File(filePath);
            
            try {
                if (file.createNewFile()) {
                    System.out.println("File created successfully");
                } else {
                    System.out.println("File already exists or couldn't be created");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while creating the file: " + e.getMessage());
            }
        } else {
            // If the input is a relative (short) path, combine it with the current directory
             file = new File(curr_directory + File.separator + filePath);
            
            try {
                if (file.createNewFile()) {
                    System.out.println("File created successfully");
                } else {
                    System.out.println("File already exists or couldn't be created");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while creating the file: " + e.getMessage());
            }
        }
    }
    
    public void cp() {
        if (parser.args.length == 2) {
            String sourcePath = parser.args[0];
            String destinationPath = parser.args[1];
            File sourceFile = new File(sourcePath);
            File destinationFile= new File(destinationPath);;
            
            if (sourceFile.isAbsolute()) {
                // Full path provided for the source file
                sourceFile = new File(sourcePath);
            } else {
                // Short path provided, combine it with the current directory for the source file
                sourceFile = new File(curr_directory + File.separator + sourcePath);
            }
            
            if (destinationFile.isAbsolute()) {
                // Full path provided for the destination file
                destinationFile = new File(destinationPath);
            } else {
                // Short path provided, combine it with the current directory for the destination file
                destinationFile = new File(curr_directory + File.separator + destinationPath);
            }
            
            try (InputStream in = new FileInputStream(sourceFile);
                 OutputStream out = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                
                System.out.println("File copied successfully.");
            } catch (IOException e) {
                System.err.println("Error copying the file: " + e.getMessage());
            }
        } else {
            System.out.println("Usage: cp <source_file> <destination_file>");
        }
    }
    
    public void cp_r() {
        if (parser.args.length != 3) {
            System.out.println("Usage: cp -r <source_directory> <destination_directory>");
            return;
        }
        
        String sourceDirectoryPath = parser.args[1];
        String destinationDirectoryPath = parser.args[2];
        File sourceDirectory= new File(sourceDirectoryPath);
        File destinationDirectory= new File(destinationDirectoryPath);
        
        if (sourceDirectory.isAbsolute()) {
            // Full path provided for the source directory
            sourceDirectory = new File(sourceDirectoryPath);
        } else {
            // Short path provided, combine it with the current directory for the source directory
            sourceDirectory = new File(curr_directory + File.separator + sourceDirectoryPath);
        }
        
        if (destinationDirectory.isAbsolute()) {
            // Full path provided for the destination directory
            destinationDirectory = new File(destinationDirectoryPath);
        } else {
            // Short path provided, combine it with the current directory for the destination directory
            destinationDirectory = new File(curr_directory + File.separator + destinationDirectoryPath);
        }
        
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            System.out.println("Source directory does not exist or is not a directory");
            return;
        }
        
        if (!destinationDirectory.exists() || !destinationDirectory.isDirectory()) {
            System.out.println("Destination directory does not exist or is not a directory");
            return;
        }
        
        String destinationPath = destinationDirectory + File.separator + sourceDirectory.getName();
        File destinationPathFile = new File(destinationPath);
        
        try {
            File finalSourceDirectory = sourceDirectory;
            Files.walk(sourceDirectory.toPath())
                    .forEach(source -> {
                        try {
                            Path dest = destinationPathFile.toPath().resolve(finalSourceDirectory.toPath().relativize(source));
                            Files.createDirectories(dest.getParent());
                            Files.copy(source, dest, StandardCopyOption.COPY_ATTRIBUTES);
                        } catch (IOException e) {
                            System.out.println("An error occurred while copying the directory: " + e.getMessage());
                        }
                    });
            System.out.println("Directory copied successfully");
        } catch (IOException e) {
            System.out.println("An error occurred while copying the directory: " + e.getMessage());
        }
    }
    
    public void cat() {
        String  myFile = parser.args[0];
        File f = new File(myFile);
        if(f.isAbsolute()){
            f = new File(myFile);
        } else {
            myFile = this.curr_directory+File.separator+this.parser.args[0];
            f = new File(myFile);
        }
        if (!f.canRead()) {
            System.out.println("Wrong Path..!");
            return;
        }
        else {
            String data = "";
            try {
                Scanner scanner = new Scanner(f);
                while (scanner.hasNextLine()){
                    data += scanner.nextLine();
                }
            } catch (FileNotFoundException var6) {
                var6.printStackTrace();
            }
            
            if (this.parser.args.length > 1 && this.parser.args[1]!= null) {
                f = new File( this.curr_directory+File.separator+this.parser.args[1]);
                if (!f.canRead()) {
                    System.out.println("Wrong Path..!");
                    return;
                }
                
                try {
                    Scanner scanner = new Scanner(f);
                    while (scanner.hasNextLine()){
                        data += scanner.nextLine();
                    }
                    data += '\n';
                } catch (FileNotFoundException var5) {
                    var5.printStackTrace();
                }
            }
            
            System.out.println(data);
        }
    }
    
    public void rm() {
        String s = parser.args[0];
        File f = new File(s);
        if(f.isAbsolute()){
            s = parser.args[0];
            f = new File(s);
        } else{
            s = this.curr_directory + File.separator + this.parser.args[0];
            f = new File(s);
        }
        if (f.delete()) {
            System.out.println("deleted successfully...!");
        } else {
            System.out.println("File not Found");
        }
        
    }
    
    public void getHistory(){
        history.remove(history.size()-1);
        for (String s : history) {
            System.out.println(s);
        }
    }
    
    public void exit_code() {
        System.out.println("Thank you for using our program");
        System.exit(0);
    }
    
    public void chooseCommandAction(String input) {
        Arrays.fill(parser.args, null);
        boolean execute =parser.parse(input);
        String commandName = parser.getCommandName();
        history.add(this.parser.commandName);
        if (Objects.equals(commandName, "exit")) {
            exit_code();
        } else if (Objects.equals(commandName, "echo")) {
            echo();
        } else if (Objects.equals(commandName, "pwd")) {
            pwd();
        } else if (Objects.equals(commandName, "cd")) {
            cd();
        } else if (Objects.equals(commandName, "ls")) {
            ls();
        } else if (Objects.equals(commandName, "mkdir")) {
            mkdir();
        }
        else if (Objects.equals(commandName, "rmdir")) {
            rmdir();
        }
        else if (Objects.equals(commandName, "touch")) {
            touch();
        } else if (Objects.equals(commandName, "cp") && Objects.equals(parser.args[0], "-r")) {
            cp_r();
        }else if (Objects.equals(commandName, "cp")) {
            cp();
        }else if (Objects.equals(this.parser.commandName, "rm")) {
            if (this.parser.args.length != 1) {
                System.out.println("Wrong Path..!!");
            } else {
                this.rm();
            }
        } else if (Objects.equals(this.parser.commandName, "cat")) {
            if (this.parser.args.length != 2 && this.parser.args.length != 1) {
                System.out.println("need at least one file Path");
            } else {
                this.cat();
            }
        } else if (Objects.equals(this.parser.commandName, "history")) {
            this.getHistory();
        }
        else {
            System.out.println("Wrong command");
            history.remove(history.size()-1);
        }
        
    }
}