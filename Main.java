package sample;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        String[] str;
        String fileInput = args[0];
        BufferedReader reader;
        try {
            Path path = Paths.get(fileInput);
            int lineCount = (int) Files.lines(path).count();
            str = new String[lineCount];
            reader = new BufferedReader(new FileReader(fileInput));
            for (int i = 0; i < lineCount; i++){
                str[i] = reader.readLine();
            }
            reader.close();
            for (int i = 0; i < str.length; i++){
                parsing(str[i], i+1);
            }
            System.out.println("\n\n" + (lineCount+1) + ": EOS");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static final Pattern checkNumber = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern checkLetter = Pattern.compile("[a-zA-Z]");
    private static final Pattern mulOp = Pattern.compile("[\\-*/]");
    private static final Pattern addOp = Pattern.compile("[+-]+");

    private static void parsing(String str, int lineCount){
        String[] parsedStr = str.split(" ");
        if(!(Objects.equals(Character.toString(parsedStr[parsedStr.length-1].charAt((parsedStr[parsedStr.length-1].length())-1)), ";"))){
            System.out.println("This line is not end with semicolon");
            return;
        }
        for(int i = 0; i < parsedStr.length; i++){
            //System.out.println(declareWhatIsThis(parsedStr[i]));
            if(i == parsedStr.length-1){
                if(parsedStr[i].length() <= 1){
                    System.out.println("Wrong Grammar");
                    return;
                }
                String[] x = parsedStr[i].split(";");
                parsedStr[i] = declareWhatIsThis(x[0]);
            }
            else
                parsedStr[i] = declareWhatIsThis(parsedStr[i]);
            if(Objects.equals(parsedStr[i], "null")){
                System.out.println("Empty or Wrong Grammar");
                return;
            }
        }
        if (checkGrammar(parsedStr)) {
            printSourceCode(str,parsedStr,lineCount);
            printSyntaxTree(str,parsedStr);
        }else {
            System.out.println("Wrong Grammar!!");
            return;
        }
    }

    private static String declareWhatIsThis(String str){
        if(str == null) return "null";
        if(Objects.equals(str, "read") || Objects.equals(str, "write")){
            return str;
        }
        if(checkLetter.matcher(String.valueOf(str.charAt(0))).matches()){
            for (int i = 0; i < str.length(); i++){
                if(!checkLetter.matcher(String.valueOf(str.charAt(i))).matches()){
                    return "null";
                }
            }
            return "id";
        }
        if(checkNumber.matcher(str).matches()){
            return "num";
        }
        if(Objects.equals(str, ":=")){
            return "assignop";
        }
        if(mulOp.matcher(str).matches()){
            return "mulop";
        }
        if(addOp.matcher(str).matches()){
            return "addop";
        }
        else if(Objects.equals(str, ";")){
            return "eos";
        }
        return "null";
    }

    private static boolean checkGrammar(String[] grm){
        if(Objects.equals(grm[0], "write") || Objects.equals(grm[0], "read")){
            if(grm.length != 2){
                return false;
            } if (Objects.equals(grm[1], "id")){
                return true;
            }
        }
        if(grm.length > 2 && Objects.equals(grm[0], "id") && Objects.equals(grm[1], "assignop") && (Objects.equals(grm[2], "id") || Objects.equals(grm[2], "num"))){
            if(grm.length == 3) return true;
            if(grm.length >= 5){
                if(Objects.equals(grm[3], "mulop") || Objects.equals(grm[3], "addop")){
                    if (Objects.equals(grm[4], "id") || Objects.equals(grm[4], "num")){
                        if(grm.length == 5) return true;
                        if(grm.length == 7){
                            if(Objects.equals(grm[5], "mulop") && Objects.equals(grm[3], "addop")){
                                return Objects.equals(grm[6], "id") || Objects.equals(grm[6], "num");
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static void printSourceCode(String str, String[] parsedStr, int lineCount){
        String[] newStr = str.split(" ");
        System.out.println("\n\n" + lineCount + ": " + str);
        for (int i = 0; i < newStr.length; i++) {
            if (newStr.length - 1 == i) {
                String[] semicolon = newStr[i].split(";");
                if(Objects.equals(parsedStr[i], "id"))  System.out.println("\t" + lineCount + ": ID, name= " + semicolon[0]);
                else if(Objects.equals(parsedStr[i], "num"))    System.out.println("\t" + lineCount + ": NUM, val= " + semicolon[0]);
                System.out.println("\t" + lineCount +": ;");
            }else {
                if(Objects.equals(parsedStr[i], "id"))  System.out.println("\t" + lineCount + ": ID, name= " + newStr[i]);
                else if(Objects.equals(parsedStr[i], "num"))    System.out.println("\t" + lineCount +": NUM, val= " + newStr[i]);
                else if(Objects.equals(parsedStr[i], "write") || Objects.equals(parsedStr[i], "read"))    System.out.println("\t" + lineCount + ": reserved word: " + newStr[i]);
                else System.out.println("\t" + lineCount +": " + newStr[i]);
            }
        }
    }

    private static void printSyntaxTree(String str, String[] parsedStr){
        String[] newStr = str.split(" ");
        String[] semicolon = newStr[newStr.length-1].split(";");
        int x = 1;
        System.out.println("\nSyntax Tree:");
        if(Objects.equals(parsedStr[0], "read")){
            System.out.println("Read: " + semicolon[0]);
            return;
        }
        if(Objects.equals(parsedStr[0], "write")){
            System.out.println("Write");
            System.out.println("\tId: " + semicolon[0]);
            return;
        }
        if(Objects.equals(parsedStr[1], "assignop")){
            System.out.println("Assign to: " + newStr[0]);
            if(newStr.length == 3){
                if(Objects.equals(parsedStr[2], "num")){
                    System.out.println("\tConst: " + semicolon[0]);
                }else if(Objects.equals(parsedStr[2], "id")){
                    System.out.println("\tId: " + semicolon[0]);
                }
            }
            else if(newStr.length == 5){
                System.out.println("\tOp: " + newStr[3]);
                if(Objects.equals(parsedStr[2], "num")){
                    System.out.println("\t\tConst: " + newStr[2]);
                }else if(Objects.equals(parsedStr[2], "id")){
                    System.out.println("\t\tId: " + newStr[2]);
                }
                if(Objects.equals(parsedStr[4], "num")){
                    System.out.println("\t\tConst: " + semicolon[0]);
                }else if(Objects.equals(parsedStr[4], "id")){
                    System.out.println("\t\tId: " + semicolon[0]);
                }

            }
            else if(newStr.length == 7){
                System.out.println("\tOp: " + newStr[3]);
                if(Objects.equals(parsedStr[2], "num")){
                    System.out.println("\t\tConst: " + newStr[2]);
                }else if(Objects.equals(parsedStr[2], "id")){
                    System.out.println("\t\tId: " + newStr[2]);
                }
                System.out.println("\t\tOp: " + newStr[5]);
                if(Objects.equals(parsedStr[4], "num")){
                    System.out.println("\t\t\tConst: " + newStr[4]);
                }else if(Objects.equals(parsedStr[4], "id")){
                    System.out.println("\t\t\tId: " + newStr[4]);
                }
                if(Objects.equals(parsedStr[6], "num")){
                    System.out.println("\t\t\tConst: " + semicolon[0]);
                }else if(Objects.equals(parsedStr[6], "id")){
                    System.out.println("\t\t\tId: " + semicolon[0]);
                }

            }
            /*for(int i = 2; i < newStr.length; i++){
                if(Objects.equals(parsedStr[i], "mulop") || Objects.equals(parsedStr[i], "addop")){
                    for (int j = 0; j < x; j++){
                        System.out.print("\t");
                    }
                    System.out.println("Op: " + newStr[i]);
                    x++;
                }
                if(Objects.equals(parsedStr[i], "id")){
                    for (int j = 0; j < x; j++){
                        System.out.print("\t");
                    }
                    System.out.println("Id: " + newStr[i]);
                }
                if(Objects.equals(parsedStr[i], "num")){
                    for (int j = 0; j < x; j++){
                        System.out.print("\t");
                    }
                    System.out.println("Const: " + newStr[i]);
                    x++;
                }
            }*/
        }
    }
}
