package com.github.obsproth.prismkey.cli;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Console;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import com.github.obsproth.prismkey.common.generator.*;
import com.github.obsproth.prismkey.HashUtil;
import com.github.obsproth.prismkey.ServiceElement;

class PasswordMismatch extends RuntimeException {
    public PasswordMismatch() {
        super();
    }
}

class CLIAddConfig {
    public String name;
    public String lengthStr;
    public String allowSmallsStr = "False";
    public String allowCapsStr = "False";
    public String allowNumbersStr = "False";
    public String symbols = GeneratorV2.DEFAULT_SYMBOLS;
    public CLIAddConfig() {
        name = null;
        lengthStr = null;
    }
    public String getRecipeStr() {
        List<String> seedSet = new ArrayList<String>();
        if (allowSmallsStr.equals("True")) {
            seedSet.add("a-z");
        }
        if (allowCapsStr.equals("True")) {
            seedSet.add("A-Z");
        }
        if (allowNumbersStr.equals("True")) {
            seedSet.add("0-9");
        }
        seedSet.add(symbols);
        return "["+String.join("", seedSet)+"]";
    }
    public static CLIAddConfig fromArgument(String[] args, int startPosition) {
        CLIAddConfig config = new CLIAddConfig();
        config.name = args[startPosition];
        for(int j = startPosition + 1; j < args.length; j++) {
            if(args[j].equals("--allow-numbers")) {
                config.allowNumbersStr = "True";
            } else if(args[j].equals("--allow-caps")) {
                config.allowCapsStr = "True";
            } else if (args[j].equals("--allow-smalls")) {
                config.allowSmallsStr = "True";
            } else if (args[j].equals("--allow-alnums")) {
                config.allowNumbersStr = "True";
                config.allowSmallsStr = "True";
                config.allowCapsStr = "True";             
            } else if (args[j].equals("--symbols")) {
                config.symbols = args[j+1];
                j++;
            } else {
                config.lengthStr = args[j];    
            }
        }
        if (config.allowSmallsStr.equals("False") && config.allowCapsStr.equals("False") && config.allowNumbersStr.equals("False")) {
            config.allowNumbersStr = "True";
            config.allowSmallsStr = "True";
            config.allowCapsStr = "True";             
        }
        return config;
    }
}

public class PrismKeyCLI {
    
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
    //     show usage for PrismKey CLI
    public static void showUsage() {
        System.out.println("-- Usage --");
        System.out.println();
        System.out.println("\tjava -jar prismkey list\tshow available services");
        System.out.println("\tjava -jar prismkey generate servicename\tgenerate password");
        System.out.println("\tjava -jar prismkey add [servicename] [length]\tadd password to service");
        System.out.println();
        System.out.println("*--- (not Implemented) ---");
        System.out.println("\t<remove>\tdelete data from service");
    }
    //
    // input password manager contains retry
    //
    private static char[] passwordPrompt(String caption, AbstractGenerator generator, ServiceElement elem) {
        java.io.Console cons = System.console();
        char[] password = null;
        for (int j = 0; j < PASSWD_MAX_PROMPT; j++){
            password = cons.readPassword(caption);
            if (password == null) {
                return null;
            }
            if (generator.verifySeed(password, elem)) {
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

    private static String getBaseHashFromInputPassword(Console cons, List<String> config)  {
        char[] password1 = null, password2 = null;
        String baseHash = null;
        passwordCheck: while(baseHash == null){
            try {
                password1 = cons.readPassword("        Enter the master password of service:");
                password2 = cons.readPassword("Enter the master password of service (again):");
                if (!Arrays.equals(password1, password2)) {
                    throw new PasswordMismatch();
                }
                baseHash = GeneratorFactory.getLatestGenerator(config).getSeedDigestStr(password1);
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

    public static void doAdd(CLIAddConfig confArg) {
        ServiceTable tbl = null;
        try{
            tbl = new ServiceTable(DATA_FILE);
        } catch (IOException e)  {
            e.printStackTrace();
            System.exit(-1);
        }
        Console cons = System.console();
        String serviceName = confArg.name;
        // サービス名は予めコマンドライン引数で決めておくか、実行時に入力
        if (serviceName == null) {
            serviceName = cons.readLine("Enter ServiceName:");
        }
        int length = 16;
        if (confArg.lengthStr != null) {
            try{
                length = Integer.parseInt(confArg.lengthStr);
            } catch (NumberFormatException e) {
                System.out.println("length must be decimal values");
                return;
            }
        }
        String lengthStr = confArg.lengthStr;
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
        // 生成設定を表示する
        int algoVersion = com.github.obsproth.prismkey.PrismKey.ALGO_VERSION;
        System.out.println(String.format("Name: %s, Length: %d, Version: %d, Recipe: %s", serviceName, length, algoVersion, confArg.getRecipeStr()));
        // password生成設定
        List<String> config = new ArrayList<String>();
        config.add(confArg.allowNumbersStr);
        config.add(confArg.allowCapsStr);
        config.add(confArg.allowSmallsStr);
        config.add(confArg.symbols);
        // passwordを入力してbaseHashを取得
        String hash = getBaseHashFromInputPassword(cons, config);
        if (hash == null) {
            return;
        }
        tbl.add(serviceName, length, hash, algoVersion, config);
        System.out.println(String.format("Added Service %s; length:%d, Hash:%s...", serviceName, length, hash));
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
        char[] seedPassword = null;
        char[] password = null;
        AbstractGenerator generator = GeneratorFactory.getGenerator(elem); 
        try {
            // passwordを入力するプロンプト（3回まで可）
            System.out.println(generator.getClass().getName());
            seedPassword = passwordPrompt("input master password :", generator, elem);
            if(seedPassword != null) {
                password = generator.generate(seedPassword, elem);
                System.out.println(password);
            }
        } finally {
            if (seedPassword != null) { Arrays.fill(seedPassword, '\n'); }
            if (password != null) { Arrays.fill(password, '\n'); }            
        }
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
                CLIAddConfig confArg = CLIAddConfig.fromArgument(args, 1);
                doAdd(confArg);
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