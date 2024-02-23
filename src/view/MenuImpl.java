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
        System.out.println(table.render());
    }
}
