import view.MenuImpl;

import java.time.LocalDate;

public class Test {
    static MenuImpl menu = new MenuImpl();
    public static void main(String[] args) {
        menu.displayBanner();
        menu.displayMainMenu();
        menu.displayHelp();
    }
}