package com.seyrancom.nlp;

import com.seyrancom.nlp.classifier.MNBClassifier;

import java.io.BufferedInputStream;
import java.util.Scanner;

public class Startup {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(new BufferedInputStream(System.in));

        System.out.println("---------------------------------");

        System.out.println("Enter your text, then double enter: ");
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            final String s = scanner.nextLine();
            if(s.isEmpty()){
                break;
            }
            stringBuilder.append("\n" + s);
        }

        System.out.print("---------------------------------");

        final App app = new App();
        app.selectClassifier(new MNBClassifier());
        System.out.println(app.run(stringBuilder.toString()));

        System.out.println("---------------------------------");

        System.out.println("Thank you!");
        System.out.println("Enter any key to complete: ");
        String key = scanner.next();

    }

}
