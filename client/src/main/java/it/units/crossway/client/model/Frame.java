package it.units.crossway.client.model;

import it.units.crossway.client.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Scanner;

@Component
public class Frame {

    private final Resource bannerResource;
    private String header;
    private String body;
    private String footer;
    public static final String IO_CHOOSE_NICKNAME = "Enter a nickname: ";
    public static final String IO_WAITING_FOR_OPPONENT_MOVE = "Waiting for opponent move...";
    public static final String IO_INSERT_VALID_PLACEMENT = "Insert a valid placement for your stone (e.g. 3,4)...";
    public static final String WIN_MESSAGE = "YOU WIN!!!";
    public static final String LOSE_MESSAGE = "YOU LOSE :(";

    public Frame() {
        bannerResource = new ClassPathResource("banner.txt");
        header = "";
        body = "";
        footer = "";
    }

    public void refresh() {
        clearConsole();
        printBanner();
        printHeader();
        printBody();
        printFooter();
    }

    public void printBanner() {
        try (Scanner scanner = new Scanner(bannerResource.getInputStream())) {
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void printHeader() {
        System.out.println(header);
    }

    public void printBody() {
        System.out.println(body);
    }

    public void printFooter() {
        System.out.println(footer);
    }

    public void reset() {
        resetHeader();
        resetBody();
        resetFooter();
    }

    public void resetHeader() {
        header = "";
    }

    public void resetBody() {
        body = "";
    }

    public void resetFooter() {
        footer = "";
    }

    public static void clearConsole() {
        try {
            String operatingSystem = System.getProperty("os.name"); //Check the current operating system
            if (operatingSystem.contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void refreshHeader(String input) {
        setHeader(input);
        refresh();
    }

    public void appendHeader(String input) {
        setHeader(header + System.lineSeparator() + input);
    }

    public void appendBody(String input) {
        setBody(body + System.lineSeparator() + input);
    }

    public void appendFooter(String input) {
        setFooter(footer + System.lineSeparator() + input);
    }

    public void appendHeaderAndRefresh(String input) {
        appendHeader(input);
        refresh();
    }

    public void appendBodyAndRefresh(String input) {
        appendBody(input);
        refresh();
    }

    public void appendFooterAndRefresh(String input) {
        appendFooter(input);
        refresh();
    }

    public void setHeader(String input) {
        header = input;
    }

    public void setBody(String input) {
        body = input;
    }

    public void setFooter(String input) {
        footer = input;
    }

    public void printNicknameMenu() {
        appendHeaderAndRefresh(IO_CHOOSE_NICKNAME);
    }

    public void printGameTypeMenu() {
        String gameTypeMenu = IOUtils.NEW_GAME_CHOICE + " -> Create a new game\n" +
                IOUtils.JOIN_GAME_CHOICE + " -> Join a game\n" +
                IOUtils.QUIT_GAME_CHOICE + " -> Quit";
        refreshHeader(gameTypeMenu);
    }
}
