package com.github.obsproth.obspassword.cli;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import com.github.obsproth.obspassword.HashUtil;
import com.github.obsproth.obspassword.ServiceElement;

public class ObsPassword{
    
    public static String DATA_FILE = "data.csv";
    static int PASSWD_MAX_PROMPT = 3;
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
        CLITable view = new CLITable();
        view.addColumn("Name", "s", true);
        view.addColumn("Length", "d", 8);
        view.addColumn("Version", "d", 8);

        for(ServiceElement elem : tbl.getContents()) {
            view.addRow(
                elem.getServiceName(), new Integer(elem.getLength()), new Integer(elem.getVersion())
            );
        }
        view.print();
    }
    // 
    // DESCRIPTION:
    //     show usage for ObsPassword CLI
    public static void showUsage() {
        System.out.println("-- Usage --");
        System.out.println();
        System.out.println("\tjava -jar obspassword list\tshow available services");
        System.out.println("\tjava -jar obspassword generate servicename\tgenerate password");
        System.out.println();
        System.out.println("*--- (not Implemented) ---");
        System.out.println("\t<remove>\tdelete data from service");
        System.out.println("\t<add>\tadd password to service");
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
            if (elem.getBaseHash().equals(HashUtil.getBaseHashStr(password))) {
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
                if (args.length > 1) {
                    doGenerate(args[1]);
                } else {
                    System.out.println("error: too few argument");
                    showUsage();
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