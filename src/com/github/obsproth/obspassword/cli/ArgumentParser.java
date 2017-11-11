package com.github.obsproth.obspassword.cli;

import java.text.ParseException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;


class ArgumentConfigItem {
    public String name;
    public boolean positional;
    public int position;
    public boolean switched;
    public int nArguments;  // 0: switch or 次のoptionalまで無限
                            // 1: 一つ
                            // 2-: 2つ以上のリスト
    public boolean detectThenBreak;

    public ArgumentConfigItem(String name, boolean switched, int nArguments) {
        this.name = name;
        this.position = -1;
        this.positional = false;
        this.switched = switched;
        this.nArguments = nArguments;
        this.detectThenBreak = false;
    }

    public ArgumentConfigItem(String name, int position) {
        this.name = name;
        this.position = position;
        this.positional = true;
        this.switched = false;
        this.nArguments = 1;
        this.detectThenBreak = false;
    }

    public static ArgumentConfigItem getHelpArgument() {
        ArgumentConfigItem item = new ArgumentConfigItem("help", true, 0);
        item.detectThenBreak = true;
        return item;
    }
}

public interface ArgumentParser {
    void printUsage();
    void printUsage(String[] args);
    void printSubUsage(String subcommand);
    void printSubUsage(String[] args, String subcommand);
    Argument parse(String[] args) throws ParseException;
    void setDescription(String value);
    String getDescription();
}

class BundledArgumentParser implements ArgumentParser {
    Map<String, ArgumentConfigItem> optionals;
    List<String> optionalKeyList;    
    Map<String, String> aliasMap;
    List<ArgumentParser> subparsers;
    Map<String, ArgumentParser> subparserMap; // 上と同一のものを参照しているが、名前から取得するためのもの
    String bundleName;                          // サブパーサの属性名を入れる(operation category等)
    String description;

    public BundledArgumentParser(String bundleName) {
        this.optionals = new HashMap<String, ArgumentConfigItem>();
        this.aliasMap = new HashMap<String, String>();
        this.optionalKeyList = new ArrayList<String>();
        this.subparsers = new ArrayList<ArgumentParser>();
        this.subparserMap = new HashMap<String, ArgumentParser>();
        this.bundleName = bundleName;
        this.description = "";
        this.addOptionalArgument(ArgumentConfigItem.getHelpArgument());
        this.addAlias("h", "help");
    }

    void addOptionalArgument(ArgumentConfigItem item) {
        this.optionals.put(item.name, item);
        this.optionalKeyList.add(item.name);
    }

