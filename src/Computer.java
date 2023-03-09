import Components.*;

import java.util.ArrayList;

public class Computer {

    public static class Info {
        public String station;
        public int iIndex;
        public int eIndex;

        public Info(String station ,int iIndex, int eIndex) {
            this.station = station;
            this.iIndex = iIndex;
            this.eIndex = eIndex;
        }
    }

    public static class Instruction {
        public String type;
        public String i;
        public String j;
        public String k;
        public String Issue;
        public String Execution_Complete;
        public String Write_Result;

        public Instruction(String type, String firstParam, String secondParam, String thirdParam) {
            this.type = type;
            this.i = firstParam;
            this.j = secondParam;
            this.k = thirdParam;
        }

        @Override
        public String toString() {
            return "Instruction{" +
                    "type='" + type + '\'' +
                    ", i='" + i + '\'' +
                    ", j='" + j + '\'' +
                    ", k='" + k + '\'' +
                    ", Issue=" + Issue +
                    ", Execution_Complete=" + Execution_Complete +
                    ", Write_Result=" + Write_Result +
                    '}';
        }
    }

    public static int clock = 1;
    public static int addCycles = 2; // no. of cycles needed to execute FP addition
    public static int subCycles = 2; // no. of cycles needed to execute FP subtraction
    public static int mulCycles = 10; // no. of cycles needed to execute FP multiplication
    public static int divCycles = 40; // no. of cycles needed to execute FP division
    public static int loadCycles = 2; // no. of cycles needed to execute LD
    public static int storeCycles = 2; // no. of cycles needed to execute SD
    public static RegisterFile registerFile = new RegisterFile();
    public static AddStation addStation = new AddStation();
    public static MulStation mulStation = new MulStation();
    public static LoadBuffer loadBuffer = new LoadBuffer();
    public static StoreBuffer storeBuffer = new StoreBuffer();
    public static ArrayList<Instruction> program = new ArrayList<>();
    public static Info readyAtIssue = null;
    public static ArrayList<Info> toBeExecuted = new ArrayList<>();
    public static ArrayList<Info> justExecuted = new ArrayList<>();
    public static boolean adderBusy = false;
    public static boolean multiplierBusy = false;
    public static boolean memoryBusy = false;
    public static ArrayList<Info> toBeWritten = new ArrayList<>();
    public static boolean writerBusy = false;
    public static int finishedInstructions = 0;

    public static void issue() {
        boolean issued = false;
        for (int i=0; i<program.size(); i++) {
            if (program.get(i).Issue == null) {
                switch (program.get(i).type) {
                    case "ADD.D":
                    case "SUB.D":
                        for (AddStation.Entry entry : addStation.entries) {
                            if (entry.busy == 0) {
                                program.get(i).Issue = Integer.toString(clock);
                                entry.index = i;
                                entry.busy = 1;
                                entry.op = program.get(i).type;
                                if (registerFile.registers.get(Integer.parseInt(program.get(i).j.substring(1))).Qi.equals("0"))
                                    entry.Vj = "R[" + program.get(i).j + "]";
                                else
                                    entry.Qj = registerFile.registers.get(Integer.parseInt(program.get(i).j.substring(1))).Qi;
                                if (registerFile.registers.get(Integer.parseInt(program.get(i).k.substring(1))).Qi.equals("0"))
                                    entry.Vk = "R[" + program.get(i).k + "]";
                                else
                                    entry.Qk = registerFile.registers.get(Integer.parseInt(program.get(i).k.substring(1))).Qi;
                                registerFile.registers.get(Integer.parseInt(program.get(i).i.substring(1))).Qi = entry.name;
                                if (entry.Vj != null && entry.Vk != null) {
                                    entry.time = entry.op.equals("ADD.D") ? addCycles : subCycles;
                                    readyAtIssue = new Info("ADD", i, Integer.parseInt(entry.name.substring(1))-1);
                                }
                                issued = true;
                                break;
                            }
                        }
                        break;
                    case "MUL.D":
                    case "DIV.D":
                        for (MulStation.Entry entry : mulStation.entries) {
                            if (entry.busy == 0) {
                                program.get(i).Issue = Integer.toString(clock);
                                entry.index = i;
                                entry.busy = 1;
                                entry.op = program.get(i).type;
                                if (registerFile.registers.get(Integer.parseInt(program.get(i).j.substring(1))).Qi.equals("0"))
                                    entry.Vj = "R[" + program.get(i).j + "]";
                                else
                                    entry.Qj = registerFile.registers.get(Integer.parseInt(program.get(i).j.substring(1))).Qi;
                                if (registerFile.registers.get(Integer.parseInt(program.get(i).k.substring(1))).Qi.equals("0"))
                                    entry.Vk = "R[" + program.get(i).k + "]";
                                else
                                    entry.Qk = registerFile.registers.get(Integer.parseInt(program.get(i).k.substring(1))).Qi;
                                registerFile.registers.get(Integer.parseInt(program.get(i).i.substring(1))).Qi = entry.name;
                                if (entry.Vj != null && entry.Vk != null) {
                                    entry.time = entry.op.equals("MUL.D") ? mulCycles : divCycles;
                                    readyAtIssue = new Info("MUL", i, Integer.parseInt(entry.name.substring(1))-1);
                                }
                                issued = true;
                                break;
                            }
                        }
                        break;
                    case "L.D":
                        for (LoadBuffer.Entry entry : loadBuffer.entries) {
                            if (entry.busy == 0) {
                                program.get(i).Issue = Integer.toString(clock);
                                entry.index = i;
                                entry.busy = 1;
                                entry.time = loadCycles;
                                entry.address = program.get(i).j + "+" + "R[" + program.get(i).k + "]";
                                readyAtIssue = new Info("LD", i, Integer.parseInt(entry.name.substring(1))-1);
                                registerFile.registers.get(Integer.parseInt(program.get(i).i.substring(1))).Qi = entry.name;
                                issued = true;
                                break;
                            }
                        }
                        break;
                    case "S.D":
                        for (StoreBuffer.Entry entry : storeBuffer.entries) {
                            if (entry.busy == 0) {
                                program.get(i).Issue = Integer.toString(clock);
                                entry.index = i;
                                entry.busy = 1;
                                entry.time = storeCycles;
                                entry.address = program.get(i).j + "+" + "R[" + program.get(i).k + "]";
                                readyAtIssue = new Info("SD", i, Integer.parseInt(entry.name.substring(1))-1);
                                registerFile.registers.get(Integer.parseInt(program.get(i).i.substring(1))).Qi = entry.name;
                                issued = true;
                                break;
                            }
                        }
                        break;
                }
            }
            if (issued)
                break;
        }
    }

