package com.github.obsproth.obspassword.cli;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.io.Console;
import java.util.Arrays;
import java.util.Base64;
import com.github.obsproth.obspassword.HashUtil;
import com.github.obsproth.obspassword.ServiceElement;

class PasswordMismatch extends RuntimeException {
    public PasswordMismatch() {
        super();
    }
}

public class ObsPassword{
    
    public static String DATA_FILE = "data.csv";
    static int PASSWD_MAX_PROMPT = 3;
    private static void showServiceList(ServiceTable tbl) {
        CLITable view = new CLITable();
        view.addColumn("Name", "s", true);
        view.addColumn("Length", "d", 8);
        view.addColumn("Version", "d", 8);
        for(ServiceElement elem : tbl.getContents()) {
            view.addRow(
                elem.getServiceName(),
                new Integer(elem.getLength()),
                new Integer(elem.getVersion())
            );
        }
        view.print();
    }
    // 
    // DESCRIPTION:
    //     show table contents
    public static void doList(Argument args) {
        ServiceTable tbl = null;
        try{
            tbl = new ServiceTable(DATA_FILE);
        } catch (FileNotFoundException e) {
			tbl = new ServiceTable();
            tbl.save(DATA_FILE);
        } catch (IOException e)  {
            e.printStackTrace();
            return;
        }
        if (tbl.size() > 0) {
            showServiceList(tbl);
        } else {
            System.out.println("* The table has no service.");
        }
    }
    //
    // input password manager contains retry
    //
    private static char[] passwordPrompt(String caption, ServiceElement elem) {
        java.io.Console cons = System.console();
        char[] password = null;
        for (int j = 0; j < PASSWD_MAX_PROMPT; j++){
            password = cons.readPassword(caption);
            if (password == null) {
                return null;
            }
            if (elem.getBaseHash().equals(HashUtil.getBaseHashStr(password, false))) {
                return password;
            }
            for(int i = 0; i < password.length; i++) {
                password[i] = '\n';
            }
            if (j < PASSWD_MAX_PROMPT - 1) {
                System.out.println("Sorry, try again!");
            }
        }
        return null;
    }

    private static String getBaseHashFromInputPassword(Console cons) {
        char[] password1 = null, password2 = null;
        String baseHash = null;
        passwordCheck: while(baseHash == null){
            try {
                password1 = cons.readPassword("        Enter the master password of service:");
                password2 = cons.readPassword("Enter the master password of service (again):");
                if (!Arrays.equals(password1, password2)) {
                    throw new PasswordMismatch();
                }
                baseHash = HashUtil.getBaseHashStr(password1);
            } catch (PasswordMismatch e) {
                System.out.println("Password mismatched! try again.");
                continue passwordCheck;
            } finally {
                if(password1 != null) {
                    for(int i = 0; i < password1.length; i++) {password1[i] = '\n';}
                }
                if(password2 != null) {
                    for(int i = 0; i < password2.length; i++) {password2[i] = '\n';}
                }
            }
        }
        return baseHash;
    }

    public static void doAdd(Argument args) {
        ServiceTable tbl = null;
        try{
            tbl = new ServiceTable(DATA_FILE);
        } catch (IOException e)  {
            e.printStackTrace();
            System.exit(-1);
        }
        Console cons = System.console();
        String serviceName = args.getString("NAME");
        int length = 16;
        try{
            length = args.getInteger("LENGTH");
            if (length <= 0 || length >= 256) {
                System.out.println("length must be in range 1 to 255");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("length must be decimal values");
            return;
        }
        // passwordを入力してbaseHashを取得
        String hash = getBaseHashFromInputPassword(cons);
        if (hash == null) {
            return;
        }
        tbl.add(serviceName, length, hash);
        System.out.println(String.format("Added Service %s; length:%d, Hash:%s...", serviceName, length, hash));
        tbl.save(DATA_FILE);
    }
    // パスワードの生成部分 フローがあってるか怪しい
    // 
    //
    public static void doGenerate(Argument args) {
        String name = args.getString("NAME");
        ServiceTable tbl = null;
        try{
            tbl = new ServiceTable(DATA_FILE);
        } catch (IOException e)  {
            e.printStackTrace();
            System.exit(-1);
        }
        ServiceElement elem = tbl.get(name);
        if (elem == null) {
            System.out.println(String.format("service %s was not found", name));
            return;
        }
        byte[] hash = null;
        char[] password = new char[1];
        try {
            // passwordを入力するプロンプト（3回まで可）
            password = passwordPrompt("input master password :", elem);
            if (password != null) {
                hash = HashUtil.calcHash(password, elem.getServiceName(), elem.getLength());
            }
        } finally {
            if (password == null) {
                return;
            }
            // passwordPromptでpasswordが出た時の消去
            for(int i = 0; i < password.length; i++) {
                password[i] = '\n';
            }
        }
        if(hash == null) {
            return;
        }
        String passwordStr = Base64.getEncoder().encodeToString(hash).substring(0, elem.getLength());
        System.out.println(passwordStr);
    }

    
    private static ArgumentParser makeArgumentParser() {
        BundledArgumentParser parser = new BundledArgumentParser("operation");
        parser.setDescription("ObsPassword");
        //options of list command
        NormalArgumentParser listparser = new NormalArgumentParser();
        listparser.setDescription("list services.");
        listparser.addArgument("human-readable", true, 0);
        listparser.addArgument("pattern", false, 1);
        parser.addSubparser("list", listparser);        
        //options of add command
        NormalArgumentParser addParser = new NormalArgumentParser();
        addParser.setDescription("add service and hash to service table.(needs password input)");
        addParser.addArgument("human-readable", true, 0);
        addParser.addArgument("NAME", 0);
        addParser.addArgument("LENGTH", 1);
        parser.addSubparser("add", addParser);

        NormalArgumentParser generateParser = new NormalArgumentParser();
        generateParser.setDescription("generate password for selected service.(needs master password input)");
        generateParser.addArgument("NAME", 0);
        parser.addSubparser("generate", generateParser);

        //NormalArgumentParser deleteParser = new NormalArgumentParser();
        //deleteParser.setDescription("delete password entry of selected service.");
        //deleteParser.addArgument("NAME", 0);
        //parser.addSubparser("delete", deleteParser);
        return parser;
    }

    public static Argument parseArguments(String[] strArgs) {
        ArgumentParser parser = makeArgumentParser();
        Argument args = null;
        try {
            args = parser.parse(strArgs);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            parser.printUsage(strArgs);
            return null;
        }
        if (args.containsKey("help")) {
            parser.printUsage(strArgs);
            return null;
        } else if (args.containsKey("version")) {
            System.out.println("ObsPassword Version ...");
            return null;
        }
        System.out.println(args.options.toString());
        return args;
    }

    public static void main(String[] args) {
        Argument parsedArgs = parseArguments(args);
        if(parsedArgs != null) {
            String operation = parsedArgs.getString("operation");
            if(operation.equals("list")) {
                doList(parsedArgs);
            } else if (operation.equals("generate")) {
                doGenerate(parsedArgs);
            } else if (operation.equals("add")) {
                doAdd(parsedArgs);
            }
        }
    }
}