    public void addSubparser(String name, ArgumentParser subparser) {
        this.subparsers.add(subparser);
        this.subparserMap.put(name, subparser);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void addArgument(String name, boolean switched, int nArguments) {
        this.optionalKeyList.add(name);
        this.optionals.put(name, new ArgumentConfigItem(name, switched, nArguments));
    }

    public void addAlias(String name, String destination) {
        this.aliasMap.put(name, destination);
    }

    public void printUsage(String[] args) {
        this.printSubUsage(args, null);
    }
    public void printUsage() {
        this.printSubUsage(null);
    }

    public void printSubUsage(String[] args, String subcommands) {
        int i = 0;
        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                String argName = arg.substring(1);
                if (!this.optionals.containsKey(argName) && !this.aliasMap.containsKey(argName)) {
                    this.printSubUsage(subcommands);
                    return;
                }
            } else {
                if (!this.subparserMap.containsKey(arg)) {
                    this.printSubUsage(subcommands);
                    return;
                }
                String[] subArgs = new String[args.length-i];
                for(int j = i; j < args.length; j++){
                    subArgs[j-i] = args[j];
                }
                String subcmd = arg;
                if(subcommands != null) {
                    subcmd = subcommands + arg;
                }
                this.subparserMap.get(arg).printSubUsage(subArgs, subcmd);
                return;
            }
            i++;
        }
        this.printSubUsage(subcommands);
    }
    public void printSubUsage(String subcommands) {
        String path = System.getProperty("java.class.path");
        String name = (new File(path)).getName();
        String prefix = String.format("java -jar %s", name);
        if(subcommands != null) {
            prefix += " " + subcommands;
        }
        String command = prefix;
        Iterator<Map.Entry<String, ArgumentConfigItem>> iter = this.optionals.entrySet().iterator();
        Set<ArgumentConfigItem> renderedItems = new HashSet<ArgumentConfigItem>();
        for(int i = 0; i < this.optionals.size(); i++) {
            Map.Entry<String, ArgumentConfigItem> item = iter.next();
            if(renderedItems.contains(item.getValue())) {
                continue;
            }
            renderedItems.add(item.getValue());
            String opt = "-"+item.getKey();
            String vfield = "";
            if (!item.getValue().switched) {
                vfield = item.getValue().name.toUpperCase();
            }
            command += String.format(" [%s %s]", opt, vfield);
        }
        command += String.format(" %s", this.bundleName);
        System.out.println(this.description);
        System.out.println("Usage:");
        System.out.println("\t"+command);
        System.out.println(String.format("Set of %s:", this.bundleName));
        for(Iterator<String> i = this.subparserMap.keySet().iterator(); i.hasNext();) {
            String parserName = i.next();
            System.out.println(String.format("\t%16s: %s", parserName, this.subparserMap.get(parserName).getDescription()));    
        }
    }

    public Argument parse(String[] args) throws ParseException {
        int iPositional = -1;
        int iArguments = -1;
        ArgumentConfigItem current = null;
        ArgumentConfigItem currentPositional = current;
        Argument result = new Argument();
        int i = 0;
        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                String argName = arg.substring(1);
                if (!this.optionals.containsKey(argName) && !this.aliasMap.containsKey(argName)) {
                    throw new ParseException("the program has no argument of \"" + arg + "\"", i);
                }
                if(this.aliasMap.containsKey(argName)){
                    current = this.optionals.get(this.aliasMap.get(argName));
                } else {
                    current = this.optionals.get(argName);
                }
                if (current.switched) {
                    // Switch パラメータ
                    result.options.put(current.name, new Boolean(true));
                    if (current.detectThenBreak) {
                        return result;
                    }
                    iArguments = -1;
                    current = currentPositional;
                }
            } else if (current != null) {
                if (current.nArguments > 1 && iArguments < 0) {
                    result.options.put(current.name, new ArrayList<String>());
                    ((List<String>)result.get(current.name)).add(arg);
                    iArguments = 1;
                    current = currentPositional;
                } else if (iArguments > 0) {
                    ((List<String>)result.get(current.name)).add(arg);
                    iArguments++;
                    if (iArguments >= current.nArguments) {
                        iArguments = -1;
                        current = currentPositional;
                    }
                } else {
                    // 1の時
                    result.options.put(current.name, arg);
                }
            } else {
                if (!this.subparserMap.containsKey(arg)) {
                    throw new ParseException(String.format("Invalid %s: \"%s\"", this.bundleName, arg), i);
                }
                result.options.put(this.bundleName, arg);
                String[] subArgs = new String[args.length-i-1];
                for(int j = i+1; j < args.length; j++){
                    subArgs[j-i-1] = args[j];
                }
                Argument subresult = this.subparserMap.get(arg).parse(subArgs);
                result.options.putAll(subresult.options);
                return result;
            }
            i++;
        }
        throw new ParseException(String.format("cmdline needs %s", this.bundleName), i);
    }
}
//TODO: 共通部分が多いBundledとの統合
class NormalArgumentParser  implements ArgumentParser {
    Map<String, ArgumentConfigItem> optionals;
    List<String> optionalKeyList;
    Map<String, String> aliasMap;
    List<ArgumentConfigItem> positionals;
    String description;

    public NormalArgumentParser() {
        this.optionals = new HashMap<String, ArgumentConfigItem>();
        this.optionalKeyList = new ArrayList<String>();
        this.aliasMap = new HashMap<String, String>();
        this.positionals = new ArrayList<ArgumentConfigItem>();
        this.addOptionalArgument(ArgumentConfigItem.getHelpArgument());
        this.addAlias("h", "help");
    }

    void addOptionalArgument(ArgumentConfigItem item) {
        this.optionalKeyList.add(item.name);
        this.optionals.put(item.name, item);
    }

