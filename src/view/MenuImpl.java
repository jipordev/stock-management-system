package view;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

public class MenuImpl implements Menu{
    @Override
    public void displayBanner() {
        System.out.println("""
                ###################################
                WELCOME TO 
                STOCK MANAGEMENT SYSTEM
                ###################################
                """);

        String banner =
                " ██████╗███████╗████████╗ █████╗ ██████╗     ███╗   ███╗██╗███╗   ██╗██╗    ██████╗ ██████╗  ██████╗      ██╗███████╗ ██████╗████████╗\n" +
                "██╔════╝██╔════╝╚══██╔══╝██╔══██╗██╔══██╗    ████╗ ████║██║████╗  ██║██║    ██╔══██╗██╔══██╗██╔═══██╗     ██║██╔════╝██╔════╝╚══██╔══╝\n" +
                "██║     ███████╗   ██║   ███████║██║  ██║    ██╔████╔██║██║██╔██╗ ██║██║    ██████╔╝██████╔╝██║   ██║     ██║█████╗  ██║        ██║   \n" +
                "██║     ╚════██║   ██║   ██╔══██║██║  ██║    ██║╚██╔╝██║██║██║╚██╗██║██║    ██╔═══╝ ██╔══██╗██║   ██║██   ██║██╔══╝  ██║        ██║   \n" +
                "╚██████╗███████║   ██║   ██║  ██║██████╔╝    ██║ ╚═╝ ██║██║██║ ╚████║██║    ██║     ██║  ██║╚██████╔╝╚█████╔╝███████╗╚██████╗   ██║   \n" +
                " ╚═════╝╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═════╝     ╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝╚═╝    ╚═╝     ╚═╝  ╚═╝ ╚═════╝  ╚════╝ ╚══════╝ ╚═════╝   ╚═╝   ";
        System.out.println(banner);
    }

    @Override
    public void displayMainMenu() {
        CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
        Table table = new Table(1, BorderStyle.UNICODE_BOX_DOUBLE_BORDER, ShownBorders.SURROUND);
        table.addCell("             Disp(l)ay | Rando(m) | (W)rite | (R)ead | (E)dit | (D)elete | (S)earch              ",cellStyle);
        table.addCell("             Set R(o)w | (C)ommit | Bac(k) up | Res(t)ore | (H)elp | E(x)it              ",cellStyle);
        System.out.println(table.render());;
    }

    @Override
    public void displayHelp() {
        System.out.println("# Help Instruction");
        Table table = new Table(1, BorderStyle.CLASSIC_COMPATIBLE_WIDE, ShownBorders.SURROUND);
        table.addCell("1.      Press       l : Display product as table");
        table.addCell("2.      Press       w : Create a new product");
        table.addCell("3.      Press       r : View product details by code");
        table.addCell("4       Press       e : Edit an existing product by code");
        table.addCell("5.      Press       d : Delete an existing product by code");
        table.addCell("6.      Press       s : Search an existing product by name");
        table.addCell("7.      Press       c : Commit transaction data");
        table.addCell("8.      Press       k : Backup data");
        table.addCell("9.      Press       t : Restore data");
        table.addCell("10.     Press       f : Navigate pagination to the last page");
        table.addCell("11.     Press       p : Navigate pagination to the previous page");
        table.addCell("12.     Press       n : Navigate pagination to the next page");
        table.addCell("13.     Press       1 : Navigate pagination to the first page");
        table.addCell("14.     Press       h : Help");
        table.addCell("15.     Press       b : Step Back of the Application");

        System.out.println(table.render());
    }
}