    public static void execute() {
        ArrayList<Info> toBeDeleted = new ArrayList<>();
        for (Info info : toBeExecuted) {
            switch (info.station) {
                case "ADD":
                    int opFullCycles = addStation.entries.get(info.eIndex).op.equals("ADD.D") ? addCycles : subCycles;
                    boolean firstExecute = false;
                    if (!adderBusy && addStation.entries.get(info.eIndex).time == opFullCycles) {
                        program.get(info.iIndex).Execution_Complete = clock + "...";
                        adderBusy = true;
                        firstExecute = true;
                    }
                    if (firstExecute || addStation.entries.get(info.eIndex).time != opFullCycles)
                        addStation.entries.get(info.eIndex).time--;
                    if (addStation.entries.get(info.eIndex).time == 0) {
                        program.get(info.iIndex).Execution_Complete += clock;
                        toBeDeleted.add(info);
                        justExecuted.add(info);
                    }
                    break;
                case "MUL":
                    int opFullCycles1 = mulStation.entries.get(info.eIndex).op.equals("MUL.D") ? mulCycles : divCycles;
                    boolean firstExecute1 = false;
                    if (!multiplierBusy && mulStation.entries.get(info.eIndex).time == opFullCycles1) {
                        program.get(info.iIndex).Execution_Complete = clock + "...";
                        multiplierBusy = true;
                        firstExecute1 = true;
                    }
                    if (firstExecute1 || mulStation.entries.get(info.eIndex).time != opFullCycles1)
                        mulStation.entries.get(info.eIndex).time--;
                    if (mulStation.entries.get(info.eIndex).time == 0) {
                        program.get(info.iIndex).Execution_Complete += clock;
                        toBeDeleted.add(info);
                        justExecuted.add(info);
                    }
                    break;
                case "LD":
                    boolean firstExecute2 = false;
                    if (!memoryBusy && loadBuffer.entries.get(info.eIndex).time == loadCycles) {
                        program.get(info.iIndex).Execution_Complete = clock + "...";
                        memoryBusy = true;
                        firstExecute2 = true;
                    }
                    if (firstExecute2 || loadBuffer.entries.get(info.eIndex).time != loadCycles)
                        loadBuffer.entries.get(info.eIndex).time--;
                    if (loadBuffer.entries.get(info.eIndex).time == 0) {
                        program.get(info.iIndex).Execution_Complete += clock;
                        toBeDeleted.add(info);
                        justExecuted.add(info);
                    }
                    break;
                case "SD":
                    boolean firstExecute3 = false;
                    if (!memoryBusy && storeBuffer.entries.get(info.eIndex).time == storeCycles) {
                        program.get(info.iIndex).Execution_Complete = clock + "...";
                        memoryBusy = true;
                        firstExecute3 = true;
                    }
                    if (firstExecute3 || storeBuffer.entries.get(info.eIndex).time != storeCycles)
                        storeBuffer.entries.get(info.eIndex).time--;
                    if (storeBuffer.entries.get(info.eIndex).time == 0) {
                        program.get(info.iIndex).Execution_Complete += clock;
                        storeBuffer.entries.get(info.eIndex).setDefault();
                        toBeDeleted.add(info);
                        finishedInstructions++;
                    }
                    break;
            }
        }
        toBeExecuted.removeAll(toBeDeleted);
    }

