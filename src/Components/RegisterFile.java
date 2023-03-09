package Components;

import java.util.ArrayList;

public class RegisterFile {

    public static class Register {
        public String name;
        public String Qi;

        public Register(int number) {
            name = "F" + number;
            Qi = "0";
        }

        @Override
        public String toString() {
            return "Register{" +
                    "name='" + name + '\'' +
                    ", Qi='" + Qi + '\'' +
                    '}';
        }
    }

    public ArrayList<Register> registers = new ArrayList<>();

    public RegisterFile() {
        for (int i=0; i<32; i++) {
            registers.add(new Register(i));
        }
    }

    public void print() {
        for (Register register : registers) {
            System.out.println(register);
        }
    }

    public static void main(String[] args) {
        RegisterFile registerFile = new RegisterFile();
        registerFile.print();
    }
}