    public void addArgument(String name, boolean switched, int nArguments) {
        this.optionalKeyList.add(name);
        this.optionals.put(name, new ArgumentConfigItem(name, switched, nArguments));
    }

    public void addArgument(String name, int position) {
        this.positionals.add(new ArgumentConfigItem(name, position));
    }

    public void addAlias(String name, String destination) {
        this.aliasMap.put(name, destination);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
    public void printSubUsage(String[] args, String subcommands) {
        this.printSubUsage(subcommands);
    }
    public void printUsage(String[] args) {
        this.printSubUsage(args, null);
    }
    public void printUsage() {
        this.printSubUsage(null);
    }
    public void printSubUsage(String subcommands) {
        String path = System.getProperty("java.class.path");
        String name = (new File(path)).getName();
        String prefix = String.format("java -jar %s", name);
        if(subcommands != null) {
            prefix += " " + subcommands;
        }
        String command = prefix;
        Iterator<Map.Entry<String, ArgumentConfigItem>> iter = this.optionals.entrySet().iterator();
        Set<ArgumentConfigItem> renderedItems = new HashSet<ArgumentConfigItem>();
        for(int i = 0; i < this.optionals.size(); i++) {
            Map.Entry<String, ArgumentConfigItem> item = iter.next();
            if(renderedItems.contains(item.getValue())) {
                continue;
            }
            renderedItems.add(item.getValue());
            String opt = "-"+item.getKey();
            String vfield = "";
            if (!item.getValue().switched) {
                vfield = item.getValue().name.toUpperCase();
            }
            command += String.format(" [%s %s]", opt, vfield);
        }
        for(int i = 0; i < this.positionals.size(); i++) {
            command += String.format(" %s", this.positionals.get(i).name);
        }
        System.out.println(this.description);
        System.out.println("Usage:");
        System.out.println("\t"+command);
        System.out.println("List of options:");
        for(String key : this.optionalKeyList) {
            ArgumentConfigItem arg = this.optionals.get(key);
            System.out.println("\t"+arg.name+": "+"");
        }
    }

    public Argument parse(String[] args) throws ParseException {
        int iPositional = -1;
        int iArguments = -1;
        ListIterator<ArgumentConfigItem> iter = this.positionals.listIterator();
        ArgumentConfigItem current = null;
        if(iter.hasNext()) {
            current = iter.next();
        } else {
            current = null;
        }
        ArgumentConfigItem currentPositional = current;
        Argument result = new Argument();
        int i = 0;
        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                String argName = arg.substring(1);
                if (!this.optionals.containsKey(argName) && !this.aliasMap.containsKey(argName)) {
                    throw new ParseException("the program has no argument of \"" + arg + "\"", i);
                }
                if(this.aliasMap.containsKey(argName)) {
                    current = this.optionals.get(this.aliasMap.get(argName));
                } else {
                    current = this.optionals.get(argName);
                }
                if (current.switched) {
                    // Switch パラメータ
                    result.options.put(current.name, new Boolean(true));
                    if (current.detectThenBreak) {
                        return result;
                    }
                    iArguments = -1;
                    current = currentPositional;
                }
            } else if (current != null) {
                if (current.positional){
                    result.options.put(current.name, arg);
                    if (iter.hasNext()) {
                        current = iter.next(); 
                    } else {
                        current = null;
                    }
                } else if (current.nArguments > 1 && iArguments < 0) {
                    result.options.put(current.name, new ArrayList<String>());
                    ((List<String>)result.get(current.name)).add(arg);
                    iArguments = 1;
                    current = currentPositional;
                } else if (iArguments > 0) {
                    ((List<String>)result.get(current.name)).add(arg);
                    iArguments++;
                    if (iArguments >= current.nArguments) {
                        iArguments = -1;
                        current = currentPositional;
                    }
                } else {
                    // 1の時
                    result.options.put(current.name, arg);
                }
            } else {
                throw new ParseException("no space for \"" + arg + "\"", i);
            }
            i++;
        }
        i = 0;
        for(iter = this.positionals.listIterator(); iter.hasNext(); ) {
            ArgumentConfigItem item = iter.next();
            if(!result.containsKey(item.name)) {
                throw new ParseException(String.format("positional argument \"%s\" is required", item.name), i);
            }
            i++;
        }
        return result;
    }
}
