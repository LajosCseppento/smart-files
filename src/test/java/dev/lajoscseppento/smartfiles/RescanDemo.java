package dev.lajoscseppento.smartfiles;

import dev.lajoscseppento.smartfiles.scanner.DirectoryScannerCleanUp2020;

public class RescanDemo {

    public static void main(String[] args) throws Exception {
        DirectoryScannerCleanUp2020.main(args);
        ModelBuilderDemo.main(args);
    }

}