    public static void write() {
        ArrayList<Info> toBeDeleted = new ArrayList<>();
        for (Info info : toBeWritten) {
            String entryName = "";
            program.get(info.iIndex).Write_Result = Integer.toString(clock);
            switch (info.station) {
                case "ADD":
                    entryName = addStation.entries.get(info.eIndex).name;
                    addStation.entries.get(info.eIndex).setDefault();
                    break;
                case "MUL":
                    entryName = mulStation.entries.get(info.eIndex).name;
                    mulStation.entries.get(info.eIndex).setDefault();
                    break;
                case "LD":
                    entryName = loadBuffer.entries.get(info.eIndex).name;
                    loadBuffer.entries.get(info.eIndex).setDefault();
                    break;
            }
            for (RegisterFile.Register register : registerFile.registers) {
                if (register.Qi.equals(entryName))
                    register.Qi = "0";
            }
            for (AddStation.Entry entry : addStation.entries) {
                boolean edited = false;
                if (entry.Qj != null && entry.Qj.equals(entryName)) {
                    entry.Qj = null;
                    entry.Vj = "new value";
                    edited = true;
                }
                if (entry.Qk != null && entry.Qk.equals(entryName)) {
                    entry.Qk = null;
                    entry.Vk = "new value";
                    edited = true;
                }
                if (edited && entry.Vj != null && entry.Vk != null) {
                    entry.time = entry.op.equals("ADD.D") ? addCycles : subCycles;
                    toBeExecuted.add(new Info("ADD", entry.index, Integer.parseInt(entry.name.substring(1))-1));
                }
            }
            for (MulStation.Entry entry : mulStation.entries) {
                boolean edited = false;
                if (entry.Qj != null && entry.Qj.equals(entryName)) {
                    entry.Qj = null;
                    entry.Vj = "new value";
                    edited = true;
                }
                if (entry.Qk != null && entry.Qk.equals(entryName)) {
                    entry.Qk = null;
                    entry.Vk = "new value";
                    edited = true;
                }
                if (edited && entry.Vj != null && entry.Vk != null) {
                    entry.time = entry.op.equals("MUL.D") ? mulCycles : divCycles;
                    toBeExecuted.add(new Info("MUL", entry.index, Integer.parseInt(entry.name.substring(1))-1));
                }
            }
            writerBusy = true;
            toBeDeleted.add(info);
            finishedInstructions++;
            break;
        }
        toBeWritten.removeAll(toBeDeleted);
    }

    public static void view() {
        System.out.println("\n~~~~~~Clock " + clock + "~~~~~~\n");
        System.out.println(">> Instructions:");
        printProgram();
        System.out.println("\n>> Reg. file:");
        registerFile.print();
        System.out.println("\n>> Add station:");
        addStation.print();
        System.out.println("\n>> Mul station:");
        mulStation.print();
        System.out.println("\n>> Load buffers:");
        loadBuffer.print();
        System.out.println("\n>> Store buffers:");
        storeBuffer.print();
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public static void runProgram() {
        while (finishedInstructions < program.size()) {
            if (readyAtIssue != null) {
                toBeExecuted.add(readyAtIssue);
                readyAtIssue = null;
            }
            toBeWritten.addAll(justExecuted);
            justExecuted = new ArrayList<>();
            adderBusy = false;
            multiplierBusy = false;
            memoryBusy = false;
            writerBusy = false;
            issue();
            execute();
            write();
            view();
            clock++;
        }
    }

    public static void printProgram() {
        for (Instruction instruction : program) {
            System.out.println(instruction);
        }
    }

    public static void main(String[] args) {
        // Lecture example
        program.add(new Instruction("L.D", "F6", "32", "R2"));
        program.add(new Instruction("L.D", "F2", "44", "R3"));
        program.add(new Instruction("MUL.D", "F0", "F2", "F4"));
        program.add(new Instruction("SUB.D", "F8", "F6", "F2"));
        program.add(new Instruction("DIV.D", "F10", "F0", "F6"));
        program.add(new Instruction("ADD.D", "F6", "F8", "F2"));

        // Tutorial example
        /*program.add(new Instruction("MUL.D", "F3", "F1", "F2"));
        program.add(new Instruction("ADD.D", "F5", "F3", "F4"));
        program.add(new Instruction("ADD.D", "F7", "F2", "F6"));
        program.add(new Instruction("ADD.D", "F10", "F8", "F9"));
        program.add(new Instruction("MUL.D", "F11", "F7", "F10"));
        program.add(new Instruction("ADD.D", "F5", "F5", "F11"));*/
        runProgram();
    }

}
