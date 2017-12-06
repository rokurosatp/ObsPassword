package com.github.obsproth.obspassword.cli;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public static void doList() {
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
    // DESCRIPTION:
    //     show usage for ObsPassword CLI
    public static void showUsage() {
        System.out.println("-- Usage --");
        System.out.println();
        System.out.println("\tjava -jar obspassword list\tshow available services");
        System.out.println("\tjava -jar obspassword generate servicename\tgenerate password");
        System.out.println("\tjava -jar obspassword add [servicename] [length]\tadd password to service");
        System.out.println();
        System.out.println("*--- (not Implemented) ---");
        System.out.println("\t<remove>\tdelete data from service");
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

    public static void doAdd(String name, String lengthStr) {
        ServiceTable tbl = null;
        try{
            tbl = new ServiceTable(DATA_FILE);
        } catch (IOException e)  {
            e.printStackTrace();
            System.exit(-1);
        }
        Console cons = System.console();
        String serviceName = name;
        // サービス名は予めコマンドライン引数で決めておくか、実行時に入力
        if (serviceName == null) {
            serviceName = cons.readLine("Enter ServiceName:");
        }
        int length = 16;
        if (lengthStr != null) {
            try{
                length = Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                System.out.println("length must be decimal values");
                return;
            }
        }
        lengthPrompt: while(lengthStr == null) {
            try{
                lengthStr = cons.readLine("Enter Length(default 16):");
                if(lengthStr.length() != 0) {
                    length = Integer.parseInt(lengthStr);
                }
                if (length <= 0 || length >= 256) {
                    System.out.println("length must be in range 1 to 255");
                    continue lengthPrompt;
                }
            } catch (NumberFormatException e) {
                System.out.println("input decimal values");
                continue lengthPrompt;
            }
        }
        // passwordを入力してbaseHashを取得
        String hash = getBaseHashFromInputPassword(cons);
        if (hash == null) {
            return;
        }
        tbl.add(name, length, hash);
        System.out.println(String.format("Added Service %s; length:%d, Hash:%s...", name, length, hash));
        tbl.save(DATA_FILE);
    }
    // パスワードの生成部分 フローがあってるか怪しい
    // 
    //
    public static void doGenerate(String name) {
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

    public static void parseArguments(String [] args) {
        if (args.length > 0) {
            String operation = args[0];
            if(operation.equals("list")) {
                doList();
            } else if(operation.equals("generate")) {
                if (args.length > 1){
                    doGenerate(args[1]);
                } else {
                    System.out.println("error: too few argument");
                    showUsage();
                }
            } else if(operation.equals("add")) {
                if (args.length > 2) {
                    doAdd(args[1], args[2]);
                } else if (args.length > 1){
                    doAdd(args[1], null);
                } else {
                    doAdd(null, null);
                }
            } else {
                showUsage();
            }
        } else {
            System.out.println("error: command needs operation argument");
            showUsage();
        }
    }

    public static void main(String[] args) {
        //String hashstr = HashUtil.getBaseHashStr("PullRequest".toCharArray());
        //System.out.println("Hash :"+hashstr);
        parseArguments(args);
    }
}